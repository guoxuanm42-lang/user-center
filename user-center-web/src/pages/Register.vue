<script setup lang="ts">
import { ref, reactive } from 'vue'
import { register } from '@/api/http'
import { useRouter } from 'vue-router'
const router = useRouter()

// 函数级注释：定义注册表单的响应式字段与密码显隐状态。
// 小白理解：准备账号、两次密码和两个“眼睛开/关”的开关变量。
const account = ref('')
const password = ref('')
const confirm = ref('')
const showPwd1 = ref(false)
const showPwd2 = ref(false)
const loading = ref(false)

// 函数级注释：切换密码输入框的可见性。
// 小白理解：点一下眼睛，就把输入框从“密文”切成“明文”，再点恢复。
function toggle1() { showPwd1.value = !showPwd1.value }
function toggle2() { showPwd2.value = !showPwd2.value }

const errors = reactive<{ account: string | null; password: string | null; confirm: string | null }>({ account: null, password: null, confirm: null })
function validateAccountRequired() { errors.account = account.value ? null : '账号是必填项！' }
function validatePasswordRequired() { errors.password = password.value ? null : '密码是必填项！' }
function validateConfirmRequired() { errors.confirm = confirm.value ? null : '确认密码是必填项！' }
function validateAll(): boolean {
  errors.account = null; errors.password = null; errors.confirm = null
  validateAccountRequired(); validatePasswordRequired(); validateConfirmRequired()
  if (errors.account || errors.password || errors.confirm) return false
  const pattern = /^[A-Za-z0-9_]+$/
  if (account.value.length < 4) { errors.account = '账号至少 4 位' }
  if (!pattern.test(account.value)) { errors.account = '账号只能包含字母、数字、下划线' }
  if (password.value.length < 8) { errors.password = '密码至少 8 位' }
  if (password.value !== confirm.value) { errors.confirm = '两次密码不一致' }
  return !(errors.account || errors.password || errors.confirm)
}

// 函数级注释：点击“注册”时执行，调用后端注册接口并在成功后跳到登录页。
// 小白理解：把填的内容交给后端登记，成功了就去登录。
async function onSubmit() {
  if (!validateAll()) return
  loading.value = true
  try {
    const userId = await register(account.value, password.value, confirm.value)
    alert(`注册成功，用户ID：${userId}，请登录`)
    router.push('/login')
  } catch (e: unknown) {
    const message = e instanceof Error ? e.message : String(e)
    alert('注册失败：' + (message || '未知错误'))
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="auth-container">
    <div class="hero">
      <img class="hero-logo" src="https://www.codefather.cn/_next/static/media/logo.d417684e.png" alt="logo" />
      <span class="hero-title">编程导航知识星球</span>
    </div>


    <div class="panel">
      <div class="panel-head"><strong class="panel-title">账号密码注册</strong></div>
      <div class="panel-body">
        <div class="form-row" style="width:100%;">
          <div style="flex:1 1 100%;">
            <div class="label">账号</div>
            <div class="input-wrap">
              <span class="icon-left" aria-hidden="true">
                <svg viewBox="0 0 24 24" width="18" height="18"><path fill="#888" d="M12 12c2.761 0 5-2.239 5-5s-2.239-5-5-5-5 2.239-5 5 2.239 5 5 5zm0 2c-4.418 0-8 2.239-8 5v1h16v-1c0-2.761-3.582-5-8-5z"/></svg>
              </span>
              <input v-model="account" class="input with-icon" :class="{ error: errors.account }" placeholder="账号：字母数字下划线" @blur="validateAccountRequired" @input="errors.account=null" />
            </div>
            <div v-if="errors.account" class="error-text">{{ errors.account }}</div>
          </div>

          <div style="flex:1 1 100%;">
            <div class="label">密码</div>
            <div class="input-wrap">
              <span class="icon-left" aria-hidden="true">
                <svg viewBox="0 0 24 24" width="18" height="18"><path fill="#888" d="M12 17a2 2 0 0 0 2-2v-3h1a3 3 0 1 0-6 0h1v3a2 2 0 0 0 2 2zm8-7h-1V8a7 7 0 1 0-14 0v2H4a2 2 0 0 0-2 2v9a2 2 0 0 0 2 2h16a2 2 0 0 0 2-2v-9a2 2 0 0 0-2-2z"/></svg>
              </span>
              <input v-model="password" :type="showPwd1 ? 'text' : 'password'" class="input with-icon" :class="{ error: errors.password }" placeholder="至少 8 位" @blur="validatePasswordRequired" @input="errors.password=null" />
              <button class="icon-right-btn" type="button" :aria-label="showPwd1 ? '隐藏密码' : '显示密码'" @click="toggle1">
                <svg v-if="showPwd1" viewBox="0 0 24 24" width="18" height="18"><path fill="#666" d="M12 5c-7 0-10 7-10 7s3 7 10 7 10-7 10-7-3-7-10-7zm0 12a5 5 0 1 1 0-10 5 5 0 0 1 0 10z"/></svg>
                <svg v-else viewBox="0 0 24 24" width="18" height="18"><path fill="#666" d="M2 5l2-2 17 17-2 2-3.873-3.873A12.253 12.253 0 0 1 12 19c-7 0-10-7-10-7a18.247 18.247 0 0 1 6.222-6.222L2 5zm10-2c7 0 10 7 10 7a18.318 18.318 0 0 1-3.299 4.5l-2.063-2.063A5 5 0 0 0 12 7c-.61 0-1.19.11-1.728.312L8.5 5.54A18.3 18.3 0 0 1 12 3z"/></svg>
              </button>
            </div>
            <div v-if="errors.password" class="error-text">{{ errors.password }}</div>
          </div>

          <div style="flex:1 1 100%;">
            <div class="label">确认密码</div>
            <div class="input-wrap">
              <span class="icon-left" aria-hidden="true">
                <svg viewBox="0 0 24 24" width="18" height="18"><path fill="#888" d="M12 17a2 2 0 0 0 2-2v-3h1a3 3 0 1 0-6 0h1v3a2 2 0 0 0 2 2zm8-7h-1V8a7 7 0 1 0-14 0v2H4a2 2 0 0 0-2 2v9a2 2 0 0 0 2 2h16a2 2 0 0 0 2-2v-9a2 2 0 0 0-2-2z"/></svg>
              </span>
              <input v-model="confirm" :type="showPwd2 ? 'text' : 'password'" class="input with-icon" :class="{ error: errors.confirm }" placeholder="再次输入密码" @blur="validateConfirmRequired" @input="errors.confirm=null" />
              <button class="icon-right-btn" type="button" :aria-label="showPwd2 ? '隐藏密码' : '显示密码'" @click="toggle2">
                <svg v-if="showPwd2" viewBox="0 0 24 24" width="18" height="18"><path fill="#666" d="M12 5c-7 0-10 7-10 7s3 7 10 7 10-7 10-7-3-7-10-7zm0 12a5 5 0 1 1 0-10 5 5 0 0 1 0 10z"/></svg>
                <svg v-else viewBox="0 0 24 24" width="18" height="18"><path fill="#666" d="M2 5l2-2 17 17-2 2-3.873-3.873A12.253 12.253 0 0 1 12 19c-7 0-10-7-10-7a18.247 18.247 0 0 1 6.222-6.222L2 5zm10-2c7 0 10 7 10 7a18.318 18.318 0 0 1-3.299 4.5l-2.063-2.063A5 5 0 0 0 12 7c-.61 0-1.19.11-1.728.312L8.5 5.54A18.3 18.3 0 0 1 12 3z"/></svg>
              </button>
            </div>
            <div v-if="errors.confirm" class="error-text">{{ errors.confirm }}</div>
          </div>

          <div style="flex:1 1 100%;">
            <button class="btn btn-primary btn-block" :disabled="loading" @click="onSubmit">
              {{ loading ? '提交中...' : '注册' }}
            </button>
          </div>
        </div>
      </div>
    </div>

    <div style="text-align:center; margin-top:8px;">
      <button class="btn btn-link" @click="$router.push('/login')">已有账号？去登录</button>
    </div>
  </div>
</template>

<style scoped>
.auth-container { max-width: 300px; margin: 24px auto; zoom: 1.15; }
.input-wrap { position: relative; }
.input.with-icon { padding-left: 32px; padding-right: 36px; }
.icon-left { position: absolute; left: 8px; top: 50%; transform: translateY(-50%); }
.icon-right-btn { position: absolute; right: 8px; top: 50%; transform: translateY(-50%); border: none; background: transparent; cursor: pointer; padding: 2px; }
.icon-right-btn:hover svg { filter: brightness(0.8); }

.hero { display: flex; align-items: center; justify-content: center; gap: 10px; margin-bottom: 8px; }
.hero-logo { height: 42px; }
.hero-title { font-size: 20px; font-weight: 700; color: var(--brand-primary); }
.sub-title { text-align: center; margin-bottom: 12px; color: var(--brand-primary); font-weight: 600; }
.panel-title { color: var(--brand-primary); }
.btn-block { display: block; width: 100%; }
.input { width: 100%; box-sizing: border-box; }
.panel-head { display: flex; justify-content: center; }
.error-text { color: #e74c3c; font-size: 12px; margin-top: 4px; }
.input.error { border-color: #e74c3c; }
</style>
