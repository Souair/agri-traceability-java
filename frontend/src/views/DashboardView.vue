<template>
  <section class="page-grid">
    <div class="card span-2 glass">
      <div class="section-title">
        <h2>ECharts 数据统计</h2>
        <button class="btn btn-secondary" @click="loadData" :disabled="loading">{{ loading ? '刷新中...' : '刷新图表' }}</button>
      </div>
      <p class="hint">三张核心图：产品批次数量柱状图、质检占比饼图、近6个月批次趋势折线图。</p>
      <p v-if="error" class="error">{{ error }}</p>
    </div>

    <div class="card stat-mini">
      <span>批次总量</span>
      <strong>{{ totalBatches }}</strong>
    </div>
    <div class="card stat-mini">
      <span>质检记录</span>
      <strong>{{ totalQuality }}</strong>
    </div>

    <div class="card span-2">
      <h3>柱状图：各类产品批次数量统计</h3>
      <div ref="barRef" class="chart-box"></div>
    </div>

    <div class="card">
      <h3>饼图：质检合格 / 不合格占比</h3>
      <div ref="pieRef" class="chart-box"></div>
    </div>

    <div class="card">
      <h3>折线图：近 6 个月生产批次趋势</h3>
      <div ref="lineRef" class="chart-box"></div>
    </div>
  </section>
</template>

<script setup>
import * as echarts from 'echarts'
import { computed, nextTick, onBeforeUnmount, onMounted, ref } from 'vue'
import { apiGet } from '../api/client'

const loading = ref(false)
const error = ref('')

const productBatchStats = ref({})
const qualityRatioStats = ref({ PASS: 0, FAIL: 0 })
const monthlyStats = ref([])

const barRef = ref(null)
const pieRef = ref(null)
const lineRef = ref(null)

let barChart
let pieChart
let lineChart

const totalBatches = computed(() => Object.values(productBatchStats.value).reduce((sum, value) => sum + Number(value || 0), 0))
const totalQuality = computed(() => Number(qualityRatioStats.value.PASS || 0) + Number(qualityRatioStats.value.FAIL || 0))

async function loadData() {
  loading.value = true
  error.value = ''
  try {
    const [p, q, m] = await Promise.all([
      apiGet('/stats/product-batches'),
      apiGet('/stats/quality-ratio'),
      apiGet('/stats/monthly-batches?months=6')
    ])

    productBatchStats.value = p
    qualityRatioStats.value = q
    monthlyStats.value = m

    await nextTick()
    renderCharts()
  } catch (err) {
    error.value = err.message || '读取统计失败'
  } finally {
    loading.value = false
  }
}

function initCharts() {
  if (barRef.value && !barChart) {
    barChart = echarts.init(barRef.value)
  }
  if (pieRef.value && !pieChart) {
    pieChart = echarts.init(pieRef.value)
  }
  if (lineRef.value && !lineChart) {
    lineChart = echarts.init(lineRef.value)
  }
}

function renderCharts() {
  initCharts()

  const names = Object.keys(productBatchStats.value)
  const counts = names.map((name) => Number(productBatchStats.value[name] || 0))

  barChart?.setOption({
    backgroundColor: 'transparent',
    tooltip: { trigger: 'axis' },
    xAxis: {
      type: 'category',
      data: names,
      axisLabel: { color: '#d8f5e7' }
    },
    yAxis: {
      type: 'value',
      axisLabel: { color: '#d8f5e7' },
      splitLine: { lineStyle: { color: 'rgba(255,255,255,0.12)' } }
    },
    series: [
      {
        type: 'bar',
        data: counts,
        itemStyle: {
          borderRadius: [6, 6, 0, 0],
          color: '#42d58b'
        }
      }
    ]
  })

  pieChart?.setOption({
    tooltip: { trigger: 'item' },
    legend: {
      bottom: 0,
      textStyle: { color: '#d8f5e7' }
    },
    series: [
      {
        type: 'pie',
        radius: ['45%', '72%'],
        avoidLabelOverlap: false,
        label: { color: '#d8f5e7' },
        data: [
          { value: Number(qualityRatioStats.value.PASS || 0), name: '合格', itemStyle: { color: '#5be58f' } },
          { value: Number(qualityRatioStats.value.FAIL || 0), name: '不合格', itemStyle: { color: '#ff8b8b' } }
        ]
      }
    ]
  })

  lineChart?.setOption({
    tooltip: { trigger: 'axis' },
    xAxis: {
      type: 'category',
      data: monthlyStats.value.map((item) => item.month),
      axisLabel: { color: '#d8f5e7' }
    },
    yAxis: {
      type: 'value',
      axisLabel: { color: '#d8f5e7' },
      splitLine: { lineStyle: { color: 'rgba(255,255,255,0.12)' } }
    },
    series: [
      {
        type: 'line',
        smooth: true,
        data: monthlyStats.value.map((item) => Number(item.count || 0)),
        lineStyle: { color: '#82ffd2', width: 3 },
        itemStyle: { color: '#82ffd2' },
        areaStyle: {
          color: 'rgba(130, 255, 210, 0.25)'
        }
      }
    ]
  })
}

function resizeCharts() {
  barChart?.resize()
  pieChart?.resize()
  lineChart?.resize()
}

onMounted(async () => {
  window.addEventListener('resize', resizeCharts)
  await loadData()
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', resizeCharts)
  barChart?.dispose()
  pieChart?.dispose()
  lineChart?.dispose()
})
</script>
