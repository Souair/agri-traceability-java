<template>
  <section class="card">
    <h2>消费者溯源查询</h2>
    <p class="hint">输入二维码编号（示例：<code>demo-qr-001</code>）</p>

    <form class="row" @submit.prevent="handleQuery">
      <input v-model.trim="qrCode" placeholder="请输入二维码编号" required />
      <button type="submit" :disabled="loading">{{ loading ? '查询中...' : '查询' }}</button>
    </form>

    <p v-if="error" class="error">{{ error }}</p>

    <div v-if="result" class="result-wrap">
      <h3>批次信息</h3>
      <ul>
        <li><b>产品：</b>{{ result.batch.productName }}</li>
        <li><b>批次：</b>{{ result.batch.batchCode }}</li>
        <li><b>产地：</b>{{ result.batch.origin }}</li>
        <li><b>生产者：</b>{{ result.batch.producer }}</li>
        <li><b>二维码：</b><code>{{ result.batch.qrCode }}</code></li>
      </ul>
      <p>
        数据完整性：
        <span :class="result.integrityValid ? 'ok' : 'danger'">
          {{ result.integrityValid ? '通过' : '异常' }}
        </span>
      </p>

      <h3>生命周期事件</h3>
      <div v-if="!result.events.length" class="hint">暂无事件</div>
      <div v-for="event in result.events" :key="event.id" class="event-item">
        <div class="event-title">
          <strong>{{ event.stage }}</strong>
          <span>{{ formatDateTime(event.eventTime) }}</span>
        </div>
        <div>操作人：{{ event.operatorName }} ｜ 地点：{{ event.location }}</div>
        <div>{{ event.details }}</div>
        <div v-if="event.iotPayload" class="hash">IoT：{{ event.iotPayload }}</div>
        <div class="hash">prevHash：{{ event.prevHash }}</div>
        <div class="hash">blockHash：{{ event.blockHash }}</div>
        <div class="hash">txId：{{ event.onChainTxId }}</div>
      </div>
    </div>
  </section>
</template>

<script setup>
import { ref } from 'vue'
import { apiGet } from '../api/client'

const qrCode = ref('demo-qr-001')
const loading = ref(false)
const error = ref('')
const result = ref(null)

function formatDateTime(value) {
  if (!value) return ''
  return new Date(value).toLocaleString('zh-CN', { hour12: false })
}

async function handleQuery() {
  error.value = ''
  result.value = null
  loading.value = true
  try {
    result.value = await apiGet(`/trace/${encodeURIComponent(qrCode.value)}`)
  } catch (err) {
    error.value = err.message
  } finally {
    loading.value = false
  }
}
</script>
