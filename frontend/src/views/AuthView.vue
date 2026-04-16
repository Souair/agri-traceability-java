<template>
  <section class="ep-login-page">
    <div class="ep-login-card">
      <!-- 左侧品牌展示区 -->
      <aside class="ep-brand-pane">
        <h1>农产品追溯优品</h1>
        <h2>数智个性化服务定制系统</h2>

        <div class="ep-logo-wrap" aria-label="Logo占位图">
          <span class="ep-logo-text-top">追溯优品</span>
          <img
            class="ep-logo"
            src="https://dummyimage.com/220x220/edf8e9/2f7d4f.png&text=Logo"
            alt="追溯优品Logo占位图"
          />
          <span class="ep-logo-text-bottom">产地追溯信息验证</span>
        </div>
      </aside>

      <!-- 右侧登录区 -->
      <section class="ep-form-pane">
        <el-tabs v-model="activeTab" class="ep-login-tabs" stretch>
          <el-tab-pane label="用户登录" name="login" />
          <el-tab-pane label="溯源查验" name="trace" />
        </el-tabs>

        <div class="ep-tabs-divider" />

        <div class="ep-form-panel">
          <p v-if="error" class="error">{{ error }}</p>
          <p v-if="success" class="ok">{{ success }}</p>

          <!-- 用户登录 -->
          <form v-if="activeTab === 'login'" class="ep-form-grid" @submit.prevent="handleLogin">
            <div class="ep-field-group">
              <el-input v-model.trim="loginForm.username" class="ep-input" :class="{ 'is-error': accountError }" placeholder="账号">
                <template #prefix>
                  <el-icon><User /></el-icon>
                </template>
              </el-input>
              <p v-if="accountError" class="ep-inline-error">请输入您的账号</p>
            </div>

            <el-input v-model.trim="loginForm.password" class="ep-input" type="password" show-password placeholder="密码">
              <template #prefix>
                <el-icon><Lock /></el-icon>
              </template>
            </el-input>

            <div class="ep-captcha-row">
              <el-input v-model.trim="loginForm.captcha" class="ep-input" placeholder="验证码">
                <template #prefix>
                  <el-icon><CircleCheck /></el-icon>
                </template>
              </el-input>

              <button class="ep-captcha-box" type="button" @click="refreshCaptcha" title="点击刷新验证码">
                {{ captchaText }}
              </button>
            </div>

            <div class="ep-form-actions">
              <el-checkbox v-model="loginForm.remember">记住密码</el-checkbox>
            </div>

            <button class="ep-submit-btn" type="submit" :disabled="authState.loading">
              {{ authState.loading ? '登 录 中...' : '登 录' }}
            </button>
          </form>

          <!-- 溯源查验（模板同风格） -->
          <form v-else class="ep-form-grid" @submit.prevent="handleTraceCheck">
            <el-input v-model.trim="traceForm.traceCode" class="ep-input" placeholder="请输入溯源码">
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>

            <div class="ep-captcha-row">
              <el-input v-model.trim="traceForm.captcha" class="ep-input" placeholder="验证码">
                <template #prefix>
                  <el-icon><CircleCheck /></el-icon>
                </template>
              </el-input>

              <button class="ep-captcha-box" type="button" @click="refreshCaptcha" title="点击刷新验证码">
                {{ captchaText }}
              </button>
            </div>

            <button class="ep-submit-btn" type="submit">查 验</button>
          </form>
        </div>
      </section>
    </div>
  </section>
</template>

<script setup>
import { computed, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { CircleCheck, Lock, Search, User } from '@element-plus/icons-vue'
import { authState, doLogin } from '../stores/auth'

const route = useRoute()
const router = useRouter()

const activeTab = ref('login')
const error = ref('')
const success = ref('')
const captchaText = ref('A7K9')

const loginForm = reactive({
  username: '',
  password: '',
  captcha: '',
  remember: true
})

const traceForm = reactive({
  traceCode: '',
  captcha: ''
})

const accountError = computed(() => activeTab.value === 'login' && !loginForm.username.trim())

function refreshCaptcha() {
  const chars = 'ABCDEFGHJKLMNPQRSTUVWXYZ23456789'
  captchaText.value = Array.from({ length: 4 }, () => chars[Math.floor(Math.random() * chars.length)]).join('')
}

async function handleLogin() {
  error.value = ''
  success.value = ''

  if (!loginForm.username.trim()) {
    error.value = '请先完善登录信息'
    return
  }

  try {
    await doLogin({
      username: loginForm.username,
      password: loginForm.password
    })

    success.value = '登录成功，正在进入系统...'
    const redirect = typeof route.query.redirect === 'string' ? route.query.redirect : '/dashboard'
    setTimeout(() => router.push(redirect), 300)
  } catch (err) {
    error.value = err.message || '登录失败'
  }
}

function handleTraceCheck() {
  error.value = ''
  success.value = ''

  if (!traceForm.traceCode.trim()) {
    error.value = '请输入溯源码'
    return
  }

  success.value = '已提交查验请求（示例）'
}
</script>

<style scoped>
.ep-login-page {
  min-height: calc(100vh - 180px);
  padding: 20px;
  display: grid;
  place-items: center;
  position: relative;
  overflow: hidden;
  background:
    radial-gradient(circle at 16% 18%, rgba(106, 176, 76, 0.18), transparent 36%),
    radial-gradient(circle at 84% 82%, rgba(72, 151, 103, 0.14), transparent 32%),
    linear-gradient(160deg, #0f4f36 0%, #0c5d40 42%, #0a4a34 100%);
}

.ep-login-page::before {
  content: '';
  position: absolute;
  inset: 0;
  opacity: 0.22;
  pointer-events: none;
  background-image:
    radial-gradient(circle at 20% 20%, rgba(255, 255, 255, 0.26) 1px, transparent 1px),
    radial-gradient(circle at 80% 70%, rgba(255, 255, 255, 0.2) 1px, transparent 1px),
    linear-gradient(125deg, transparent 0 46%, rgba(255, 255, 255, 0.06) 48%, transparent 50% 100%);
  background-size: 120px 120px, 160px 160px, 100% 100%;
}

.ep-login-card {
  position: relative;
  z-index: 1;
  width: min(1100px, 100%);
  background: #fff;
  border-radius: 26px;
  box-shadow: 0 24px 60px rgba(8, 32, 24, 0.26);
  padding: 28px;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 22px;
}

.ep-brand-pane {
  border-radius: 18px;
  background: linear-gradient(180deg, #f8fcf7 0%, #f0f8ef 100%);
  border: 1px solid #d9ebdb;
  padding: 26px 22px;
  display: grid;
  align-content: start;
  justify-items: center;
  text-align: center;
  gap: 10px;
}

.ep-brand-pane h1 {
  margin: 0;
  color: #19643f;
  font-size: clamp(30px, 3vw, 38px);
  font-weight: 800;
  letter-spacing: 1px;
}

.ep-brand-pane h2 {
  margin: 0;
  color: #1f6d46;
  font-size: clamp(20px, 2.2vw, 28px);
  font-weight: 800;
  letter-spacing: 0.5px;
}

.ep-logo-wrap {
  margin-top: 14px;
  display: grid;
  justify-items: center;
  gap: 10px;
}

.ep-logo-text-top,
.ep-logo-text-bottom {
  color: #2f7d4f;
  font-weight: 700;
  font-size: 13px;
  letter-spacing: 0.1em;
}

.ep-logo {
  width: clamp(160px, 22vw, 220px);
  aspect-ratio: 1 / 1;
  border-radius: 50%;
  border: 4px solid #5fa86a;
  background: #fff;
  object-fit: cover;
  box-shadow: 0 8px 24px rgba(35, 120, 74, 0.2);
}

.ep-form-pane {
  display: grid;
  align-content: start;
}

:deep(.ep-login-tabs .el-tabs__header) {
  margin: 0;
}

:deep(.ep-login-tabs .el-tabs__nav-wrap::after) {
  display: none;
}

:deep(.ep-login-tabs .el-tabs__item) {
  height: 42px;
  color: #3f4a44;
  font-size: 16px;
  font-weight: 600;
}

:deep(.ep-login-tabs .el-tabs__item.is-active) {
  color: #2f7dff;
}

:deep(.ep-login-tabs .el-tabs__active-bar) {
  background: #2f7dff;
  height: 3px;
  border-radius: 99px;
}

.ep-tabs-divider {
  margin-top: 2px;
  border-bottom: 1px solid #e6e8eb;
}

.ep-form-panel {
  margin-top: 14px;
  background: linear-gradient(165deg, #1f7a58 0%, #176847 100%);
  border-radius: 14px;
  padding: 16px;
}

.ep-form-grid {
  display: grid;
  gap: 12px;
}

.ep-field-group {
  display: grid;
  gap: 4px;
}

.ep-inline-error {
  margin: 0;
  font-size: 11px;
  color: #ffd7d7;
}

:deep(.ep-input .el-input__wrapper) {
  border-radius: 8px;
  background: #fff;
  box-shadow: none;
  padding: 0 12px;
  min-height: 40px;
}

:deep(.ep-input .el-input__inner) {
  color: #1f2a24;
}

:deep(.ep-input .el-input__prefix .el-icon) {
  color: #76827c;
}

:deep(.ep-input.is-error .el-input__wrapper) {
  box-shadow: 0 0 0 1px #ff6767 inset !important;
}

.ep-captcha-row {
  display: grid;
  grid-template-columns: 3fr 2fr;
  gap: 10px;
}

.ep-captcha-box {
  border: none;
  border-radius: 8px;
  background: #cfd4d9;
  color: #364046;
  font-size: 24px;
  font-style: italic;
  letter-spacing: 0.2em;
  transform: skewX(-12deg);
  cursor: pointer;
  user-select: none;
}

.ep-form-actions {
  margin-top: -2px;
}

:deep(.ep-form-actions .el-checkbox__label) {
  color: #fff;
}

:deep(.ep-form-actions .el-checkbox__input.is-checked .el-checkbox__inner) {
  background-color: #fff;
  border-color: #fff;
}

:deep(.ep-form-actions .el-checkbox__input.is-checked .el-checkbox__inner::after) {
  border-color: #176847;
}

.ep-submit-btn {
  height: 42px;
  border: none;
  border-radius: 8px;
  background: #fff;
  color: #176847;
  font-size: 18px;
  font-weight: 800;
  letter-spacing: 0.5em;
  cursor: pointer;
}

.ep-submit-btn:disabled {
  opacity: 0.72;
  cursor: not-allowed;
}

.ep-account-list,
.ep-account-grid {
  display: grid;
  gap: 8px;
}

.ep-account-list {
  margin-top: 12px;
}

.ep-account-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
  padding: 9px 10px;
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.12);
  color: #fff;
}

.ep-account-item span {
  font-size: 12px;
  opacity: 0.9;
}

.ep-account-item strong {
  font-size: 13px;
}

.error {
  color: #ffd3d3;
  margin: 0 0 8px;
}

.ok {
  color: #d9ffe4;
  margin: 0 0 8px;
}

@media (max-width: 980px) {
  .ep-login-card {
    grid-template-columns: 1fr;
    padding: 16px;
    gap: 14px;
  }

  .ep-brand-pane {
    padding: 20px 14px;
  }

  .ep-brand-pane h1 {
    font-size: 30px;
  }

  .ep-brand-pane h2 {
    font-size: 22px;
  }
}
</style>
