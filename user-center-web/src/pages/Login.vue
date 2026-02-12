<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useUserStore } from '@/stores/user'
import { useRoute, useRouter } from 'vue-router'
const account = ref('')
const password = ref('')
const loading = ref(false)
const user = useUserStore()
const router = useRouter()
const route = useRoute()

// 函数级注释：切换登录页密码的显隐状态。
// 小白理解：点一下眼睛，密码从“点点”变成“明文”，再点变回去。
const showPwd = ref(false)
function togglePwd() { showPwd.value = !showPwd.value }

/* 函数级注释：表单校验逻辑，提供“必填项”提示。
   小白理解：如果不填，就在输入框下面显示红字提醒。 */
const errors = reactive<{ account: string | null; password: string | null }>({ account: null, password: null })

function validateAccount() { errors.account = account.value ? null : '账号是必填项！' }
function validatePassword() { errors.password = password.value ? null : '密码是必填项！' }

/* 函数级注释：点击登录按钮时执行，先做前端必填校验，再调后端。
   小白理解：先检查有没有填，再去后端验证。 */
async function onSubmit() {
  validateAccount()
  validatePassword()
  if (errors.account || errors.password) return
  loading.value = true
  try {
    await user.doLogin(account.value, password.value)
    alert('登录成功')
    const redirect = typeof route.query.redirect === 'string' ? route.query.redirect : '/profile'
    await router.replace(redirect)
  } catch (e: unknown) {
    const message = e instanceof Error ? e.message : String(e)
    alert('登录失败：' + (message || '未知错误'))
  } finally {
    loading.value = false
  }
}

function goRegister() {
  router.push('/register')
}
</script>

<template>
  <div class="auth-container">
    <div class="hero">
      <img class="hero-logo" src="https://i.111666.best/image/LC8BmsDPVdCMwMDy2AVc3Z.png" alt="logo" />
      <span class="hero-title">用户中心系统</span>
    </div>

    <div class="panel">
      <div class="panel-head"><strong class="panel-title">账号密码登录</strong></div>
      <div class="panel-body">
        <div class="form-row">
          <div>
            <div class="label">账号</div>
            <div class="input-wrap">
              <span class="icon-left" aria-hidden="true">
                <svg viewBox="0 0 24 24" width="18" height="18"><path fill="#888" d="M12 12c2.761 0 5-2.239 5-5s-2.239-5-5-5-5 2.239-5 5 2.239 5 5 5zm0 2c-4.418 0-8 2.239-8 5v1h16v-1c0-2.761-3.582-5-8-5z"/></svg>
              </span>
              <input v-model="account" class="input with-icon" :class="{ error: errors.account }" placeholder="请输入账号" @blur="validateAccount" @input="errors.account=null" />
            </div>
            <div v-if="errors.account" class="error-text">{{ errors.account }}</div>
          </div>
          <div>
            <div class="label">密码</div>
            <div class="input-wrap">
              <span class="icon-left" aria-hidden="true">
                <svg viewBox="0 0 24 24" width="18" height="18"><path fill="#888" d="M12 17a2 2 0 0 0 2-2v-3h1a3 3 0 1 0-6 0h1v3a2 2 0 0 0 2 2zm8-7h-1V8a7 7 0 1 0-14 0v2H4a2 2 0 0 0-2 2v9a2 2 0 0 0 2 2h16a2 2 0 0 0 2-2v-9a2 2 0 0 0-2-2z"/></svg>
              </span>
              <input v-model="password" :type="showPwd ? 'text' : 'password'" class="input with-icon" :class="{ error: errors.password }" placeholder="请输入密码" @blur="validatePassword" @input="errors.password=null" />
              <button class="icon-right-btn" type="button" :aria-label="showPwd ? '隐藏密码' : '显示密码'" @click="togglePwd">
                <svg v-if="showPwd" viewBox="0 0 24 24" width="18" height="18"><path fill="#666" d="M12 5c-7 0-10 7-10 7s3 7 10 7 10-7 10-7-3-7-10-7zm0 12a5 5 0 1 1 0-10 5 5 0 0 1 0 10z"/></svg>
                <svg v-else viewBox="0 0 24 24" width="18" height="18"><path fill="#666" d="M2 5l2-2 17 17-2 2-3.873-3.873A12.253 12.253 0 0 1 12 19c-7 0-10-7-10-7a18.247 18.247 0 0 1 6.222-6.222L2 5zm10-2c7 0 10 7 10 7a18.318 18.318 0 0 1-3.299 4.5l-2.063-2.063A5 5 0 0 0 12 7c-.61 0-1.19.11-1.728.312L8.5 5.54A18.3 18.3 0 0 1 12 3z"/></svg>
              </button>
            </div>
            <div v-if="errors.password" class="error-text">{{ errors.password }}</div>
          </div>
          <div class="login-links">
            <button class="link-btn" type="button" @click="goRegister">新用户注册</button>
          </div>
          <button class="btn btn-primary btn-block" :disabled="loading" @click="onSubmit">
            {{ loading ? '登录中...' : '登录' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.auth-container { max-width: 300px; margin: 24px auto; zoom: 1.15; }
.input-wrap { position: relative; }
.input.with-icon { padding-left: 30px; padding-right: 34px; }
.icon-left { position: absolute; left: 6px; top: 50%; transform: translateY(-50%); }
.icon-right-btn { position: absolute; right: 6px; top: 50%; transform: translateY(-50%); border: none; background: transparent; cursor: pointer; padding: 2px; }
.icon-right-btn:hover svg { filter: brightness(0.8); }
.hero { display: flex; align-items: center; justify-content: center; gap: 6px; margin-bottom: 8px; }
.hero-logo { height: 42px; }
.hero-title { font-size: 20px; font-weight: 700; color: var(--brand-primary); line-height: 1.1; }
.panel-title { color: var(--brand-primary); }
.panel-head { display: flex; justify-content: center; }
.btn-block { display: block; width: 100%; }
.input { width: 100%; box-sizing: border-box; }
.login-links { display: flex; justify-content: center; margin-top: 6px; }
.link-btn { border: none; background: transparent; color: var(--brand-primary); cursor: pointer; padding: 0; }
.link-btn:hover { text-decoration: underline; }
</style>

<style scoped>
.error-text { color: #e74c3c; font-size: 12px; margin-top: 4px; }
.input.error { border-color: #e74c3c; }
</style>
