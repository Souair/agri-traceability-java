<template>
  <section class="page-grid">
    <div class="card">
      <div class="section-title">
        <h2>批次建档</h2>
        <button class="btn btn-light" @click="fillBatchTemplate">填充示例</button>
      </div>

      <form @submit.prevent="createBatch" class="form-grid">
        <label>
          批次编号（可选）
          <input v-model.trim="batchForm.batchCode" placeholder="留空则自动生成" />
        </label>
        <label>
          产品名称
          <input v-model.trim="batchForm.productName" placeholder="例如：高山有机辣椒" required />
        </label>
        <label>
          产地
          <input v-model.trim="batchForm.origin" placeholder="例如：湖南省湘潭市雨湖区" required />
        </label>
        <label>
          生产主体
          <input v-model.trim="batchForm.producer" placeholder="例如：雨湖生态合作社" required />
        </label>
        <label>
          种植日期
          <input v-model="batchForm.plantingDate" type="date" />
        </label>
        <label>
          采收日期
          <input v-model="batchForm.harvestDate" type="date" />
        </label>

        <button type="submit" :disabled="batchLoading">
          {{ batchLoading ? '提交中...' : '创建批次' }}
        </button>
      </form>

      <p v-if="batchMsg" class="ok">{{ batchMsg }}</p>
      <p v-if="batchErr" class="error">{{ batchErr }}</p>
    </div>

    <div class="card">
      <div class="section-title">
        <h2>事件录入</h2>
        <button class="btn btn-light" @click="fillEventTemplate">填充模板</button>
      </div>

      <form @submit.prevent="createEvent" class="form-grid">
        <label>
          选择批次
          <select v-model="eventForm.batchId" required>
            <option disabled value="">请选择批次</option>
            <option v-for="item in batches" :key="item.id" :value="item.id">
              {{ item.batchCode }} ｜ {{ item.productName }}
            </option>
          </select>
        </label>

        <label>
          事件阶段
          <select v-model="eventForm.stage" required>
            <option v-for="s in stages" :key="s.value" :value="s.value">{{ s.label }}</option>
          </select>
        </label>

        <label>
          事件时间
          <input v-model="eventForm.eventTime" type="datetime-local" required />
        </label>

        <label>
          操作人
          <input v-model.trim="eventForm.operatorName" placeholder="例如：质检员李工" required />
        </label>

        <label>
          地点
          <input v-model.trim="eventForm.location" placeholder="例如：湘潭农残检测站" required />
        </label>

        <label>
          事件详情
          <textarea v-model.trim="eventForm.details" rows="3" placeholder="记录关键操作、检测结论、物流动作等" required />
        </label>

        <label>
          IoT 数据（JSON，可选）
          <textarea v-model.trim="eventForm.iotPayload" rows="3" placeholder='例如：{"temperature":4,"humidity":68}' />
        </label>

        <div class="row">
          <button type="submit" :disabled="eventLoading">
            {{ eventLoading ? '写入中...' : '写入事件' }}
          </button>
          <button type="button" class="btn btn-secondary" @click="autoCompleteStages" :disabled="eventLoading || !eventForm.batchId">
            一键补齐标准流程
          </button>
        </div>
      </form>

      <p v-if="eventMsg" class="ok">{{ eventMsg }}</p>
      <p v-if="eventErr" class="error">{{ eventErr }}</p>
    </div>

    <div class="card span-2">
      <div class="section-title">
        <h2>批次台账</h2>
        <div class="inline-badges">
          <span class="pill">总批次 {{ batches.length }}</span>
          <span class="pill">已采收 {{ harvestedCount }}</span>
        </div>
      </div>

      <div class="filters">
        <input v-model.trim="keyword" placeholder="搜索批次号/产品/产地/生产主体" />
      </div>

      <div class="table-wrap">
        <table>
          <thead>
            <tr>
              <th>批次号</th>
              <th>产品</th>
              <th>产地</th>
              <th>状态</th>
              <th>二维码</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="item in filteredBatches" :key="item.id">
              <td>{{ item.batchCode }}</td>
              <td>{{ item.productName }}</td>
              <td>{{ item.origin }}</td>
              <td>{{ statusLabel(item.status) }}</td>
              <td><code>{{ item.qrCode }}</code></td>
              <td>
                <div class="actions-inline">
                  <button class="btn btn-light" @click="copyQrCode(item.qrCode)">复制二维码</button>
                  <RouterLink class="btn btn-light" :to="`/trace?qr=${encodeURIComponent(item.qrCode)}`">去查询</RouterLink>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </section>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { RouterLink } from 'vue-router'
import { apiGet, apiPost } from '../api/client'
import { stageLabel, statusLabel } from '../utils/trace'

const stageOrder = ['SEED', 'PLANTING', 'INSPECTION', 'PROCESSING', 'LOGISTICS', 'SALES']

const stages = stageOrder.map((value) => ({ value, label: `${stageLabel(value)}（${value}）` }))

const batches = ref([])
const keyword = ref('')

const batchLoading = ref(false)
const eventLoading = ref(false)
const batchMsg = ref('')
const batchErr = ref('')
const eventMsg = ref('')
const eventErr = ref('')

function nowLocalInputValue(offsetHours = 0) {
  const date = new Date(Date.now() + offsetHours * 60 * 60 * 1000)
  const pad = (n) => String(n).padStart(2, '0')
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}T${pad(date.getHours())}:${pad(date.getMinutes())}`
}

const batchForm = ref({
  batchCode: '',
  productName: '',
  origin: '',
  producer: '',
  plantingDate: '',
  harvestDate: ''
})

const eventForm = ref({
  batchId: '',
  stage: 'PLANTING',
  eventTime: nowLocalInputValue(),
  operatorName: '',
  location: '',
  details: '',
  iotPayload: ''
})

const harvestedCount = computed(() => batches.value.filter((item) => item.harvestDate).length)

const filteredBatches = computed(() => {
  const word = keyword.value.toLowerCase()
  if (!word) return batches.value

  return batches.value.filter((item) => {
    const text = `${item.batchCode} ${item.productName} ${item.origin} ${item.producer}`.toLowerCase()
    return text.includes(word)
  })
})

async function loadBatches() {
  batches.value = await apiGet('/batches')
  if (!eventForm.value.batchId && batches.value.length) {
    eventForm.value.batchId = batches.value[0].id
  }
}

function fillBatchTemplate() {
  batchForm.value = {
    batchCode: '',
    productName: '高山有机辣椒',
    origin: '湖南省湘潭市雨湖区',
    producer: '雨湖生态合作社',
    plantingDate: '2026-04-01',
    harvestDate: ''
  }
}

function fillEventTemplate() {
  eventForm.value.stage = 'INSPECTION'
  eventForm.value.eventTime = nowLocalInputValue()
  eventForm.value.operatorName = '质检员王敏'
  eventForm.value.location = '雨湖区检测中心'
  eventForm.value.details = '抽样检测通过，允许进入下一环节'
  eventForm.value.iotPayload = '{"temperature":22,"humidity":62}'
}

function normalizeEventTime(value) {
  return value.length === 16 ? `${value}:00` : value
}

async function createBatch() {
  batchLoading.value = true
  batchMsg.value = ''
  batchErr.value = ''

  try {
    const payload = { ...batchForm.value }
    if (!payload.batchCode) delete payload.batchCode
    if (!payload.plantingDate) delete payload.plantingDate
    if (!payload.harvestDate) delete payload.harvestDate

    const created = await apiPost('/batches', payload)
    batchMsg.value = `创建成功：${created.batchCode}，二维码：${created.qrCode}`

    await loadBatches()
    eventForm.value.batchId = created.id
  } catch (err) {
    batchErr.value = err.message || '创建批次失败'
  } finally {
    batchLoading.value = false
  }
}

async function createEvent() {
  eventLoading.value = true
  eventMsg.value = ''
  eventErr.value = ''

  try {
    const payload = {
      stage: eventForm.value.stage,
      eventTime: normalizeEventTime(eventForm.value.eventTime),
      operatorName: eventForm.value.operatorName,
      location: eventForm.value.location,
      details: eventForm.value.details
    }

    if (eventForm.value.iotPayload) {
      payload.iotPayload = eventForm.value.iotPayload
    }

    const data = await apiPost(`/batches/${eventForm.value.batchId}/events`, payload)
    eventMsg.value = `写入成功：${stageLabel(data.stage)}，链上凭证 ${data.onChainTxId}`
    eventForm.value.eventTime = nowLocalInputValue()
  } catch (err) {
    eventErr.value = err.message || '写入事件失败'
  } finally {
    eventLoading.value = false
  }
}

async function autoCompleteStages() {
  if (!eventForm.value.batchId) return

  eventLoading.value = true
  eventMsg.value = ''
  eventErr.value = ''

  try {
    const batch = batches.value.find((item) => item.id === Number(eventForm.value.batchId))
    if (!batch) throw new Error('批次不存在')

    const trace = await apiGet(`/trace/${encodeURIComponent(batch.qrCode)}`)
    const existing = new Set((trace.events || []).map((event) => event.stage))

    const stageTemplates = {
      SEED: ['完成选种和育苗床准备', '{"seedQuality":"A"}', '农技员陈工', '育苗大棚'],
      PLANTING: ['完成移栽并开启水肥一体化', '{"soilMoisture":58,"temperature":24}', '农技员陈工', '种植区 A1'],
      INSPECTION: ['农残与重金属抽检通过', '{"pesticideResidue":"PASS"}', '质检员王敏', '区级检测站'],
      PROCESSING: ['分拣包装完成，进入待发货区', '{"packingLine":"L2"}', '加工员赵宁', '分拣车间'],
      LOGISTICS: ['冷链装车并发往配送中心', '{"temperature":4}', '物流调度李杰', '冷链中转仓'],
      SALES: ['上架销售，渠道可追溯信息同步', '{"store":"雨湖生鲜店"}', '门店店长周婷', '雨湖生鲜门店']
    }

    const appendStages = stageOrder.filter((stage) => !existing.has(stage))
    if (!appendStages.length) {
      eventMsg.value = '当前批次已具备完整标准流程，无需补齐'
      return
    }

    for (let i = 0; i < appendStages.length; i += 1) {
      const stage = appendStages[i]
      const [details, iotPayload, operatorName, location] = stageTemplates[stage]

      await apiPost(`/batches/${eventForm.value.batchId}/events`, {
        stage,
        eventTime: normalizeEventTime(nowLocalInputValue(i)),
        operatorName,
        location,
        details,
        iotPayload
      })
    }

    eventMsg.value = `补齐完成：新增 ${appendStages.length} 个阶段（${appendStages.map((s) => stageLabel(s)).join('、')}）`
  } catch (err) {
    eventErr.value = err.message || '一键补齐失败'
  } finally {
    eventLoading.value = false
  }
}

async function copyQrCode(qrCode) {
  try {
    await navigator.clipboard.writeText(qrCode)
    alert('二维码编号已复制')
  } catch {
    alert(`复制失败，请手动复制：${qrCode}`)
  }
}

onMounted(async () => {
  await loadBatches()
})
</script>
