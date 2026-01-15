<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { updateMyUser } from '@/api/http'
import type { UserUpdateMyPayload } from '@/api/dto'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const initializing = ref(true)
const saving = ref(false)

/* 函数级注释：编辑页表单数据。
   小白理解：把输入框里填的内容先放在这里，点保存时一次性提交。 */
const form = reactive<{
  name: string
  email: string
  phone: string
  avatarUrl: string
  gender: number | ''
}>({
  name: '',
  email: '',
  phone: '',
  avatarUrl: '',
  gender: '',
})

const errors = reactive<{
  name: string | null
  email: string | null
  phone: string | null
}>({
  name: null,
  email: null,
  phone: null,
})

const currentUser = computed(() => userStore.currentUser)
const isBanned = computed(() => currentUser.value?.userStatus === 1)

onMounted(() => {
  userStore
    .initLoginState()
    .catch(() => {})
    .finally(() => {
      const u = userStore.currentUser
      form.name = u?.name || ''
      form.email = u?.email || ''
      form.phone = u?.phone || ''
      form.avatarUrl = u?.avatarUrl || ''
      form.gender = u?.gender === null || typeof u?.gender === 'undefined' ? '' : (u.gender as number)
      initializing.value = false
    })
})

/* 函数级注释：校验用户名必填。
   小白理解：用户名不允许空着，不然保存会失败。 */
function validateName() {
  errors.name = form.name.trim() ? null : '用户名是必填项！'
}

/* 函数级注释：校验邮箱格式（允许空）。
   小白理解：如果你填了邮箱，就必须像 xxx@xxx.com 这种样子。 */
function validateEmail() {
  const v = form.email.trim()
  if (!v) {
    errors.email = null
    return
  }
  const pattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  errors.email = pattern.test(v) ? null : '邮箱格式不正确'
}

/* 函数级注释：校验手机号格式（允许空）。
   小白理解：如果你填了手机号，就必须是 7~20 位数字。 */
function validatePhone() {
  const v = form.phone.trim()
  if (!v) {
    errors.phone = null
    return
  }
  const pattern = /^[0-9]{7,20}$/
  errors.phone = pattern.test(v) ? null : '手机号格式不正确'
}

/* 函数级注释：一键校验所有字段。
   小白理解：点保存前，先把每一项都检查一遍。 */
function validateAll() {
  validateName()
  validateEmail()
  validatePhone()
  return !(errors.name || errors.email || errors.phone)
}

/* 函数级注释：保存按钮处理（带权限校验 + 点击态反馈）。
   小白理解：只有登录且没被封禁，才允许把修改发给后端。 */
async function onSave() {
  if (saving.value) return
  if (!currentUser.value) {
    window.alert('未登录，请先登录')
    router.replace('/login')
    return
  }
  if (isBanned.value) {
    window.alert('账号已被封禁，无法更新信息')
    return
  }
  if (!validateAll()) return

  const payload: UserUpdateMyPayload = {
    name: form.name.trim(),
    email: form.email.trim(),
    phone: form.phone.trim(),
    avatarUrl: form.avatarUrl.trim(),
    gender: form.gender === '' ? null : form.gender,
  }

  saving.value = true
  try {
    const updated = await updateMyUser(payload)
    userStore.setCurrentUser(updated)
    window.alert('保存成功')
    router.replace('/profile')
  } catch (e: unknown) {
    const message = e instanceof Error ? e.message : String(e)
    window.alert('保存失败：' + (message || '未知错误'))
  } finally {
    saving.value = false
  }
}

/* 函数级注释：取消按钮处理。
   小白理解：不保存，直接回到个人中心。 */
function onCancel() {
  router.replace('/profile')
}
</script>

<template>
  <div class="edit-page">
    <div class="panel edit-panel">
      <div class="panel-head"><strong>编辑资料</strong></div>
      <div class="panel-body">
        <Transition name="fade-slide" mode="out-in">
          <div v-if="initializing" key="loading" class="skeleton">
            <div class="skeleton-line w-60"></div>
            <div class="skeleton-line w-90"></div>
            <div class="skeleton-line w-80"></div>
          </div>

          <div v-else key="form" class="form-grid">
            <div class="field">
              <div class="label">用户名</div>
              <input v-model="form.name" class="input" :class="{ error: errors.name }" placeholder="请输入用户名" @blur="validateName" @input="errors.name=null" />
              <div v-if="errors.name" class="error-text" role="alert">{{ errors.name }}</div>
            </div>

            <div class="field">
              <div class="label">性别</div>
              <select v-model="form.gender" class="select">
                <option :value="''">未设置</option>
                <option :value="0">男</option>
                <option :value="1">女</option>
              </select>
            </div>

            <div class="field">
              <div class="label">邮箱</div>
              <input v-model="form.email" class="input" :class="{ error: errors.email }" placeholder="可选：xxx@xxx.com" @blur="validateEmail" @input="errors.email=null" />
              <div v-if="errors.email" class="error-text" role="alert">{{ errors.email }}</div>
            </div>

            <div class="field">
              <div class="label">手机</div>
              <input v-model="form.phone" class="input" :class="{ error: errors.phone }" placeholder="可选：7~20 位数字" @blur="validatePhone" @input="errors.phone=null" />
              <div v-if="errors.phone" class="error-text" role="alert">{{ errors.phone }}</div>
            </div>

            <div class="field span-2">
              <div class="label">头像地址</div>
              <input v-model="form.avatarUrl" class="input" placeholder="可选：http(s)://..." />
            </div>

            <div v-if="isBanned" class="hint danger span-2">提示：账号已被封禁，无法保存修改。</div>

            <div class="actions span-2">
              <button class="btn btn-secondary action-btn" :disabled="saving" @click="onCancel">取消</button>
              <button class="btn btn-primary action-btn" :disabled="saving || isBanned" @click="onSave">
                {{ saving ? '保存中...' : '保存' }}
              </button>
            </div>
          </div>
        </Transition>
      </div>
    </div>
  </div>
</template>

<style scoped>
.edit-page {
  max-width: 960px;
  margin: 0 auto;
}
.edit-panel {
  margin: 16px auto;
}
.form-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}
.field {
  background: var(--bg-soft);
  border: 1px solid var(--panel-border);
  border-radius: 10px;
  padding: 10px;
}
.span-2 {
  grid-column: 1 / -1;
}
.actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
.action-btn:active:not(:disabled) {
  transform: translateY(1px);
}
.error-text {
  color: #e74c3c;
  font-size: 12px;
  margin-top: 4px;
}
.input.error {
  border-color: #e74c3c;
}
.hint {
  color: var(--text-secondary);
  font-size: 13px;
}
.hint.danger {
  color: #dc2626;
}
.skeleton {
  padding: 8px 0;
}
.skeleton-line {
  height: 12px;
  border-radius: 999px;
  background: rgba(0, 0, 0, 0.06);
  margin: 10px 0;
}
.w-60 {
  width: 60%;
}
.w-80 {
  width: 80%;
}
.w-90 {
  width: 90%;
}
.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: opacity 160ms ease, transform 160ms ease;
}
.fade-slide-enter-from,
.fade-slide-leave-to {
  opacity: 0;
  transform: translateY(8px);
}
@media (max-width: 720px) {
  .form-grid {
    grid-template-columns: 1fr;
  }
}
</style>
