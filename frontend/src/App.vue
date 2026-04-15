<template>
  <div class="app-shell">
    <header class="topbar">
      <div class="brand">
        <span class="brand-icon">🌾</span>
        <div>
          <h1>智慧农产品溯源平台</h1>
          <p>现代农业科技 · 全流程追溯 · 多角色协同</p>
        </div>
      </div>

      <nav class="nav-tabs">
        <RouterLink to="/dashboard">数据统计</RouterLink>
        <RouterLink to="/batch-center">批次中心</RouterLink>
        <RouterLink to="/trace">扫码溯源</RouterLink>
        <RouterLink v-if="isManagerRole()" to="/ops">业务管理</RouterLink>
      </nav>

      <div class="user-panel" v-if="authState.user">
        <div>
          <div class="user-name">{{ authState.user.username }}</div>
          <div class="user-role">{{ roleLabel(authState.user.role) }}</div>
        </div>
        <button class="btn btn-light" @click="handleLogout">退出</button>
      </div>
      <div v-else>
        <RouterLink class="btn btn-light" to="/auth">登录 / 注册</RouterLink>
      </div>
    </header>

    <main class="container">
      <RouterView />
    </main>
  </div>
</template>

<script setup>
import { RouterLink, RouterView, useRouter } from 'vue-router'
import { authState, isManagerRole, logout, roleLabel } from './stores/auth'

const router = useRouter()

function handleLogout() {
  logout()
  router.push('/auth')
}
</script>
