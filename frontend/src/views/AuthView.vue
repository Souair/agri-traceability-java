<template>
  <section class="auth-single-page">
    <div class="auth-template-shell">
      <aside class="auth-hero card glass">
        <div class="auth-hero-header">
          <span class="auth-dot"></span>
          <span>Agri Traceability Platform</span>
        </div>

        <h2 class="auth-hero-title">让农产品溯源管理更清晰、更可靠</h2>
        <p class="auth-hero-subtitle">覆盖批次管理、过程记录、质检留痕、仓储流转与扫码追溯。</p>

        <div class="auth-hero-cards">
          <article class="auth-hero-card">
            <strong>全链路数据归档</strong>
            <span>生产、加工、质检、仓储统一留痕</span>
          </article>
          <article class="auth-hero-card">
            <strong>角色权限分级</strong>
            <span>管理员 / 企业 / 普通用户按职责访问</span>
          </article>
          <article class="auth-hero-card">
            <strong>终端扫码可查</strong>
            <span>快速定位批次来源与流转状态</span>
          </article>
        </div>
      </aside>

      <section class="auth-form-card card glass">
        <div class="auth-form-top">
          <h2>{{ mode === 'login' ? '欢迎登录' : '创建账号' }}</h2>
          <p class="hint">{{ mode === 'login' ? '请输入账号信息进入平台' : '填写信息后即可注册并进入平台' }}</p>
        </div>

        <div class="auth-switch">
          <button :class="['auth-switch-btn', mode === 'login' ? 'active' : '']" @click="mode = 'login'">登录</button>
          <button :class="['auth-switch-btn', mode === 'register' ? 'active' : '']" @click="mode = 'register'">注册</button>
        </div>

        <p v-if="error" class="error">{{ error }}</p>
        <p v-if="success" class="ok">{{ success }}</p>

        <form class="form-grid auth-form-grid" @submit.prevent="submit">
          <label class="auth-field">
            <span>用户名</span>
            <input v-model.trim="form.username" placeholder="请输入用户名" required />
          </label>

          <label class="auth-field">
            <span>密码</span>
            <input v-model.trim="form.password" type="password" placeholder="请输入密码" required />
          </label>

          <template v-if="mode === 'register'">
            <label class="auth-field">
              <span>角色</span>
              <select v-model="form.role" required>
                <option value="ADMIN">管理员</option>
                <option value="ENTERPRISE">企业</option>
                <option value="USER">普通用户</option>
              </select>
            </label>

            <label class="auth-field">
              <span>企业名称（企业角色建议填写）</span>
              <input v-model.trim="form.enterpriseName" placeholder="例如：大祥区示范农场" />
            </label>
          </template>

          <div class="auth-footrow hint">
            <span>未登录不可进入业务页面</span>
            <span>{{ mode === 'login' ? '请先登录' : '请先注册' }}</span>
          </div>

          <button class="auth-submit" :disabled="authState.loading" type="submit">
            {{ authState.loading ? '提交中...' : mode === 'login' ? '立即登录' : '立即注册' }}
          </button>
        </form>

        <div class="auth-account-grid">
          <div class="auth-account-item"><span>管理员</span><strong>admin / admin123</strong></div>
          <div class="auth-account-item"><span>企业</span><strong>enterprise / ent123</strong></div>
          <div class="auth-account-item"><span>普通用户</span><strong>user / user123</strong></div>
        </div>
      </section>
    </div>
  </section>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { authState, doLogin, doRegister, roleLabel } from '../stores/auth'

const route = useRoute()
const router = useRouter()

const mode = ref('login')
const error = ref('')
const success = ref('')

const form = reactive({
  username: '',
  password: '',
  role: 'USER',
  enterpriseName: ''
})

async function submit() {
  error.value = ''
  success.value = ''

  try {
    let user
    if (mode.value === 'login') {
      user = await doLogin({ username: form.username, password: form.password })
      success.value = `登录成功，当前角色：${roleLabel(user.role)}`
    } else {
      user = await doRegister({
        username: form.username,
        password: form.password,
        role: form.role,
        enterpriseName: form.enterpriseName
      })
      success.value = `注册并登录成功，当前角色：${roleLabel(user.role)}`
    }

    const redirect = typeof route.query.redirect === 'string' ? route.query.redirect : '/dashboard'
    setTimeout(() => router.push(redirect), 300)
  } catch (err) {
    error.value = err.message || '提交失败'
  }
}
</script>
