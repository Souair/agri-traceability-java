<template>
  <div class="app-shell">
    <header class="topbar">
      <div class="brand">
        <span class="brand-icon">🌾</span>
        <div>
          <h1>智慧农产品溯源平台</h1>
          <p>现代农业科技 · 全链路可信溯源 · 数据可视化监管</p>
        </div>
      </div>

      <nav class="nav-tabs">
        <RouterLink to="/dashboard">运营看板</RouterLink>
        <RouterLink to="/batch-center">批次中心</RouterLink>
        <RouterLink to="/trace">溯源查询</RouterLink>
        <RouterLink to="/admin">数据录入</RouterLink>
      </nav>
    </header>

    <main class="container">
      <RouterView />
    </main>
  </div>
</template>

<script setup>
import { RouterLink, RouterView } from 'vue-router'
</script>
