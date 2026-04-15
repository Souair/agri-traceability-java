import axios from 'axios'

const baseURL = import.meta.env.VITE_API_BASE_URL || '/api'
const TOKEN_KEY = 'agri-traceability-token'

export const apiClient = axios.create({
  baseURL,
  timeout: 10000
})

apiClient.interceptors.request.use((config) => {
  const token = localStorage.getItem(TOKEN_KEY)
  if (token) {
    config.headers = config.headers || {}
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

function unwrapResponse(data) {
  if (data.code !== 0) {
    throw new Error(data.message || '接口调用失败')
  }
  return data.data
}

export async function apiGet(url, config) {
  const { data } = await apiClient.get(url, config)
  return unwrapResponse(data)
}

export async function apiPost(url, body, config) {
  const { data } = await apiClient.post(url, body, config)
  return unwrapResponse(data)
}

export async function apiPut(url, body, config) {
  const { data } = await apiClient.put(url, body, config)
  return unwrapResponse(data)
}

export async function apiDelete(url, config) {
  const { data } = await apiClient.delete(url, config)
  return unwrapResponse(data)
}

export function saveToken(token) {
  if (token) {
    localStorage.setItem(TOKEN_KEY, token)
  }
}

export function clearToken() {
  localStorage.removeItem(TOKEN_KEY)
}

export function getToken() {
  return localStorage.getItem(TOKEN_KEY)
}

export async function login(payload) {
  const res = await apiPost('/auth/login', payload)
  saveToken(res.token)
  return res
}

export async function register(payload) {
  const res = await apiPost('/auth/register', payload)
  saveToken(res.token)
  return res
}

export async function fetchMe() {
  return apiGet('/auth/me')
}
