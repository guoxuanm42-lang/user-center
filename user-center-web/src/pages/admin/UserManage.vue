<script setup lang="ts">
import { computed, nextTick, onBeforeUnmount, onMounted, reactive, ref, watch } from 'vue'
import {
  assignUserRoles,
  deleteUser,
  getUserRoleIds,
  listRoles,
  searchUsers,
  updateUser,
} from '@/api/http'
import type { RoleDTO, UserAssignRolesPayload, UserDTO, UserUpdatePayload } from '@/api/dto'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const loading = ref(false)
const keyword = ref('')
const roleFilter = ref<number | ''>('')
const statusFilter = ref<number | ''>('')
const page = ref(1)
const pageSize = ref(10)
const users = ref<UserDTO[]>([])

const allRoles = ref<RoleDTO[]>([])

const filteredUsers = computed(() => {
  let list = users.value
  if (roleFilter.value !== '') {
    list = list.filter((u) => u.userRole === roleFilter.value)
  }
  if (statusFilter.value !== '') {
    list = list.filter((u) => u.userStatus === statusFilter.value)
  }
  return list
})

const total = computed(() => filteredUsers.value.length)
const totalPages = computed(() => Math.max(1, Math.ceil(total.value / pageSize.value)))

const pagedUsers = computed(() => {
  const start = (page.value - 1) * pageSize.value
  return filteredUsers.value.slice(start, start + pageSize.value)
})

async function refresh() {
  loading.value = true
  try {
    const data = await searchUsers(keyword.value)
    users.value = data
    page.value = 1
  } finally {
    loading.value = false
  }
}

async function refreshKeepPage() {
  const keep = page.value
  loading.value = true
  try {
    const data = await searchUsers(keyword.value)
    users.value = data
    page.value = Math.min(keep, totalPages.value)
  } finally {
    loading.value = false
  }
}

function resetFilters() {
  keyword.value = ''
  roleFilter.value = ''
  statusFilter.value = ''
  page.value = 1
  refresh()
}

function formatRole(role: number | undefined) {
  if (role === 1) return '管理员'
  return '普通用户'
}

function formatStatus(status: number | undefined) {
  if (status === 1) return '封禁'
  return '正常'
}

async function onDelete(u: UserDTO) {
  if (!u?.id) return
  const ok = window.confirm(`确认删除用户：${u.name || u.userAccount || u.id}？`)
  if (!ok) return
  const res = await deleteUser(u.id)
  if (!res) {
    window.alert('删除失败：后端返回 false（通常是权限不足或参数不对）')
    return
  }
  await refresh()
}

const editVisible = ref(false)
const saving = ref(false)
const editTitleId = 'edit-user-title'
const dialogRef = ref<HTMLElement | null>(null)
const previouslyFocused = ref<HTMLElement | null>(null)

const detailVisible = ref(false)
const detailTitleId = 'detail-user-title'
const detailDialogRef = ref<HTMLElement | null>(null)
const detailUser = ref<UserDTO | null>(null)
const showPhoneFull = ref(false)

const assignVisible = ref(false)
const assignTitleId = 'assign-role-title'
const assignDialogRef = ref<HTMLElement | null>(null)
const assignUserId = ref<number | null>(null)
const assignUserName = ref('')
const assignSelectedRoleId = ref<number>(0)
const assignSaving = ref(false)

const isAdmin = computed(() => userStore.currentUser?.userRole === 1)

const editForm = reactive<{
  id: number
  name: string
  userAccount: string
  email: string
  phone: string
  userRole: number
  userStatus: number
  avatarUrl: string
  gender: number | ''
}>({
  id: 0,
  name: '',
  userAccount: '',
  email: '',
  phone: '',
  userRole: 0,
  userStatus: 0,
  avatarUrl: '',
  gender: '',
})

const editErrors = reactive<{
  name: string | null
  userAccount: string | null
  email: string | null
  phone: string | null
}>({
  name: null,
  userAccount: null,
  email: null,
  phone: null,
})

function openEdit(u: UserDTO) {
  previouslyFocused.value = document.activeElement as HTMLElement | null

  editForm.id = u.id
  editForm.name = u.name || ''
  editForm.userAccount = u.userAccount || ''
  editForm.email = u.email || ''
  editForm.phone = u.phone || ''
  editForm.userRole = u.userRole ?? 0
  editForm.userStatus = u.userStatus ?? 0
  editForm.avatarUrl = u.avatarUrl || ''
  editForm.gender = (u.gender ?? '') as number | ''

  editErrors.name = null
  editErrors.userAccount = null
  editErrors.email = null
  editErrors.phone = null

  editVisible.value = true
}

function closeEdit(arg?: boolean | Event) {
  const force = arg === true
  if (!force && saving.value) return
  editVisible.value = false
}

/* 函数级注释：打开“详情”弹窗并把当前行用户数据带进去。
   小白理解：点“详情”时，把这行用户的信息存起来，然后弹出一个窗口展示。 */
function openDetail(u: UserDTO) {
  previouslyFocused.value = document.activeElement as HTMLElement | null
  detailUser.value = u
  showPhoneFull.value = false
  detailVisible.value = true
}

/* 函数级注释：关闭“详情”弹窗。
   小白理解：把弹窗关掉，回到列表页面。 */
function closeDetail() {
  detailVisible.value = false
}

/* 函数级注释：手机号打码显示（默认只露出前 3 后 2）。
   小白理解：手机号属于敏感信息，我们先遮住一部分，避免泄露。 */
function maskPhone(phone?: string) {
  const v = (phone || '').trim()
  if (!v) return '-'
  if (v.length <= 5) return v.replace(/\d/g, '*')
  const head = v.slice(0, 3)
  const tail = v.slice(-2)
  return head + '*'.repeat(Math.max(0, v.length - 5)) + tail
}

/* 函数级注释：把性别数字翻译成中文。
   小白理解：0/1 这种数字不直观，我们换成“男/女/未设置”。 */
function formatGender(gender?: number | null) {
  if (gender === 0) return '男'
  if (gender === 1) return '女'
  return '未设置'
}

const displayPhone = computed(() => {
  const u = detailUser.value
  if (!u?.phone) return '-'
  if (!isAdmin.value) return maskPhone(u.phone)
  return showPhoneFull.value ? u.phone : maskPhone(u.phone)
})

function validateName() {
  editErrors.name = editForm.name.trim() ? null : '用户名是必填项！'
}

function validateUserAccount() {
  const v = editForm.userAccount.trim()
  if (!v) {
    editErrors.userAccount = '账号是必填项！'
    return
  }
  if (v.length < 4) {
    editErrors.userAccount = '账号至少 4 位'
    return
  }
  const pattern = /^[A-Za-z0-9_]+$/
  if (!pattern.test(v)) {
    editErrors.userAccount = '账号只能包含字母、数字、下划线'
    return
  }
  editErrors.userAccount = null
}

function validateEmail() {
  const v = editForm.email.trim()
  if (!v) {
    editErrors.email = null
    return
  }
  const pattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  editErrors.email = pattern.test(v) ? null : '邮箱格式不正确'
}

function validatePhone() {
  const v = editForm.phone.trim()
  if (!v) {
    editErrors.phone = null
    return
  }
  const pattern = /^[0-9]{7,20}$/
  editErrors.phone = pattern.test(v) ? null : '手机号格式不正确'
}

function validateAll() {
  validateName()
  validateUserAccount()
  validateEmail()
  validatePhone()
  return !(editErrors.name || editErrors.userAccount || editErrors.email || editErrors.phone)
}

function getFocusableElements(container: HTMLElement) {
  const selector =
    'a[href],button:not([disabled]),textarea:not([disabled]),input:not([disabled]),select:not([disabled]),[tabindex]:not([tabindex="-1"])'
  const all = Array.from(container.querySelectorAll<HTMLElement>(selector))
  return all.filter((el) => !el.hasAttribute('disabled') && el.getAttribute('aria-hidden') !== 'true' && el.tabIndex !== -1)
}

function onDialogKeydown(e: KeyboardEvent) {
  if (e.key !== 'Tab') return
  const container = detailVisible.value ? detailDialogRef.value : editVisible.value ? dialogRef.value : assignDialogRef.value
  if (!container) return
  const focusables = getFocusableElements(container)
  if (focusables.length === 0) return
  const first = focusables[0]
  const last = focusables[focusables.length - 1]
  const active = document.activeElement as HTMLElement | null

  if (e.shiftKey) {
    if (!active || active === first) {
      e.preventDefault()
      last.focus()
    }
    return
  }

  if (active === last) {
    e.preventDefault()
    first.focus()
  }
}

function onMaskClick(e: MouseEvent) {
  if (e.target !== e.currentTarget) return
  if (detailVisible.value) {
    closeDetail()
    return
  }
  if (assignVisible.value) {
    closeAssign()
    return
  }
  closeEdit()
}

async function onSave() {
  if (!validateAll()) {
    await nextTick()
    const firstInvalidId =
      (editErrors.name && 'edit-name') ||
      (editErrors.userAccount && 'edit-account') ||
      (editErrors.email && 'edit-email') ||
      (editErrors.phone && 'edit-phone') ||
      null
    if (firstInvalidId) {
      const el = document.getElementById(firstInvalidId) as HTMLElement | null
      el?.focus()
    }
    return
  }

  saving.value = true
  try {
    const payload: UserUpdatePayload = {
      id: editForm.id,
      name: editForm.name.trim(),
      userAccount: editForm.userAccount.trim(),
      email: editForm.email.trim(),
      phone: editForm.phone.trim(),
      userRole: editForm.userRole,
      userStatus: editForm.userStatus,
      avatarUrl: editForm.avatarUrl.trim(),
      gender: editForm.gender === '' ? null : editForm.gender,
    }
    const res = await updateUser(payload)
    if (!res) {
      window.alert('保存失败：后端返回 false（通常是权限不足或参数不对）')
      return
    }
    await refreshKeepPage()
    closeEdit(true)
  } catch (e: unknown) {
    const message = e instanceof Error ? e.message : String(e)
    window.alert('保存失败：' + (message || '未知错误'))
  } finally {
    saving.value = false
  }
}

function onWindowKeydown(e: KeyboardEvent) {
  if (!editVisible.value && !detailVisible.value && !assignVisible.value) return
  if (e.key === 'Escape') {
    e.preventDefault()
    if (detailVisible.value) {
      closeDetail()
      return
    }
    if (assignVisible.value) {
      closeAssign()
      return
    }
    closeEdit()
  }
}

watch(
  () => editVisible.value || detailVisible.value || assignVisible.value,
  async (v) => {
    if (v) {
      document.body.style.overflow = 'hidden'
      window.addEventListener('keydown', onWindowKeydown)
      await nextTick()
      const container = detailVisible.value
        ? detailDialogRef.value
        : assignVisible.value
          ? assignDialogRef.value
          : dialogRef.value
      const focusables = container ? getFocusableElements(container) : []
      ;(focusables[0] || container)?.focus?.()
    } else {
      document.body.style.overflow = ''
      window.removeEventListener('keydown', onWindowKeydown)
      previouslyFocused.value?.focus?.()
      previouslyFocused.value = null
    }
  }
)

onBeforeUnmount(() => {
  document.body.style.overflow = ''
  window.removeEventListener('keydown', onWindowKeydown)
})

function prevPage() {
  page.value = Math.max(1, page.value - 1)
}

function nextPage() {
  page.value = Math.min(totalPages.value, page.value + 1)
}

async function initRoles() {
  try {
    const list = await listRoles()
    allRoles.value = list
  } catch {
    allRoles.value = []
  }
}

function openAssign(u: UserDTO) {
  previouslyFocused.value = document.activeElement as HTMLElement | null
  assignUserId.value = u.id
  assignUserName.value = u.name || u.userAccount || String(u.id)
  assignSelectedRoleId.value = 0
  assignVisible.value = true
  getUserRoleIds(u.id)
    .then((ids) => {
      assignSelectedRoleId.value = (ids && ids.length > 0 ? ids[0] : 0) || 0
    })
    .catch(() => {
      assignSelectedRoleId.value = 0
    })
}

function closeAssign() {
  if (assignSaving.value) return
  assignVisible.value = false
}

async function onAssignSave() {
  if (!assignUserId.value) return
  assignSaving.value = true
  try {
    const payload: UserAssignRolesPayload = {
      userId: assignUserId.value,
      roleIds: assignSelectedRoleId.value > 0 ? [assignSelectedRoleId.value] : [],
    }
    const ok = await assignUserRoles(payload)
    if (!ok) {
      window.alert('分配角色失败：后端返回 false')
      return
    }
    window.alert('分配角色成功')
    await refreshKeepPage()
    assignSaving.value = false
    assignVisible.value = false
  } catch (e: unknown) {
    const message = e instanceof Error ? e.message : String(e)
    window.alert('分配角色失败：' + (message || '未知错误'))
  } finally {
    assignSaving.value = false
  }
}

onMounted(() => {
  initRoles()
  refresh()
})
</script>

<template>
  <div>
    <div style="margin-bottom: 10px; color: var(--text-secondary);">后台管理模块 / 用户管理</div>

    <div class="panel" style="margin-bottom: 12px;">
      <div class="panel-head">
        <strong>筛选</strong>
        <div style="color: var(--text-secondary);">共 {{ total }} 条</div>
      </div>
      <div class="panel-body">
        <div class="form-row">
          <div>
            <div class="label">关键词</div>
            <input v-model="keyword" class="input" placeholder="按用户名模糊搜索" @keyup.enter="refresh" />
          </div>
          <div>
            <div class="label">角色</div>
            <select v-model="roleFilter" class="select">
              <option :value="''">全部</option>
              <option :value="0">普通用户</option>
              <option :value="1">管理员</option>
            </select>
          </div>
          <div>
            <div class="label">状态</div>
            <select v-model="statusFilter" class="select">
              <option :value="''">全部</option>
              <option :value="0">正常</option>
              <option :value="1">封禁</option>
            </select>
          </div>
          <button :disabled="loading" class="btn btn-primary" @click="refresh">
            {{ loading ? '查询中...' : '查询' }}
          </button>
          <button :disabled="loading" class="btn btn-secondary" @click="resetFilters">重置</button>
        </div>
      </div>
    </div>

    <div class="panel">
      <div class="panel-head">
        <strong>用户列表</strong>
        <div style="color: var(--text-secondary);">第 {{ page }} / {{ totalPages }} 页</div>
      </div>
      <div class="panel-body">
        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th>用户名</th>
              <th>账号</th>
              <th>邮箱</th>
              <th>手机</th>
              <th>角色</th>
              <th>状态</th>
              <th>创建时间</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="u in pagedUsers" :key="u.id">
              <td>{{ u.id }}</td>
              <td>{{ u.name }}</td>
              <td>{{ u.userAccount }}</td>
              <td>{{ u.email }}</td>
              <td>{{ u.phone }}</td>
              <td>
                <span
                  class="tag"
                  :class="u.roleKey === 'ADMIN' || u.userRole === 1 ? 'tag-primary' : 'tag-plain'"
                >{{ u.roleName || formatRole(u.userRole) }}</span>
              </td>
              <td>
                <span class="tag" :class="u.userStatus === 1 ? 'tag-danger' : 'tag-success'">{{ formatStatus(u.userStatus) }}</span>
              </td>
              <td>{{ u.createTime }}</td>
              <td>
                <div class="op-actions">
                  <button class="btn btn-link" @click="openDetail(u)">详情</button>
                  <button class="btn btn-link" @click="openAssign(u)">分配角色</button>
                  <button class="btn btn-link" @click="openEdit(u)">编辑</button>
                  <button class="btn btn-link" @click="onDelete(u)">删除</button>
                </div>
              </td>
            </tr>
            <tr v-if="pagedUsers.length === 0">
              <td colspan="9">暂无数据</td>
            </tr>
          </tbody>
        </table>

        <div class="pagination">
          <button :disabled="page <= 1" class="btn btn-secondary" @click="prevPage">上一页</button>
          <button :disabled="page >= totalPages" class="btn btn-secondary" @click="nextPage">下一页</button>
          <div style="margin-left: auto; color: var(--text-secondary);">每页</div>
          <select v-model="pageSize" class="select" @change="page=1">
            <option :value="5">5</option>
            <option :value="10">10</option>
            <option :value="20">20</option>
          </select>
          <div style="color: var(--text-secondary);">条</div>
        </div>
      </div>
    </div>

    <transition name="modal">
      <div v-if="editVisible" class="modal-mask" @click="onMaskClick">
        <div
          ref="dialogRef"
          class="modal-panel"
          role="dialog"
          aria-modal="true"
          :aria-labelledby="editTitleId"
          tabindex="-1"
          @keydown="onDialogKeydown"
        >
          <div class="modal-head">
            <strong :id="editTitleId">编辑信息</strong>
          </div>
          <div class="modal-body">
            <div class="form-grid">
              <div>
                <div class="label">用户名</div>
                <input
                  id="edit-name"
                  v-model="editForm.name"
                  class="input"
                  :class="{ error: editErrors.name }"
                  placeholder="请输入用户名"
                  :aria-invalid="!!editErrors.name"
                  :aria-describedby="editErrors.name ? 'edit-name-error' : undefined"
                  @blur="validateName"
                  @input="editErrors.name=null"
                />
                <div v-if="editErrors.name" id="edit-name-error" class="error-text" role="alert">{{ editErrors.name }}</div>
              </div>
              <div>
                <div class="label">账号</div>
                <input
                  id="edit-account"
                  v-model="editForm.userAccount"
                  class="input"
                  :class="{ error: editErrors.userAccount }"
                  placeholder="字母 / 数字 / 下划线"
                  :aria-invalid="!!editErrors.userAccount"
                  :aria-describedby="editErrors.userAccount ? 'edit-account-error' : undefined"
                  @blur="validateUserAccount"
                  @input="editErrors.userAccount=null"
                />
                <div
                  v-if="editErrors.userAccount"
                  id="edit-account-error"
                  class="error-text"
                  role="alert"
                >{{ editErrors.userAccount }}</div>
              </div>

              <div>
                <div class="label">邮箱</div>
                <input
                  id="edit-email"
                  v-model="editForm.email"
                  class="input"
                  :class="{ error: editErrors.email }"
                  placeholder="如：tom@example.com"
                  :aria-invalid="!!editErrors.email"
                  :aria-describedby="editErrors.email ? 'edit-email-error' : undefined"
                  @blur="validateEmail"
                  @input="editErrors.email=null"
                />
                <div v-if="editErrors.email" id="edit-email-error" class="error-text" role="alert">{{ editErrors.email }}</div>
              </div>
              <div>
                <div class="label">手机</div>
                <input
                  id="edit-phone"
                  v-model="editForm.phone"
                  class="input"
                  :class="{ error: editErrors.phone }"
                  placeholder="数字 7-20 位"
                  :aria-invalid="!!editErrors.phone"
                  :aria-describedby="editErrors.phone ? 'edit-phone-error' : undefined"
                  @blur="validatePhone"
                  @input="editErrors.phone=null"
                />
                <div v-if="editErrors.phone" id="edit-phone-error" class="error-text" role="alert">{{ editErrors.phone }}</div>
              </div>

              <div>
                <div class="label">角色</div>
                <select v-model="editForm.userRole" class="select">
                  <option :value="0">普通用户</option>
                  <option :value="1">管理员</option>
                </select>
              </div>
              <div>
                <div class="label">状态</div>
                <select v-model="editForm.userStatus" class="select">
                  <option :value="0">正常</option>
                  <option :value="1">封禁</option>
                </select>
              </div>

              <div>
                <div class="label">性别</div>
                <select v-model="editForm.gender" class="select">
                  <option :value="''">未设置</option>
                  <option :value="0">男</option>
                  <option :value="1">女</option>
                </select>
              </div>

              <div class="grid-span-2">
                <div class="label">头像地址</div>
                <input v-model="editForm.avatarUrl" class="input" placeholder="可选：http(s)://..." />
              </div>
            </div>
          </div>
          <div class="modal-foot">
            <button class="btn btn-secondary" :disabled="saving" @click="closeEdit">取消</button>
            <button class="btn btn-primary" :disabled="saving" @click="onSave">
              {{ saving ? '保存中...' : '保存' }}
            </button>
          </div>
        </div>
      </div>
    </transition>

    <transition name="modal">
      <div v-if="detailVisible" class="modal-mask" @click="onMaskClick">
        <div
          ref="detailDialogRef"
          class="modal-panel"
          role="dialog"
          aria-modal="true"
          :aria-labelledby="detailTitleId"
          tabindex="-1"
        >
          <div class="modal-head">
            <strong :id="detailTitleId">用户详情</strong>
            <button class="btn btn-secondary" @click="closeDetail">关闭</button>
          </div>

          <div class="modal-body">
            <div class="detail-card">
              <div class="detail-main">
                <div class="avatar" aria-hidden="true">
                  <img v-if="detailUser?.avatarUrl" class="avatar-img" :src="detailUser.avatarUrl" alt="" />
                  <div v-else class="avatar-fallback">{{ (detailUser?.name || 'U').slice(0, 1).toUpperCase() }}</div>
                </div>
                <div class="detail-text">
                  <div class="detail-title">{{ detailUser?.name || '-' }}</div>
                  <div class="detail-sub">{{ detailUser?.userAccount ? `账号：${detailUser.userAccount}` : '账号：-' }}</div>
                </div>
              </div>

              <div class="detail-grid">
                <div class="detail-row">
                  <div class="k">邮箱</div>
                  <div class="v">{{ detailUser?.email || '-' }}</div>
                </div>
                <div class="detail-row">
                  <div class="k">手机</div>
                  <div class="v">
                    {{ displayPhone }}
                    <button v-if="isAdmin" class="btn btn-link inline-btn" @click="showPhoneFull = !showPhoneFull">
                      {{ showPhoneFull ? '隐藏' : '显示完整' }}
                    </button>
                  </div>
                </div>
                <div class="detail-row">
                  <div class="k">性别</div>
                  <div class="v">{{ formatGender(detailUser?.gender ?? null) }}</div>
                </div>
                <div class="detail-row">
                  <div class="k">角色</div>
                  <div class="v">
                    <span class="tag" :class="detailUser?.userRole === 1 ? 'tag-primary' : 'tag-plain'">{{ formatRole(detailUser?.userRole) }}</span>
                  </div>
                </div>
                <div class="detail-row">
                  <div class="k">状态</div>
                  <div class="v">
                    <span class="tag" :class="detailUser?.userStatus === 1 ? 'tag-danger' : 'tag-success'">{{ formatStatus(detailUser?.userStatus) }}</span>
                  </div>
                </div>
                <div class="detail-row">
                  <div class="k">创建时间</div>
                  <div class="v">{{ detailUser?.createTime || '-' }}</div>
                </div>
                <div class="detail-row">
                  <div class="k">用户ID</div>
                  <div class="v">{{ detailUser?.id ?? '-' }}</div>
                </div>
              </div>
            </div>
          </div>

          <div class="modal-foot">
            <button class="btn btn-secondary" @click="closeDetail">关闭</button>
          </div>
        </div>
      </div>
    </transition>

    <transition name="modal">
      <div v-if="assignVisible" class="modal-mask" @click="onMaskClick">
        <div
          ref="assignDialogRef"
          class="modal-panel"
          role="dialog"
          aria-modal="true"
          :aria-labelledby="assignTitleId"
          tabindex="-1"
          @keydown="onDialogKeydown"
        >
          <div class="modal-head">
            <strong :id="assignTitleId">分配角色 - {{ assignUserName }}</strong>
          </div>
          <div class="modal-body">
            <div class="label">可选角色</div>
            <div v-if="allRoles.length === 0" style="color: var(--text-secondary); font-size: 14px; margin-top: 4px;">
              暂无可用角色，请先在后端初始化角色数据
            </div>
            <div v-else class="role-checkbox-group">
              <label class="role-checkbox">
                <input
                  v-model="assignSelectedRoleId"
                  class="role-checkbox-input"
                  type="radio"
                  name="assign-role"
                  :value="0"
                />
                <span class="role-checkbox-label">不分配（清空当前角色）</span>
              </label>
              <label
                v-for="r in allRoles"
                :key="r.id"
                class="role-checkbox"
              >
                <input
                  v-model="assignSelectedRoleId"
                  class="role-checkbox-input"
                  type="radio"
                  name="assign-role"
                  :value="r.id"
                />
                <span class="role-checkbox-label">{{ r.roleName }}（{{ r.roleKey }}）</span>
              </label>
            </div>
          </div>
          <div class="modal-foot">
            <button class="btn btn-secondary" :disabled="assignSaving" @click="closeAssign">取消</button>
            <button class="btn btn-primary" :disabled="assignSaving" @click="onAssignSave">
              {{ assignSaving ? '保存中...' : '保存' }}
            </button>
          </div>
        </div>
      </div>
    </transition>
  </div>
</template>

<style scoped>
.op-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

.role-checkbox-group {
  display: flex;
  flex-wrap: wrap;
  gap: 8px 12px;
  margin-top: 6px;
}

.role-checkbox {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 4px 8px;
  border-radius: 999px;
  background: #f5f5f5;
}

.role-checkbox-input {
  margin: 0;
}

.role-checkbox-label {
  font-size: 13px;
}

.detail-card {
  border: 1px solid var(--panel-border);
  border-radius: 10px;
  padding: 12px;
  background: #fff;
}
.detail-main {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px;
  border-radius: 10px;
  background: var(--bg-soft);
}
.avatar {
  width: 44px;
  height: 44px;
  border-radius: 999px;
  overflow: hidden;
  border: 1px solid var(--panel-border);
  background: #fff;
  flex: 0 0 auto;
}
.avatar-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}
.avatar-fallback {
  width: 100%;
  height: 100%;
  display: grid;
  place-items: center;
  font-weight: 700;
  color: var(--brand-primary);
}
.detail-title {
  font-weight: 800;
  color: var(--text-primary);
}
.detail-sub {
  margin-top: 2px;
  color: var(--text-secondary);
  font-size: 13px;
}
.detail-grid {
  margin-top: 10px;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px 14px;
}
.detail-row {
  display: flex;
  justify-content: space-between;
  gap: 10px;
  padding: 10px 10px;
  border-radius: 10px;
  background: var(--bg-soft);
}
.detail-row .k {
  color: var(--text-secondary);
  flex: 0 0 auto;
}
.detail-row .v {
  color: var(--text-primary);
  font-weight: 600;
  text-align: right;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.inline-btn {
  padding: 0;
  margin-left: 8px;
}

.tag {
  display: inline-block;
  padding: 2px 8px;
  border-radius: 999px;
  font-size: 12px;
  border: 1px solid var(--panel-border);
}
.tag-primary {
  background: rgba(53, 205, 195, 0.12);
  color: var(--brand-primary);
  border-color: rgba(53, 205, 195, 0.25);
}
.tag-plain {
  background: #fafafa;
  color: var(--text-secondary);
}
.tag-success {
  background: rgba(34, 197, 94, 0.12);
  color: #16a34a;
  border-color: rgba(34, 197, 94, 0.25);
}
.tag-danger {
  background: rgba(239, 68, 68, 0.12);
  color: #dc2626;
  border-color: rgba(239, 68, 68, 0.25);
}

.modal-mask {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.35);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 16px;
  z-index: 999;
}

.modal-panel {
  width: min(720px, 92vw);
  max-height: calc(100vh - 32px);
  overflow: auto;
  background: #fff;
  border: 1px solid var(--panel-border);
  border-radius: 10px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.18);
  outline: none;
}

.modal-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 14px;
  border-bottom: 1px solid var(--panel-border);
}

.modal-body {
  padding: 12px 14px;
}

.modal-foot {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding: 12px 14px;
  border-top: 1px solid var(--panel-border);
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}

.grid-span-2 {
  grid-column: 1 / -1;
}

@media (max-width: 640px) {
  .form-grid {
    grid-template-columns: 1fr;
  }
  .detail-grid {
    grid-template-columns: 1fr;
  }
}

.error-text {
  color: #e74c3c;
  font-size: 12px;
  margin-top: 4px;
}
.input.error {
  border-color: #e74c3c;
}

.modal-enter-active,
.modal-leave-active {
  transition: opacity 180ms ease;
}
.modal-enter-from,
.modal-leave-to {
  opacity: 0;
}
.modal-enter-active .modal-panel,
.modal-leave-active .modal-panel {
  transition: transform 180ms ease;
}
.modal-enter-from .modal-panel,
.modal-leave-to .modal-panel {
  transform: translateY(10px) scale(0.98);
}

@media (prefers-reduced-motion: reduce) {
  .modal-enter-active,
  .modal-leave-active,
  .modal-enter-active .modal-panel,
  .modal-leave-active .modal-panel {
    transition: none;
  }
}
</style>
