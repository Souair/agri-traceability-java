<template>
  <section class="auth-landing page-grid">
    <div class="card glass auth-intro auth-intro-clean">
      <span class="pill">企业版入口</span>
      <h2>农产品溯源平台</h2>
      <p class="hint">请使用账号登录后进入业务页面。平台支持管理员、企业、普通用户三类角色。</p>

      <div class="auth-feature-list compact">
        <div class="auth-feature-item">
          <strong>全链路记录</strong>
          <span>覆盖生产、加工、质检、仓储、追溯查询</span>
        </div>
        <div class="auth-feature-item">
          <strong>角色权限分层</strong>
          <span>不同身份进入对应功能区，避免误操作</span>
        </div>
      </div>
    </div>

    <section class="auth-wrap auth-capsule card glass">
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
          <p class="hint">查看全局数据、维护业务配置</p>
        </div>
        <div class="demo-account-card">
          <span class="pill">企业</span>
          <strong>enterprise（企业） / ent123</strong>
          <p class="hint">创建批次、录入过程记录、生成二维码</p>
        </div>
        <div class="demo-account-card">
          <span class="pill">普通用户</span>
          <strong>user（普通用户） / user123</strong>
          <p class="hint">查询溯源信息、查看公开数据</p>
        </div>
      </div>
    </section>
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
