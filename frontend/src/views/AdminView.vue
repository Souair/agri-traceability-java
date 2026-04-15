<template>
  <section class="admin-grid">
    <div class="card">
      <h2>创建批次</h2>
      <form @submit.prevent="createBatch" class="form-grid">
        <input v-model.trim="batchForm.batchCode" placeholder="批次编号（可留空自动生成）" />
        <input v-model.trim="batchForm.productName" placeholder="产品名称" required />
        <input v-model.trim="batchForm.origin" placeholder="产地" required />
        <input v-model.trim="batchForm.producer" placeholder="生产主体" required />
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
    </div>

    <div class="card">
      <h2>录入溯源事件</h2>
      <form @submit.prevent="createEvent" class="form-grid">
        <select v-model="eventForm.batchId" required>
          <option disabled value="">请选择批次</option>
          <option v-for="item in batches" :key="item.id" :value="item.id">
            {{ item.id }} | {{ item.batchCode }} | {{ item.productName }}
          </option>
        </select>

        <select v-model="eventForm.stage" required>
          <option v-for="s in stages" :key="s" :value="s">{{ s }}</option>
        </select>

        <label>
          事件时间
          <input v-model="eventForm.eventTime" type="datetime-local" required />
        </label>
        <input v-model.trim="eventForm.operatorName" placeholder="操作人" required />
        <input v-model.trim="eventForm.location" placeholder="地点" required />
        <textarea v-model.trim="eventForm.details" rows="3" placeholder="事件详情" required />
        <textarea v-model.trim="eventForm.iotPayload" rows="3" placeholder="IoT 数据 JSON（可选）" />

        <button type="submit" :disabled="eventLoading">
          {{ eventLoading ? '写入中...' : '写入事件' }}
        </button>
      </form>
      <p v-if="eventMsg" class="ok">{{ eventMsg }}</p>
      <p v-if="eventErr" class="error">{{ eventErr }}</p>
    </div>

    <div class="card full">
      <h2>批次列表</h2>
      <table>
        <thead>
          <tr>
            <th>ID</th>
            <th>批次号</th>
            <th>产品</th>
            <th>产地</th>
            <th>生产者</th>
            <th>二维码</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in batches" :key="item.id">
            <td>{{ item.id }}</td>
            <td>{{ item.batchCode }}</td>
            <td>{{ item.productName }}</td>
            <td>{{ item.origin }}</td>
            <td>{{ item.producer }}</td>
            <td><code>{{ item.qrCode }}</code></td>
          </tr>
        </tbody>
      </table>
    </div>
  </section>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { apiGet, apiPost } from '../api/client'

const stages = ['SEED', 'PLANTING', 'PROCESSING', 'INSPECTION', 'LOGISTICS', 'SALES']

const batches = ref([])
const batchLoading = ref(false)
const eventLoading = ref(false)
const batchMsg = ref('')
const eventMsg = ref('')
const eventErr = ref('')

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
  eventTime: '',
  operatorName: '',
  location: '',
  details: '',
  iotPayload: ''
})

async function loadBatches() {
  batches.value = await apiGet('/batches')
  if (!eventForm.value.batchId && batches.value.length) {
    eventForm.value.batchId = batches.value[0].id
  }
}

async function createBatch() {
  batchLoading.value = true
  batchMsg.value = ''
  try {
    const payload = { ...batchForm.value }
    if (!payload.batchCode) delete payload.batchCode
    if (!payload.plantingDate) delete payload.plantingDate
    if (!payload.harvestDate) delete payload.harvestDate

    const created = await apiPost('/batches', payload)
    batchMsg.value = `批次创建成功：${created.batchCode}，二维码：${created.qrCode}`
    await loadBatches()
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
      eventTime: eventForm.value.eventTime + ':00',
      operatorName: eventForm.value.operatorName,
      location: eventForm.value.location,
      details: eventForm.value.details
    }
    if (eventForm.value.iotPayload) payload.iotPayload = eventForm.value.iotPayload

    const data = await apiPost(`/batches/${eventForm.value.batchId}/events`, payload)
    eventMsg.value = `事件写入成功，txId：${data.onChainTxId}`
  } catch (err) {
    eventErr.value = err.message
  } finally {
    eventLoading.value = false
  }
}

onMounted(async () => {
  await loadBatches()
})
</script>
