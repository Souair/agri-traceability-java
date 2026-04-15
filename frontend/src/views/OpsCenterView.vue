<template>
  <section class="page-grid">
    <div class="card span-2">
      <div class="section-title">
        <h2>产品管理 + 批次管理</h2>
        <button class="btn btn-secondary" @click="reloadAll" :disabled="loading">{{ loading ? '刷新中...' : '刷新' }}</button>
      </div>
      <p v-if="error" class="error">{{ error }}</p>
      <p v-if="success" class="ok">{{ success }}</p>
    </div>

    <div class="card">
      <h3>产品管理</h3>
      <form class="form-grid" @submit.prevent="createProduct">
        <input v-model.trim="productForm.name" placeholder="产品名称" required />
        <input v-model.trim="productForm.category" placeholder="产品分类（如：蔬菜/粮食）" required />
        <input v-model.trim="productForm.ownerEnterprise" placeholder="所属企业（可选）" />
        <textarea v-model.trim="productForm.description" rows="2" placeholder="产品描述（可选）" />
        <button :disabled="loading">新增产品</button>
      </form>

      <div class="table-wrap" style="margin-top: 10px">
        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th>名称</th>
              <th>分类</th>
              <th>企业</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="item in products" :key="item.id">
              <td>{{ item.id }}</td>
              <td>{{ item.name }}</td>
              <td>{{ item.category }}</td>
              <td>{{ item.ownerEnterprise || '—' }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <div class="card">
      <h3>批次管理</h3>
      <form class="form-grid" @submit.prevent="createBatch">
        <select v-model="batchForm.productName" required>
          <option disabled value="">选择产品</option>
          <option v-for="item in products" :key="item.id" :value="item.name">{{ item.name }}（{{ item.category }}）</option>
        </select>
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
        <button :disabled="loading">创建批次</button>
      </form>

      <div class="filters" style="margin-top: 10px">
        <select v-model="statusForm.batchId">
          <option disabled value="">选择批次修改状态</option>
          <option v-for="item in batches" :key="item.id" :value="item.id">{{ item.batchCode }} | {{ item.productName }}</option>
        </select>
        <select v-model="statusForm.status">
          <option value="IN_PRODUCTION">生产中</option>
          <option value="IN_TRANSIT">运输中</option>
          <option value="ON_SHELF">在售</option>
          <option value="COMPLETED">已完成</option>
        </select>
      </div>
      <button class="btn btn-secondary" @click="updateBatchStatus" :disabled="!statusForm.batchId || loading">更新批次状态</button>
    </div>

    <div class="card span-2">
      <h3>生产/加工/质检/仓储记录（精简）</h3>

      <div class="filters">
        <select v-model="recordBatchId">
          <option disabled value="">请选择批次</option>
          <option v-for="item in batches" :key="item.id" :value="item.id">{{ item.batchCode }} | {{ item.productName }}</option>
        </select>
        <RouterLink class="btn btn-light" :to="recordBatchId ? `/trace?qr=${encodeURIComponent(getBatchQr(recordBatchId))}` : '/trace'">
          去溯源页查看
        </RouterLink>
      </div>

      <div class="record-grid">
        <form class="form-grid" @submit.prevent="createProduction">
          <h4>生产记录（种植 + 农资）</h4>
          <input v-model="productionForm.eventTime" type="datetime-local" required />
          <textarea v-model.trim="productionForm.plantingInfo" rows="2" placeholder="种植信息" required />
          <textarea v-model.trim="productionForm.agriInput" rows="2" placeholder="农资使用" required />
          <input v-model.trim="productionForm.operatorName" placeholder="操作人" required />
          <button :disabled="!recordBatchId || loading">新增生产记录</button>
        </form>

        <form class="form-grid" @submit.prevent="createProcessing">
          <h4>加工记录（极简）</h4>
          <input v-model="processingForm.eventTime" type="datetime-local" required />
          <textarea v-model.trim="processingForm.processInfo" rows="2" placeholder="加工信息" required />
          <input v-model.trim="processingForm.operatorName" placeholder="操作人" required />
          <button :disabled="!recordBatchId || loading">新增加工记录</button>
        </form>

        <form class="form-grid" @submit.prevent="createQuality">
          <h4>质检管理</h4>
          <select v-model="qualityForm.result">
            <option value="PASS">合格</option>
            <option value="FAIL">不合格</option>
          </select>
          <input v-model="qualityForm.inspectTime" type="datetime-local" required />
          <input v-model.trim="qualityForm.inspector" placeholder="质检员" required />
          <textarea v-model.trim="qualityForm.notes" rows="2" placeholder="质检说明" required />
          <button :disabled="!recordBatchId || loading">新增质检记录</button>
        </form>

        <form class="form-grid" @submit.prevent="createWarehouse">
          <h4>仓储记录（入库/出库）</h4>
          <select v-model="warehouseForm.recordType">
            <option value="IN">入库</option>
            <option value="OUT">出库</option>
          </select>
          <input v-model.number="warehouseForm.quantity" type="number" min="0.01" step="0.01" required />
          <input v-model.trim="warehouseForm.location" placeholder="仓位/库区" required />
          <input v-model="warehouseForm.recordTime" type="datetime-local" required />
          <input v-model.trim="warehouseForm.operatorName" placeholder="仓管员" required />
          <button :disabled="!recordBatchId || loading">新增仓储记录</button>
        </form>
      </div>
    </div>

    <div class="card span-2">
      <h3>二维码生成（核心亮点）</h3>
      <div class="filters">
        <select v-model="qrBatchId">
          <option disabled value="">请选择批次生成二维码</option>
          <option v-for="item in batches" :key="item.id" :value="item.id">{{ item.batchCode }} | {{ item.productName }}</option>
        </select>
        <button class="btn btn-secondary" @click="generateBatchQr" :disabled="!qrBatchId">生成二维码</button>
      </div>

      <div v-if="qrPreview" class="qr-wrap">
        <img :src="qrPreview" alt="二维码" />
        <div>
          <p>二维码内容：</p>
          <code>{{ qrText }}</code>
        </div>
      </div>
      <p v-else class="hint">选择批次后点击“生成二维码”</p>
    </div>
  </section>
</template>

<script setup>
import QRCode from 'qrcode'
import { onMounted, reactive, ref } from 'vue'
import { RouterLink } from 'vue-router'
import { apiGet, apiPost, apiPut } from '../api/client'

const loading = ref(false)
const error = ref('')
const success = ref('')

const products = ref([])
const batches = ref([])

const productForm = reactive({
  name: '',
  category: '',
  description: '',
  ownerEnterprise: ''
})

const batchForm = reactive({
  productName: '',
  origin: '湖南省湘潭市雨湖区',
  producer: '雨湖区示范农场',
  plantingDate: '',
  harvestDate: ''
})

const statusForm = reactive({
  batchId: '',
  status: 'IN_PRODUCTION'
})

const recordBatchId = ref('')

function nowLocalInputValue() {
  const date = new Date()
  const pad = (n) => String(n).padStart(2, '0')
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}T${pad(date.getHours())}:${pad(date.getMinutes())}`
}

function normalizeTime(value) {
  if (!value) return value
  return value.length === 16 ? `${value}:00` : value
}

const productionForm = reactive({
  eventTime: nowLocalInputValue(),
  plantingInfo: '',
  agriInput: '',
  operatorName: ''
})

const processingForm = reactive({
  eventTime: nowLocalInputValue(),
  processInfo: '',
  operatorName: ''
})

const qualityForm = reactive({
  result: 'PASS',
  inspectTime: nowLocalInputValue(),
  inspector: '',
  notes: ''
})

const warehouseForm = reactive({
  recordType: 'IN',
  quantity: 100,
  location: '',
  recordTime: nowLocalInputValue(),
  operatorName: ''
})

const qrBatchId = ref('')
const qrPreview = ref('')
const qrText = ref('')

async function reloadAll() {
  loading.value = true
  error.value = ''

  try {
    const [p, b] = await Promise.all([apiGet('/products'), apiGet('/batches')])
    products.value = p
    batches.value = b

    if (!recordBatchId.value && b.length) {
      recordBatchId.value = b[0].id
    }
    if (!statusForm.batchId && b.length) {
      statusForm.batchId = b[0].id
    }
    if (!qrBatchId.value && b.length) {
      qrBatchId.value = b[0].id
    }
  } catch (err) {
    error.value = err.message || '加载数据失败'
  } finally {
    loading.value = false
  }
}

function toastOk(text) {
  success.value = text
  error.value = ''
}

function toastErr(err) {
  success.value = ''
  error.value = err.message || '操作失败'
}

async function createProduct() {
  try {
    await apiPost('/products', productForm)
    toastOk('产品创建成功')
    productForm.name = ''
    productForm.category = ''
    productForm.description = ''
    await reloadAll()
  } catch (err) {
    toastErr(err)
  }
}

async function createBatch() {
  try {
    await apiPost('/batches', {
      productName: batchForm.productName,
      origin: batchForm.origin,
      producer: batchForm.producer,
      plantingDate: batchForm.plantingDate || undefined,
      harvestDate: batchForm.harvestDate || undefined
    })
    toastOk('批次创建成功')
    await reloadAll()
  } catch (err) {
    toastErr(err)
  }
}

async function updateBatchStatus() {
  try {
    await apiPut(`/batches/${statusForm.batchId}/status`, { status: statusForm.status })
    toastOk('批次状态已更新')
    await reloadAll()
  } catch (err) {
    toastErr(err)
  }
}

async function createProduction() {
  try {
    await apiPost(`/batches/${recordBatchId.value}/production-records`, {
      ...productionForm,
      eventTime: normalizeTime(productionForm.eventTime)
    })
    toastOk('生产记录已新增')
  } catch (err) {
    toastErr(err)
  }
}

async function createProcessing() {
  try {
    await apiPost(`/batches/${recordBatchId.value}/processing-records`, {
      ...processingForm,
      eventTime: normalizeTime(processingForm.eventTime)
    })
    toastOk('加工记录已新增')
  } catch (err) {
    toastErr(err)
  }
}

async function createQuality() {
  try {
    await apiPost(`/batches/${recordBatchId.value}/quality-records`, {
      ...qualityForm,
      inspectTime: normalizeTime(qualityForm.inspectTime)
    })
    toastOk('质检记录已新增')
  } catch (err) {
    toastErr(err)
  }
}

async function createWarehouse() {
  try {
    await apiPost(`/batches/${recordBatchId.value}/warehouse-records`, {
      ...warehouseForm,
      recordTime: normalizeTime(warehouseForm.recordTime)
    })
    toastOk('仓储记录已新增')
  } catch (err) {
    toastErr(err)
  }
}

function getBatchQr(batchId) {
  const target = batches.value.find((item) => Number(item.id) === Number(batchId))
  return target?.qrCode || ''
}

async function generateBatchQr() {
  const code = getBatchQr(qrBatchId.value)
  if (!code) return

  const text = `${window.location.origin}/trace?qr=${encodeURIComponent(code)}`
  qrText.value = text
  qrPreview.value = await QRCode.toDataURL(text, {
    margin: 1,
    width: 220,
    color: {
      dark: '#073f2a',
      light: '#ffffff'
    }
  })
}

onMounted(reloadAll)
</script>
