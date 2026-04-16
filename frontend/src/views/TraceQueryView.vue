<template>
  <section class="card trace-page">
    <h2>二维码生成 + 扫码溯源</h2>
    <p class="hint">支持手动输入二维码编号，或启用摄像头扫码。</p>

    <form class="row" @submit.prevent="handleQuery">
      <input v-model.trim="qrCode" placeholder="请输入二维码编号，例如 demo-qr-001" required />
      <button type="submit" :disabled="loading">{{ loading ? '查询中...' : '查询溯源' }}</button>
    </form>

    <div class="actions-inline" style="margin-top: 10px">
      <button class="btn btn-light" type="button" @click="useDemo('demo-qr-001')">示例二维码</button>
      <button class="btn btn-light" type="button" @click="useDemo(latestBatchQr)" :disabled="!latestBatchQr">最近批次</button>
      <button class="btn btn-secondary" type="button" @click="toggleScanner">{{ scannerActive ? '停止扫码' : '启用扫码' }}</button>
    </div>

    <div v-if="scannerActive" class="scanner-wrap">
      <video ref="videoRef" autoplay muted playsinline class="scanner-video"></video>
      <p class="hint">请把二维码放入画面中央，识别后会自动查询。</p>
    </div>

    <p v-if="scanHint" class="hint">{{ scanHint }}</p>
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

      <h3>链式溯源事件</h3>
      <div v-if="!result.events.length" class="hint">暂无事件记录</div>
      <div v-for="event in result.events" :key="event.id" class="event-item">
        <div class="event-title">
          <strong>{{ stageLabel(event.stage) }}</strong>
          <span>{{ formatDateTime(event.eventTime) }}</span>
        </div>
        <div>{{ event.location }} ｜ {{ event.operatorName }}</div>
        <div>{{ event.details }}</div>
        <div v-if="event.iotPayload" class="hint">已记录环境监测数据（温湿度等）。</div>
      </div>

      <h3>生产记录</h3>
      <div v-if="!result.productionRecords?.length" class="hint">暂无生产记录</div>
      <div v-else class="event-item" v-for="item in result.productionRecords" :key="`p-${item.id}`">
        <div class="event-title">
          <strong>种植 + 农资</strong>
          <span>{{ formatDateTime(item.eventTime) }}</span>
        </div>
        <div>种植信息：{{ item.plantingInfo }}</div>
        <div>农资使用：{{ item.agriInput }}</div>
        <div class="hint">操作人：{{ item.operatorName }}</div>
      </div>

      <h3>加工记录</h3>
      <div v-if="!result.processingRecords?.length" class="hint">暂无加工记录</div>
      <div v-else class="event-item" v-for="item in result.processingRecords" :key="`pc-${item.id}`">
        <div class="event-title">
          <strong>加工</strong>
          <span>{{ formatDateTime(item.eventTime) }}</span>
        </div>
        <div>{{ item.processInfo }}</div>
        <div class="hint">操作人：{{ item.operatorName }}</div>
      </div>

      <h3>质检管理</h3>
      <div v-if="!result.qualityRecords?.length" class="hint">暂无质检记录</div>
      <div v-else class="event-item" v-for="item in result.qualityRecords" :key="`q-${item.id}`">
        <div class="event-title">
          <strong :class="item.result === 'PASS' ? 'ok' : 'danger'">{{ item.result === 'PASS' ? '合格' : '不合格' }}</strong>
          <span>{{ formatDateTime(item.inspectTime) }}</span>
        </div>
        <div>{{ item.notes }}</div>
        <div class="hint">质检员：{{ item.inspector }}</div>
      </div>

      <h3>仓储记录</h3>
      <div v-if="!result.warehouseRecords?.length" class="hint">暂无仓储记录</div>
      <div v-else class="event-item" v-for="item in result.warehouseRecords" :key="`w-${item.id}`">
        <div class="event-title">
          <strong>{{ item.recordType === 'IN' ? '入库' : '出库' }}</strong>
          <span>{{ formatDateTime(item.recordTime) }}</span>
        </div>
        <div>数量：{{ item.quantity }} ｜ 仓位：{{ item.location }}</div>
        <div class="hint">操作人：{{ item.operatorName }}</div>
      </div>
    </div>
  </section>
</template>

<script setup>
import { onBeforeUnmount, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { apiGet } from '../api/client'
import { formatDateTime, stageLabel, statusLabel } from '../utils/trace'

const route = useRoute()
const router = useRouter()

const qrCode = ref('demo-qr-001')
const loading = ref(false)
const error = ref('')
const result = ref(null)
const latestBatchQr = ref('')

const scannerActive = ref(false)
const scanHint = ref('')
const videoRef = ref(null)
let mediaStream = null
let scanTimer = null

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

async function startScanner() {
  error.value = ''
  scanHint.value = ''

  if (!navigator.mediaDevices?.getUserMedia) {
    scanHint.value = '当前浏览器不支持摄像头扫码，请手动输入二维码编号。'
    return
  }

  if (typeof window.BarcodeDetector === 'undefined') {
    scanHint.value = '当前环境暂不支持摄像头扫码，请手动输入二维码编号或更换设备后重试。'
    return
  }

  const detector = new window.BarcodeDetector({ formats: ['qr_code'] })

  try {
    mediaStream = await navigator.mediaDevices.getUserMedia({
      video: { facingMode: { ideal: 'environment' } },
      audio: false
    })

    if (videoRef.value) {
      videoRef.value.srcObject = mediaStream
      await videoRef.value.play()
    }

    scannerActive.value = true
    scanHint.value = '扫码已开启，正在识别...'

    scanTimer = window.setInterval(async () => {
      if (!videoRef.value) return
      try {
        const barcodes = await detector.detect(videoRef.value)
        if (barcodes?.length) {
          const raw = barcodes[0].rawValue || ''
          if (raw) {
            const parsed = extractQr(raw)
            qrCode.value = parsed
            stopScanner()
            await handleQuery()
          }
        }
      } catch {
        // ignore one-frame detection errors
      }
    }, 500)
  } catch (err) {
    scanHint.value = `摄像头开启失败：${err.message || '未知错误'}`
    stopScanner()
  }
}

function extractQr(text) {
  try {
    const url = new URL(text)
    const q = url.searchParams.get('qr')
    return q || text
  } catch {
    return text
  }
}

function stopScanner() {
  scannerActive.value = false
  scanHint.value = ''

  if (scanTimer) {
    window.clearInterval(scanTimer)
    scanTimer = null
  }

  if (mediaStream) {
    mediaStream.getTracks().forEach((track) => track.stop())
    mediaStream = null
  }

  if (videoRef.value) {
    videoRef.value.srcObject = null
  }
}

function toggleScanner() {
  if (scannerActive.value) {
    stopScanner()
  } else {
    startScanner()
  }
}

onMounted(async () => {
  await fetchLatestBatch()

  if (typeof route.query.qr === 'string' && route.query.qr.trim()) {
    qrCode.value = route.query.qr.trim()
  }

  await handleQuery()
})

onBeforeUnmount(() => {
  stopScanner()
})
</script>
