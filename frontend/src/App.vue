<template>
  <div class="app-shell">
    <header class="topbar">
      <div class="brand">
        <span class="brand-icon">农</span>
        <div>
          <h1>农产品溯源平台</h1>
          <p>Agri Traceability Platform</p>
        </div>
      </div>

      <nav v-if="authState.user" class="nav-tabs">
        <RouterLink to="/dashboard">概览</RouterLink>
        <RouterLink to="/batch-center">批次中心</RouterLink>
        <RouterLink to="/trace">溯源查询</RouterLink>
        <RouterLink v-if="isManagerRole()" to="/ops">运营台</RouterLink>
      </nav>

      <div class="topbar-actions">
        <button class="theme-toggle" type="button" @click="toggleTheme">
          <span>{{ theme === 'light' ? '🌙' : '☀️' }}</span>
          <span>{{ theme === 'light' ? '深色模式' : '浅色模式' }}</span>
        </button>

        <div class="user-panel" v-if="authState.user">
          <div class="user-card">
            <div class="user-name">{{ authState.user.username }}</div>
            <div class="user-role">{{ roleLabel(authState.user.role) }}</div>
          </div>
          <button class="btn btn-light" @click="handleLogout">退出</button>
        </div>
        <div v-else>
          <RouterLink class="btn btn-light" to="/auth">登录 / 注册</RouterLink>
        </div>
      </div>
    </header>

    <main class="container">
      <RouterView />
    </main>
  </div>
</template>

<script setup>
import { onMounted, ref, watch } from 'vue'
import { RouterLink, RouterView, useRouter } from 'vue-router'
import { authState, isManagerRole, logout, roleLabel } from './stores/auth'

const router = useRouter()
const theme = ref(localStorage.getItem('agri-traceability-theme') || 'dark')

function applyTheme(mode) {
  document.documentElement.setAttribute('data-theme', mode)
  localStorage.setItem('agri-traceability-theme', mode)
}

function toggleTheme() {
  theme.value = theme.value === 'light' ? 'dark' : 'light'
}

watch(theme, (value) => {
  applyTheme(value)
}, { immediate: true })

onMounted(() => {
  applyTheme(theme.value)
})

function handleLogout() {
  logout()
  router.push('/auth')
}
</script>
