<template>
  <section class="page-grid">
    <div class="card glass">
      <div class="section-title">
        <h2>农业运营总览</h2>
        <button class="btn btn-secondary" @click="loadDashboard" :disabled="loading">
          {{ loading ? '刷新中...' : '刷新数据' }}
        </button>
      </div>

      <p v-if="error" class="error">{{ error }}</p>

      <div class="stats-grid">
        <article class="stat-card">
          <span>批次总数</span>
          <strong>{{ metrics.totalBatches }}</strong>
          <small>当前系统登记批次</small>
        </article>
        <article class="stat-card">
          <span>事件总量</span>
          <strong>{{ metrics.totalEvents }}</strong>
          <small>全链路采集记录</small>
        </article>
        <article class="stat-card">
          <span>链路完整率</span>
          <strong>{{ metrics.integrityRate }}%</strong>
          <small>{{ metrics.integrityPass }}/{{ metrics.totalBatches }} 批次校验通过</small>
        </article>
        <article class="stat-card">
          <span>采收完成率</span>
          <strong>{{ metrics.harvestRate }}%</strong>
          <small>{{ metrics.harvested }}/{{ metrics.totalBatches }} 批次已采收</small>
        </article>
      </div>
    </div>

    <div class="card">
      <h3>批次阶段分布</h3>
      <div v-if="!stageDistribution.length" class="hint">暂无可分析数据</div>
      <div v-else class="progress-list">
        <div v-for="item in stageDistribution" :key="item.stage" class="progress-item">
          <div class="progress-title">
            <span>{{ item.label }}</span>
            <span>{{ item.count }} 批次</span>
          </div>
          <div class="progress-track">
            <div class="progress-value" :style="{ width: item.percent + '%' }"></div>
          </div>
        </div>
      </div>
    </div>

    <div class="card">
      <h3>风险预警</h3>
      <div v-if="!alerts.length" class="ok">当前未发现明显异常风险</div>
      <ul v-else class="alert-list">
        <li v-for="item in alerts" :key="item.key" :class="item.level">
          <strong>{{ item.title }}</strong>
          <span>{{ item.message }}</span>
        </li>
      </ul>
    </div>

    <div class="card span-2">
      <h3>最新溯源动态</h3>
      <div v-if="!recentEvents.length" class="hint">暂无事件</div>
      <div v-else class="timeline">
        <article class="timeline-item" v-for="event in recentEvents" :key="event.id">
          <div class="timeline-dot"></div>
          <div class="timeline-content">
            <div class="timeline-title">
              <strong>{{ stageLabel(event.stage) }}</strong>
              <span>{{ formatDateTime(event.eventTime) }}</span>
            </div>
            <p>
              <b>{{ event.batch.productName }}</b>
              （{{ event.batch.batchCode }}）- {{ event.location }} - {{ event.operatorName }}
            </p>
            <p class="hint">{{ event.details }}</p>
          </div>
        </article>
      </div>
    </div>
  </section>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { apiGet } from '../api/client'
import { formatDateTime, latestEvent, parseJsonMaybe, stageLabel } from '../utils/trace'

const loading = ref(false)
const error = ref('')
const rows = ref([])

function extractTemperature(event) {
  const payload = parseJsonMaybe(event?.iotPayload)
  if (!payload || typeof payload !== 'object') return null
  const possibleKeys = ['temperature', 'temp', 'coldChainTemp']
  for (const key of possibleKeys) {
    if (payload[key] !== undefined && payload[key] !== null && !Number.isNaN(Number(payload[key]))) {
      return Number(payload[key])
    }
  }
  return null
}

async function loadDashboard() {
  loading.value = true
  error.value = ''
  try {
    const batches = await apiGet('/batches')
    const traces = await Promise.allSettled(
      batches.map((batch) => apiGet(`/trace/${encodeURIComponent(batch.qrCode)}`))
    )

    rows.value = batches.map((batch, index) => {
      const traceResult = traces[index]
      if (traceResult.status === 'fulfilled') {
        const trace = traceResult.value
        return {
          batch,
          events: trace.events || [],
          integrityValid: trace.integrityValid,
          traceError: ''
        }
      }

      return {
        batch,
        events: [],
        integrityValid: false,
        traceError: traceResult.reason?.message || '溯源链路读取失败'
      }
    })
  } catch (err) {
    error.value = err.message || '读取看板数据失败'
  } finally {
    loading.value = false
  }
}

const metrics = computed(() => {
  const totalBatches = rows.value.length
  const totalEvents = rows.value.reduce((sum, row) => sum + row.events.length, 0)
  const integrityPass = rows.value.filter((row) => row.integrityValid).length
  const harvested = rows.value.filter((row) => row.batch.harvestDate).length

  return {
    totalBatches,
    totalEvents,
    integrityPass,
    harvested,
    integrityRate: totalBatches ? Math.round((integrityPass / totalBatches) * 100) : 0,
    harvestRate: totalBatches ? Math.round((harvested / totalBatches) * 100) : 0
  }
})

const stageDistribution = computed(() => {
  const counter = new Map()

  rows.value.forEach((row) => {
    const last = latestEvent(row.events)
    const stage = last?.stage || 'SEED'
    counter.set(stage, (counter.get(stage) || 0) + 1)
  })

  const total = rows.value.length || 1

  return Array.from(counter.entries()).map(([stage, count]) => ({
    stage,
    label: stageLabel(stage),
    count,
    percent: Math.round((count / total) * 100)
  }))
})

const alerts = computed(() => {
  const list = []

  rows.value.forEach((row) => {
    const name = `${row.batch.productName} (${row.batch.batchCode})`

    if (row.traceError) {
      list.push({
        key: `${row.batch.id}-trace-error`,
        level: 'danger',
        title: '链路读取失败',
        message: `${name}：${row.traceError}`
      })
      return
    }

    if (!row.events.length) {
      list.push({
        key: `${row.batch.id}-no-events`,
        level: 'warning',
        title: '记录不足',
        message: `${name} 还没有任何溯源事件`
      })
      return
    }

    if (!row.integrityValid) {
      list.push({
        key: `${row.batch.id}-integrity`,
        level: 'danger',
        title: '链路异常',
        message: `${name} 的哈希链校验未通过`
      })
    }

    const last = latestEvent(row.events)
    const temp = extractTemperature(last)
    if (last?.stage === 'LOGISTICS' && temp !== null && temp > 8) {
      list.push({
        key: `${row.batch.id}-temp`,
        level: 'warning',
        title: '冷链温度预警',
        message: `${name} 物流环节温度 ${temp}℃，建议复核冷链质量`
      })
    }
  })

  return list.slice(0, 8)
})

const recentEvents = computed(() => {
  return rows.value
    .flatMap((row) => row.events.map((event) => ({ ...event, batch: row.batch })))
    .sort((a, b) => new Date(b.eventTime) - new Date(a.eventTime))
    .slice(0, 8)
})

onMounted(loadDashboard)
</script>
