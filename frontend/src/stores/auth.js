import { reactive } from 'vue'
import { clearToken, fetchMe, getToken, login, register } from '../api/client'

export const authState = reactive({
  token: getToken(),
  user: null,
  loading: false
})

export const roleMap = {
  ADMIN: '管理员',
  ENTERPRISE: '企业',
  USER: '普通用户'
}

export function roleLabel(role) {
  return roleMap[role] || role || '未知角色'
}

export async function restoreSession() {
  if (!authState.token) return
  try {
    authState.user = await fetchMe()
  } catch {
    logout()
  }
}

export async function doLogin(payload) {
  authState.loading = true
  try {
    const res = await login(payload)
    authState.token = res.token
    authState.user = {
      userId: res.userId,
      username: res.username,
      role: res.role,
      enterpriseName: res.enterpriseName
    }
    return authState.user
  } finally {
    authState.loading = false
  }
}

export async function doRegister(payload) {
  authState.loading = true
  try {
    const res = await register(payload)
    authState.token = res.token
    authState.user = {
      userId: res.userId,
      username: res.username,
      role: res.role,
      enterpriseName: res.enterpriseName
    }
    return authState.user
  } finally {
    authState.loading = false
  }
}

export function logout() {
  clearToken()
  authState.token = null
  authState.user = null
}

export function isManagerRole() {
  const role = authState.user?.role
  return role === 'ADMIN' || role === 'ENTERPRISE'
}
