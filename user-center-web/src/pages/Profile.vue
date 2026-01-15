<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
const userStore = useUserStore()
const router = useRouter()
const initializing = ref(true)
const logoutLoading = ref(false)

/* 函数级注释：个人中心页面，展示当前登录用户的基本信息。
   小白理解：这是“我的主页”，用来显示我是谁、一些常用操作入口。 */

onMounted(() => {
  userStore.initLoginState().catch(() => {}).finally(() => { initializing.value = false })
})

const currentUser = computed(() => userStore.currentUser)
const isBanned = computed(() => currentUser.value?.userStatus === 1)
const canEdit = computed(() => Boolean(currentUser.value) && !isBanned.value)
const isAdmin = computed(() => currentUser.value?.userRole === 1)

/* 函数级注释：把角色数字翻译成中文。
   小白理解：后端给的是 0/1，我们把它变成人能看懂的“普通用户/管理员”。 */
function formatRole(role?: number) {
  if (role === 1) return '管理员'
  if (role === 0) return '普通用户'
  return '-'
}

/* 函数级注释：把账号状态数字翻译成中文。
   小白理解：0 表示正常，1 表示封禁。 */
function formatStatus(status?: number) {
  if (status === 1) return '封禁'
  if (status === 0) return '正常'
  return '-'
}

/* 函数级注释：把性别数字翻译成中文。
   小白理解：0/1/2 这种数字，看起来太抽象，我们换成“未知/男/女”。 */
function formatGender(gender?: number | null) {
  if (gender === 0) return '男'
  if (gender === 1) return '女'
  return '未知'
}

/* 函数级注释：更新信息按钮点击处理（带权限校验）。
   小白理解：只有“登录且没被封禁”的用户才允许进入编辑页。 */
function goEdit() {
  if (!canEdit.value) {
    window.alert(isBanned.value ? '账号已被封禁，无法更新信息' : '请先登录')
    return
  }
  router.push('/profile/edit')
}

/* 函数级注释：退出登录按钮点击处理（清除会话并跳回登录页）。
   小白理解：先问一下用户是否确认退出，然后把登录状态清空并回到登录页面。 */
async function onLogout() {
  if (logoutLoading.value) return
  const ok = window.confirm('确认退出登录吗？')
  if (!ok) return
  logoutLoading.value = true
  try {
    await userStore.doLogout()
    await router.replace('/login')
  } finally {
    logoutLoading.value = false
  }
}

/* 函数级注释：进入后台管理模块（管理员入口）。
   小白理解：只有管理员才会看到这个按钮，点了就去后台首页。 */
function goAdmin() {
  router.push('/admin')
}
</script>

<template>
  <div class="profile-page">
    <div class="panel profile-panel">
      <div class="panel-head"><strong>个人中心</strong></div>

      <div class="panel-body">
        <Transition name="fade-slide" mode="out-in">
          <div v-if="initializing" key="loading" class="skeleton">
            <div class="skeleton-line w-40"></div>
            <div class="skeleton-line w-70"></div>
            <div class="skeleton-line w-55"></div>
          </div>

          <div v-else key="content" class="profile-grid">
            <section class="section info-section">
              <div class="section-title">用户信息</div>
              <div class="info-main">
                <div class="avatar" aria-hidden="true">
                  <img v-if="currentUser?.avatarUrl" class="avatar-img" :src="currentUser.avatarUrl" alt="" />
                  <div v-else class="avatar-fallback">{{ (currentUser?.name || 'U').slice(0, 1).toUpperCase() }}</div>
                </div>
                <div class="info-text">
                  <div class="welcome">欢迎，{{ currentUser?.name || '-' }}</div>
                  <div class="sub">{{ currentUser?.userAccount ? `账号：${currentUser.userAccount}` : '账号：-' }}</div>
                </div>
              </div>

              <div class="info-list">
                <div class="info-row">
                  <div class="k">邮箱</div>
                  <div class="v">{{ currentUser?.email || '-' }}</div>
                </div>
                <div class="info-row">
                  <div class="k">手机</div>
                  <div class="v">{{ currentUser?.phone || '-' }}</div>
                </div>
                <div class="info-row">
                  <div class="k">性别</div>
                  <div class="v">{{ formatGender(currentUser?.gender ?? null) }}</div>
                </div>
                <div class="info-row">
                  <div class="k">角色</div>
                  <div class="v">
                    <span class="tag" :class="isAdmin ? 'tag-primary' : 'tag-plain'">{{ formatRole(currentUser?.userRole) }}</span>
                  </div>
                </div>
                <div class="info-row">
                  <div class="k">状态</div>
                  <div class="v">
                    <span class="tag" :class="currentUser?.userStatus === 1 ? 'tag-danger' : 'tag-success'">{{ formatStatus(currentUser?.userStatus) }}</span>
                  </div>
                </div>
                <div class="info-row">
                  <div class="k">创建时间</div>
                  <div class="v">{{ currentUser?.createTime || '-' }}</div>
                </div>
              </div>
            </section>

            <section class="section action-section">
              <div class="section-title">功能操作</div>
              <div class="actions">
                <button class="btn btn-primary action-btn" :disabled="!canEdit" @click="goEdit">更新信息</button>
                <button class="btn btn-secondary action-btn" :disabled="logoutLoading" @click="onLogout">
                  {{ logoutLoading ? '退出中...' : '退出登录' }}
                </button>
                <button v-if="isAdmin" class="btn btn-secondary action-btn" @click="goAdmin">进入后台管理模块</button>
              </div>
              <div v-if="isBanned" class="hint danger">提示：账号已被封禁，部分操作不可用。</div>
            </section>
          </div>
        </Transition>
      </div>
    </div>
  </div>
</template>

<style scoped>
.profile-page {
  max-width: 960px;
  margin: 0 auto;
}
.profile-panel {
  margin: 16px auto;
}
.profile-grid {
  display: grid;
  grid-template-columns: 1.6fr 1fr;
  gap: 12px;
}
.section {
  border: 1px solid var(--panel-border);
  border-radius: 10px;
  background: #fff;
  padding: 12px;
}
.section-title {
  font-weight: 700;
  color: var(--brand-primary);
  margin-bottom: 10px;
}
.info-main {
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
.welcome {
  font-weight: 700;
  color: var(--text-primary);
}
.sub {
  margin-top: 2px;
  color: var(--text-secondary);
  font-size: 13px;
}
.info-list {
  margin-top: 10px;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px 14px;
}
.info-row {
  display: flex;
  justify-content: space-between;
  gap: 10px;
  padding: 10px 10px;
  border-radius: 10px;
  background: var(--bg-soft);
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
.k {
  color: var(--text-secondary);
  flex: 0 0 auto;
}
.v {
  color: var(--text-primary);
  font-weight: 600;
  text-align: right;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.actions {
  display: grid;
  grid-template-columns: 1fr;
  gap: 10px;
}
.action-btn:active:not(:disabled) {
  transform: translateY(1px);
}
.hint {
  margin-top: 10px;
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
.w-40 {
  width: 40%;
}
.w-55 {
  width: 55%;
}
.w-70 {
  width: 70%;
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

@media (max-width: 820px) {
  .profile-grid {
    grid-template-columns: 1fr;
  }
  .info-list {
    grid-template-columns: 1fr;
  }
}
</style>
