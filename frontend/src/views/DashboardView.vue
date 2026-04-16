<template>
  <section class="page-grid">
    <div class="card span-2 glass dashboard-hero">
      <div>
        <span class="pill">运营概览</span>
        <h2>农产品全流程追溯总览</h2>
        <p class="hint">聚合批次、质检与近 6 个月产出趋势，帮助平台快速掌握运行状态。</p>
      </div>
      <div class="actions-inline">
        <button class="btn btn-secondary" @click="loadData" :disabled="loading">{{ loading ? '刷新中...' : '刷新数据' }}</button>
      </div>
    </div>

    <div class="card stat-mini">
      <span>批次总量</span>
      <strong>{{ totalBatches }}</strong>
      <small class="hint">当前平台已登记的产品批次数</small>
    </div>
    <div class="card stat-mini">
      <span>质检记录</span>
      <strong>{{ totalQuality }}</strong>
      <small class="hint">已纳入平台归档的质检条目</small>
    </div>
    <div class="card stat-mini">
      <span>合格率</span>
      <strong>{{ passRate }}%</strong>
      <small class="hint">按现有质检记录自动统计</small>
    </div>
    <div class="card stat-mini">
      <span>近 6 月峰值</span>
      <strong>{{ peakMonth.count }}</strong>
      <small class="hint">峰值月份：{{ peakMonth.month || '—' }}</small>
    </div>


    <div class="card span-2">
      <div class="section-title">
        <h3>各类产品批次数量统计</h3>
        <span class="hint">用于观察平台当前品类分布</span>
      </div>
      <div ref="barRef" class="chart-box"></div>
    </div>

    <div class="card">
      <div class="section-title">
        <h3>质检结果占比</h3>
        <span class="hint">合格 / 不合格</span>
      </div>
      <div ref="pieRef" class="chart-box"></div>
    </div>

    <div class="card">
      <div class="section-title">
        <h3>近 6 个月批次趋势</h3>
        <span class="hint">反映阶段性产出变化</span>
      </div>
      <div ref="lineRef" class="chart-box"></div>
    </div>

    <p v-if="error" class="error span-2">{{ error }}</p>
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
let themeObserver

const totalBatches = computed(() => Object.values(productBatchStats.value).reduce((sum, value) => sum + Number(value || 0), 0))
const totalQuality = computed(() => Number(qualityRatioStats.value.PASS || 0) + Number(qualityRatioStats.value.FAIL || 0))
const passRate = computed(() => {
  if (!totalQuality.value) return 0
  return Math.round((Number(qualityRatioStats.value.PASS || 0) / totalQuality.value) * 100)
})
const peakMonth = computed(() => {
  if (!monthlyStats.value.length) {
    return { month: '', count: 0 }
  }

  return monthlyStats.value.reduce((max, item) => {
    return Number(item.count || 0) > Number(max.count || 0) ? item : max
  }, monthlyStats.value[0])
})

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

function themeVars() {
  const style = getComputedStyle(document.documentElement)
  return {
    textMain: style.getPropertyValue('--text-main').trim() || '#e5eefb',
    textSub: style.getPropertyValue('--text-sub').trim() || '#95a6c6',
    border: style.getPropertyValue('--border').trim() || 'rgba(148, 163, 184, 0.18)',
    primary: style.getPropertyValue('--primary').trim() || '#7dd3a8',
    primaryStrong: style.getPropertyValue('--primary-strong').trim() || '#4fd18b',
    danger: style.getPropertyValue('--danger').trim() || '#f59c9c'
  }
}

function renderCharts() {
  initCharts()

  const { textMain, textSub, border, primary, primaryStrong, danger } = themeVars()
  const names = Object.keys(productBatchStats.value)
  const counts = names.map((name) => Number(productBatchStats.value[name] || 0))

  barChart?.setOption({
    backgroundColor: 'transparent',
    tooltip: { trigger: 'axis' },
    xAxis: {
      type: 'category',
      data: names,
      axisLabel: { color: textSub }
    },
    yAxis: {
      type: 'value',
      axisLabel: { color: textSub },
      splitLine: { lineStyle: { color: border } }
    },
    series: [
      {
        type: 'bar',
        data: counts,
        itemStyle: {
          borderRadius: [6, 6, 0, 0],
          color: primaryStrong
        }
      }
    ]
  })

  pieChart?.setOption({
    tooltip: { trigger: 'item' },
    legend: {
      bottom: 0,
      textStyle: { color: textSub }
    },
    series: [
      {
        type: 'pie',
        radius: ['45%', '72%'],
        avoidLabelOverlap: false,
        label: { color: textMain },
        data: [
          { value: Number(qualityRatioStats.value.PASS || 0), name: '合格', itemStyle: { color: primary } },
          { value: Number(qualityRatioStats.value.FAIL || 0), name: '不合格', itemStyle: { color: danger } }
        ]
      }
    ]
  })

  lineChart?.setOption({
    tooltip: { trigger: 'axis' },
    xAxis: {
      type: 'category',
      data: monthlyStats.value.map((item) => item.month),
      axisLabel: { color: textSub }
    },
    yAxis: {
      type: 'value',
      axisLabel: { color: textSub },
      splitLine: { lineStyle: { color: border } }
    },
    series: [
      {
        type: 'line',
        smooth: true,
        data: monthlyStats.value.map((item) => Number(item.count || 0)),
        lineStyle: { color: primary, width: 3 },
        itemStyle: { color: primaryStrong },
        areaStyle: {
          color: `${primary}55`
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
  themeObserver = new MutationObserver(() => {
    renderCharts()
  })
  themeObserver.observe(document.documentElement, {
    attributes: true,
    attributeFilter: ['data-theme']
  })
  await loadData()
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', resizeCharts)
  themeObserver?.disconnect()
  barChart?.dispose()
  pieChart?.dispose()
  lineChart?.dispose()
})
</script>
