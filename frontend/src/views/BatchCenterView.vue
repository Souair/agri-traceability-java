<template>
  <section class="page-grid">
    <div class="card span-2 glass">
      <div class="section-title">
        <h2>批次中心</h2>
         <span class="hint">集中查看批次台账、链路校验结果与追溯明细</span>
        <button class="btn btn-secondary" @click="loadData" :disabled="loading">
          {{ loading ? '加载中...' : '刷新批次' }}
        </button>
      </div>

      <div class="filters">
        <input v-model.trim="keyword" placeholder="搜索批次号 / 产品名称 / 产地 / 生产主体" />
        <select v-model="integrityFilter">
          <option value="all">全部链路状态</option>
          <option value="pass">仅完整通过</option>
          <option value="fail">仅链路异常</option>
          <option value="no-events">仅无事件批次</option>
        </select>
      </div>

      <p class="hint">点击表格行可查看该批次的完整时间线、校验状态和过程记录。</p>
      <p v-if="error" class="error">{{ error }}</p>

      <div class="table-wrap">
        <table>
          <thead>
            <tr>
              <th>批次号</th>
              <th>产品</th>
              <th>状态</th>
              <th>最新阶段</th>
              <th>链路校验</th>
              <th>二维码</th>
            </tr>
          </thead>
          <tbody>
            <tr
              v-for="batch in filteredBatches"
              :key="batch.id"
              @click="selectBatch(batch)"
              :class="{ selected: selectedBatch?.id === batch.id }"
            >
              <td>{{ batch.batchCode }}</td>
              <td>{{ batch.productName }}</td>
              <td>{{ statusLabel(batch.status) }}</td>
              <td>{{ stageLabel(traceMap[batch.qrCode]?.latestStage || 'SEED') }}</td>
              <td>
                <span :class="integrityClass(traceMap[batch.qrCode])">
                  {{ integrityText(traceMap[batch.qrCode]) }}
                </span>
              </td>
              <td><code>{{ batch.qrCode }}</code></td>
            </tr>
          </tbody>
        </table>
      </div>

      <p v-if="!filteredBatches.length" class="hint">没有匹配的批次记录</p>
    </div>

    <div class="card span-2" v-if="selectedBatch">
      <div class="section-title">
        <h3>{{ selectedBatch.productName }} · {{ selectedBatch.batchCode }}</h3>
        <div class="actions-inline">
          <button class="btn btn-light" @click="copyQrCode(selectedBatch.qrCode)">复制二维码编号</button>
          <button class="btn btn-light" @click="exportBatchReport">导出追溯报告（TXT）</button>
        </div>
      </div>

      <div class="meta-grid">
        <div><b>产地：</b>{{ selectedBatch.origin }}</div>
        <div><b>生产主体：</b>{{ selectedBatch.producer }}</div>
        <div><b>种植日期：</b>{{ formatDate(selectedBatch.plantingDate) }}</div>
        <div><b>采收日期：</b>{{ formatDate(selectedBatch.harvestDate) }}</div>
      </div>

      <p>
        链路校验：
        <span :class="integrityClass(selectedTrace)">{{ integrityText(selectedTrace) }}</span>
      </p>

      <div class="timeline" v-if="selectedTrace?.events?.length">
        <article class="timeline-item" v-for="event in selectedTrace.events" :key="event.id">
          <div class="timeline-dot"></div>
          <div class="timeline-content">
            <div class="timeline-title">
              <strong>{{ stageLabel(event.stage) }}</strong>
              <span>{{ formatDateTime(event.eventTime) }}</span>
            </div>
            <p>{{ event.details }}</p>
            <p class="hint">{{ event.location }} ｜ {{ event.operatorName }}</p>
            <div v-if="event.iotPayload" class="hint">已记录环境监测数据（温湿度等）。</div>
          </div>
        </article>
      </div>
      <p v-else class="hint">该批次暂无事件记录</p>
    </div>
  </section>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { apiGet } from '../api/client'
import { formatDate, formatDateTime, latestEvent, stageLabel, statusLabel } from '../utils/trace'

const loading = ref(false)
const error = ref('')
const keyword = ref('')
const integrityFilter = ref('all')

const batches = ref([])
const traceMap = ref({})
const selectedBatch = ref(null)

function integrityClass(trace) {
  if (!trace) return 'hint'
  if (!trace.events?.length) return 'warning'
  return trace.integrityValid ? 'ok' : 'danger'
}

function integrityText(trace) {
  if (!trace) return '待检测'
  if (!trace.events?.length) return '暂无事件'
  return trace.integrityValid ? '通过' : '异常'
}

async function loadData() {
  loading.value = true
  error.value = ''
  try {
    batches.value = await apiGet('/batches')
    const traceResult = await Promise.allSettled(
      batches.value.map((batch) => apiGet(`/trace/${encodeURIComponent(batch.qrCode)}`))
    )

    const map = {}
    batches.value.forEach((batch, idx) => {
      const item = traceResult[idx]
      if (item.status === 'fulfilled') {
        const trace = item.value
        map[batch.qrCode] = {
          ...trace,
          latestStage: latestEvent(trace.events)?.stage || 'SEED'
        }
      } else {
        map[batch.qrCode] = {
          events: [],
          integrityValid: false,
          latestStage: 'SEED',
          error: item.reason?.message || '读取失败'
        }
      }
    })

    traceMap.value = map

    if (!selectedBatch.value && batches.value.length) {
      selectedBatch.value = batches.value[0]
    }
  } catch (err) {
    error.value = err.message || '加载批次失败'
  } finally {
    loading.value = false
  }
}

function selectBatch(batch) {
  selectedBatch.value = batch
}

const selectedTrace = computed(() => {
  if (!selectedBatch.value) return null
  return traceMap.value[selectedBatch.value.qrCode]
})

const filteredBatches = computed(() => {
  const word = keyword.value.toLowerCase()

  return batches.value.filter((batch) => {
    const text = `${batch.batchCode} ${batch.productName} ${batch.origin} ${batch.producer}`.toLowerCase()
    if (word && !text.includes(word)) return false

    const trace = traceMap.value[batch.qrCode]
    if (integrityFilter.value === 'all') return true
    if (!trace) return false

    if (integrityFilter.value === 'pass') return trace.integrityValid === true
    if (integrityFilter.value === 'fail') return trace.events?.length > 0 && trace.integrityValid === false
    if (integrityFilter.value === 'no-events') return !trace.events?.length

    return true
  })
})

async function copyQrCode(qrCode) {
  try {
    await navigator.clipboard.writeText(qrCode)
    alert('二维码编号已复制')
  } catch {
    alert(`复制失败，请手动复制：${qrCode}`)
  }
}

function exportBatchReport() {
  if (!selectedBatch.value) return

  const trace = selectedTrace.value || { events: [], integrityValid: false }
  const lines = [
    `产品：${selectedBatch.value.productName}`,
    `批次：${selectedBatch.value.batchCode}`,
    `产地：${selectedBatch.value.origin}`,
    `生产者：${selectedBatch.value.producer}`,
    `状态：${statusLabel(selectedBatch.value.status)}`,
    `二维码：${selectedBatch.value.qrCode}`,
    `数据完整性：${trace.integrityValid ? '校验通过' : '存在异常'}`,
    '',
    '链式溯源事件',
    trace.events?.length ? `共 ${trace.events.length} 条` : '暂无事件记录'
  ]

  lines.push('', '生产记录', trace.productionRecords?.length ? `共 ${trace.productionRecords.length} 条` : '暂无生产记录')
  lines.push('', '加工记录', trace.processingRecords?.length ? `共 ${trace.processingRecords.length} 条` : '暂无加工记录')
  lines.push('', '质检管理', trace.qualityRecords?.length ? `共 ${trace.qualityRecords.length} 条` : '暂无质检记录')
  lines.push('', '仓储记录', trace.warehouseRecords?.length ? `共 ${trace.warehouseRecords.length} 条` : '暂无仓储记录')

  const content = lines.join('\n')
  const blob = new Blob([content], { type: 'text/plain;charset=utf-8' })
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = `${selectedBatch.value.batchCode}-trace-report.txt`
  a.click()
  URL.revokeObjectURL(url)
}

onMounted(loadData)
</script>
