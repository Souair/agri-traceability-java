<template>
  <div class="layout">
    <header class="header">
      <h1>🥬 农产品溯源系统（前后端分离版）</h1>
      <nav>
        <RouterLink to="/">溯源查询</RouterLink>
        <RouterLink to="/admin">管理录入</RouterLink>
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
