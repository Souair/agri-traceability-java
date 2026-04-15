import axios from 'axios'

const baseURL = import.meta.env.VITE_API_BASE_URL || '/api'

export const apiClient = axios.create({
  baseURL,
  timeout: 10000
})

export async function apiGet(url) {
  const { data } = await apiClient.get(url)
  if (data.code !== 0) {
    throw new Error(data.message || '接口调用失败')
  }
  return data.data
}

export async function apiPost(url, body) {
  const { data } = await apiClient.post(url, body)
  if (data.code !== 0) {
    throw new Error(data.message || '接口调用失败')
  }
  return data.data
}
