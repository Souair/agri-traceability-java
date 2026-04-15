export const STAGE_LABELS = {
  SEED: '育苗',
  PLANTING: '种植',
  PROCESSING: '加工',
  INSPECTION: '质检',
  LOGISTICS: '物流',
  SALES: '销售'
}

export const STATUS_LABELS = {
  IN_PRODUCTION: '生产中',
  IN_TRANSIT: '运输中',
  ON_SHELF: '在售',
  COMPLETED: '已完成'
}

export function stageLabel(stage) {
  return STAGE_LABELS[stage] || stage || '未知阶段'
}

export function statusLabel(status) {
  return STATUS_LABELS[status] || status || '未知状态'
}

export function formatDate(value) {
  if (!value) return '—'
  return new Date(value).toLocaleDateString('zh-CN')
}

export function formatDateTime(value) {
  if (!value) return '—'
  return new Date(value).toLocaleString('zh-CN', { hour12: false })
}

export function parseJsonMaybe(text) {
  if (!text) return null
  if (typeof text === 'object') return text
  try {
    return JSON.parse(text)
  } catch {
    return null
  }
}

export function toPrettyJson(text) {
  const data = parseJsonMaybe(text)
  if (!data) return text || '—'
  return JSON.stringify(data, null, 2)
}

export function latestEvent(events = []) {
  if (!events.length) return null
  return [...events].sort((a, b) => new Date(b.eventTime) - new Date(a.eventTime))[0]
}
