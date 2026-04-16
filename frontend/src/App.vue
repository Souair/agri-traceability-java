<template>
  <div class="app-shell" :class="{ 'auth-shell': isAuthPage }">
    <header v-if="!isAuthPage" class="topbar">
      <div class="brand">
        <span class="brand-icon">农</span>
        <div>
          <h1>农产品溯源平台</h1>
          <p>Agri Traceability Platform</p>
        </div>
      </div>

      <div class="topbar-actions">
        <button class="theme-toggle" type="button" @click="toggleTheme">
          <span>{{ theme === 'light' ? '🌙' : '☀️' }}</span>
          <span>{{ theme === 'light' ? '深色模式' : '浅色模式' }}</span>
        </button>

        <div class="user-panel" v-if="authState.user">
          <div class="user-card user-card-inline">
            <span class="user-name">{{ authState.user.username }}</span>
            <span class="user-divider">·</span>
            <span class="user-role">{{ roleLabel(authState.user.role) }}</span>
          </div>
          <button class="btn btn-light" @click="handleLogout">退出</button>
        </div>
        <div v-else>
          <RouterLink class="btn btn-light" to="/auth">登录 / 注册</RouterLink>
        </div>
      </div>
    </header>

    <div v-if="!isAuthPage && authState.user" class="layout-main">
      <aside class="side-menu card glass">
        <details open>
          <summary>工作台</summary>
          <RouterLink to="/dashboard">概览</RouterLink>
        </details>

        <details open>
          <summary>追溯业务</summary>
          <RouterLink to="/batch-center">批次中心</RouterLink>
          <RouterLink to="/trace">溯源查询</RouterLink>
        </details>

        <details v-if="isManagerRole()" open>
          <summary>运营管理</summary>
          <RouterLink to="/ops">运营台</RouterLink>
        </details>
      </aside>

      <main class="container content-area">
        <RouterView />
      </main>
    </div>

    <main v-else class="container" :class="{ 'auth-container': isAuthPage }">
      <RouterView />
    </main>
  </div>
</template>

<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { RouterLink, RouterView, useRoute, useRouter } from 'vue-router'
import { authState, isManagerRole, logout, roleLabel } from './stores/auth'

const router = useRouter()
const route = useRoute()
const theme = ref(localStorage.getItem('agri-traceability-theme') || 'dark')

const isAuthPage = computed(() => route.path === '/auth')

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
