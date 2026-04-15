<template>
  <section class="auth-wrap card">
    <div class="section-title">
      <h2>用户模块</h2>
      <div class="hint">支持管理员 / 企业 / 普通用户</div>
    </div>

    <div class="auth-tabs">
      <button :class="['btn', mode === 'login' ? '' : 'btn-light']" @click="mode = 'login'">登录</button>
      <button :class="['btn', mode === 'register' ? '' : 'btn-light']" @click="mode = 'register'">注册</button>
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
          <input v-model.trim="form.enterpriseName" placeholder="例如：雨湖区示范农场" />
        </label>
      </template>

      <button :disabled="authState.loading" type="submit">
        {{ authState.loading ? '提交中...' : mode === 'login' ? '立即登录' : '立即注册' }}
      </button>
    </form>

    <div class="demo-login">
      <p class="hint">演示账号：</p>
      <ul>
        <li><code>admin / admin123</code>（管理员）</li>
        <li><code>enterprise / ent123</code>（企业）</li>
        <li><code>user / user123</code>（普通用户）</li>
      </ul>
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
