import { createRouter, createWebHistory } from 'vue-router'
import Login from '@/pages/Login.vue'
import Profile from '@/pages/Profile.vue'
import ProfileEdit from '@/pages/ProfileEdit.vue'
import Register from '@/pages/Register.vue'
import AdminLayout from '@/pages/admin/AdminLayout.vue'
import AdminPortal from '@/pages/admin/AdminPortal.vue'
import UserManage from '@/pages/admin/UserManage.vue'
import type { Pinia } from 'pinia'
import { useUserStore } from '@/stores/user'

// 函数级注释：创建应用路由，定义页面与路径对应关系。
// 小白理解：规定“去登录页/用户列表页”的地址。
const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', redirect: '/profile' },
    { path: '/login', component: Login },
    { path: '/profile', component: Profile, meta: { requiresAuth: true } },
    { path: '/profile/edit', component: ProfileEdit, meta: { requiresAuth: true } },
    { path: '/register', component: Register },
    {
      path: '/admin',
      component: AdminLayout,
      meta: { requiresAuth: true, requiresAdmin: true },
      children: [
        { path: '', redirect: '/admin/portal' },
        { path: 'portal', component: AdminPortal },
        { path: 'users', component: UserManage },
      ],
    },
  ],
})

export function setupRouterGuards(pinia: Pinia) {
  router.beforeEach(async (to) => {
    const userStore = useUserStore(pinia)

    if (to.meta.requiresAuth) {
      try {
        await userStore.initLoginState()
      } catch {
        userStore.currentUser = null
      }

      if (!userStore.currentUser) {
        return { path: '/login', query: { redirect: to.fullPath } }
      }
    }

    if (to.meta.requiresAdmin) {
      const role = userStore.currentUser?.userRole
      if (role !== 1) {
        return { path: '/profile' }
      }
    }
  })
}

export default router
