<template>
  <section class="auth-single-page">
    <div class="auth-stage">
      <aside class="auth-panel card glass">
        <span class="pill">企业版入口</span>
        <h2>农产品溯源平台</h2>
        <p class="hint">统一管理批次台账、过程记录、质检留痕与扫码追溯。请登录后使用。</p>

        <div class="auth-points">
          <div class="auth-point">
            <strong>全链路记录</strong>
            <span>生产、加工、质检、仓储数据统一沉淀</span>
          </div>
          <div class="auth-point">
            <strong>角色分级权限</strong>
            <span>管理员 / 企业 / 普通用户按职责访问</span>
          </div>
          <div class="auth-point">
            <strong>支持扫码追溯</strong>
            <span>终端用户可直接查看批次来源与状态</span>
          </div>
        </div>
      </aside>

      <section class="auth-wrap auth-clean card glass">
        <div class="auth-header">
          <h2>{{ mode === 'login' ? '登录平台' : '注册账号' }}</h2>
          <div class="hint">未登录不可进入平台页面，请先登录或注册账号。</div>
        </div>

        <div class="auth-tabs auth-tabs-capsule">
          <button :class="['tab-pill', mode === 'login' ? 'active' : '']" @click="mode = 'login'">登录</button>
          <button :class="['tab-pill', mode === 'register' ? 'active' : '']" @click="mode = 'register'">注册</button>
        </div>

        <p v-if="error" class="error">{{ error }}</p>
        <p v-if="success" class="ok">{{ success }}</p>

        <form class="form-grid" @submit.prevent="submit">
          <label>
            用户名
            <input v-model.trim="form.username" placeholder="请输入用户名" required />
          </label>

          <label>
            密码
            <input v-model.trim="form.password" type="password" placeholder="请输入密码" required />
          </label>

          <template v-if="mode === 'register'">
            <label>
              角色
              <select v-model="form.role" required>
                <option value="ADMIN">管理员</option>
                <option value="ENTERPRISE">企业</option>
                <option value="USER">普通用户</option>
              </select>
            </label>

            <label>
              企业名称（企业角色建议填写）
              <input v-model.trim="form.enterpriseName" placeholder="例如：大祥区示范农场" />
            </label>
          </template>

          <button :disabled="authState.loading" type="submit">
            {{ authState.loading ? '提交中...' : mode === 'login' ? '立即登录' : '立即注册' }}
          </button>
        </form>

        <div class="demo-login auth-demo-grid">
          <div class="demo-account-card">
            <span class="pill">管理员</span>
            <strong>admin（管理员） / admin123</strong>
          </div>
          <div class="demo-account-card">
            <span class="pill">企业</span>
            <strong>enterprise（企业） / ent123</strong>
          </div>
          <div class="demo-account-card">
            <span class="pill">普通用户</span>
            <strong>user（普通用户） / user123</strong>
          </div>
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
