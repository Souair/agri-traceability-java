<template>
  <section class="card trace-page">
    <h2>消费者溯源查询</h2>
    <p class="hint">输入二维码编号，即可查看农产品从种植到流通的全过程。</p>

    <form class="row" @submit.prevent="handleQuery">
      <input v-model.trim="qrCode" placeholder="请输入二维码编号，例如 demo-qr-001" required />
      <button type="submit" :disabled="loading">{{ loading ? '查询中...' : '立即查询' }}</button>
    </form>

    <div class="quick-tags">
      <button class="btn btn-light" type="button" @click="useDemo('demo-qr-001')">示例批次</button>
      <button class="btn btn-light" type="button" @click="useDemo(latestBatchQr)" :disabled="!latestBatchQr">
        最近新增批次
      </button>
    </div>

    <p v-if="error" class="error">{{ error }}</p>

    <div v-if="result" class="result-wrap">
      <div class="meta-grid">
        <div><b>产品：</b>{{ result.batch.productName }}</div>
        <div><b>批次：</b>{{ result.batch.batchCode }}</div>
        <div><b>产地：</b>{{ result.batch.origin }}</div>
        <div><b>生产者：</b>{{ result.batch.producer }}</div>
        <div><b>状态：</b>{{ statusLabel(result.batch.status) }}</div>
        <div><b>二维码：</b><code>{{ result.batch.qrCode }}</code></div>
      </div>

      <p>
        数据完整性：
        <span :class="result.integrityValid ? 'ok' : 'danger'">
          {{ result.integrityValid ? '校验通过' : '存在异常' }}
        </span>
      </p>

      <h3>生命周期事件</h3>
      <div v-if="!result.events.length" class="hint">暂无事件记录</div>
      <div v-for="event in result.events" :key="event.id" class="event-item">
        <div class="event-title">
          <strong>{{ stageLabel(event.stage) }}</strong>
          <span>{{ formatDateTime(event.eventTime) }}</span>
        </div>
        <div>{{ event.location }} ｜ {{ event.operatorName }}</div>
        <div>{{ event.details }}</div>
        <pre v-if="event.iotPayload" class="iot-block">{{ toPrettyJson(event.iotPayload) }}</pre>
        <div class="hash">prevHash：{{ event.prevHash }}</div>
        <div class="hash">blockHash：{{ event.blockHash }}</div>
        <div class="hash">txId：{{ event.onChainTxId }}</div>
      </div>
    </div>
  </section>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { apiGet } from '../api/client'
import { formatDateTime, stageLabel, statusLabel, toPrettyJson } from '../utils/trace'

const route = useRoute()
const router = useRouter()

const qrCode = ref('demo-qr-001')
const loading = ref(false)
const error = ref('')
const result = ref(null)
const latestBatchQr = ref('')

async function fetchLatestBatch() {
  try {
    const batches = await apiGet('/batches')
    if (batches.length > 0) {
      latestBatchQr.value = batches[0].qrCode
    }
  } catch {
    latestBatchQr.value = ''
  }
}

async function handleQuery() {
  if (!qrCode.value) return

  error.value = ''
  result.value = null
  loading.value = true
  try {
    result.value = await apiGet(`/trace/${encodeURIComponent(qrCode.value)}`)
    router.replace({ path: '/trace', query: { qr: qrCode.value } })
  } catch (err) {
    error.value = err.message
  } finally {
    loading.value = false
  }
}

function useDemo(code) {
  if (!code) return
  qrCode.value = code
  handleQuery()
}

onMounted(async () => {
  await fetchLatestBatch()

  if (typeof route.query.qr === 'string' && route.query.qr.trim()) {
    qrCode.value = route.query.qr.trim()
  }

  await handleQuery()
})
</script>
