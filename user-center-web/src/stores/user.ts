import { defineStore } from 'pinia'
import { getCurrentUser, login, logout } from '@/api/http'
import type { UserDTO } from '@/api/dto'

/* 函数级注释：用户状态仓库，保存当前登录用户信息。
   小白理解：这是“全局的盒子”，装着用户信息，任何页面都能拿到。 */
export const useUserStore = defineStore('user', {
  state: () => ({
    currentUser: null as null | UserDTO,
    _loginStateInited: false,
    _loginStateInitPromise: null as null | Promise<void>,//防止“并发初始化登录态”
  }),
  actions: {
    async initLoginState() {
      if (this._loginStateInited) return
      if (this._loginStateInitPromise) return this._loginStateInitPromise
      this._loginStateInitPromise = (async () => {
        const user = await getCurrentUser()
        this.currentUser = user//给currentUser赋值
        this._loginStateInited = true
        this._loginStateInitPromise = null
      })().catch((e) => {
        this.currentUser = null
        this._loginStateInited = true
        this._loginStateInitPromise = null
        throw e
      })
      return this._loginStateInitPromise
    },

    /* 函数级注释：执行登录并把返回的用户信息保存到全局状态。
       小白理解：登录成功后，把“我是谁”写进全局的盒子里。 */
    async doLogin(account: string, password: string) {
      const user = await login(account, password)
      if (!user || typeof user.id !== 'number') {
        throw new Error('账号或密码错误')
      }
      this.currentUser = user
      this._loginStateInited = true
    },

    /* 函数级注释：更新当前用户信息（一般用于“保存资料”后同步到全局）。
       小白理解：我修改成功后，把最新的“我是谁”重新塞回全局盒子里。 */
    setCurrentUser(next: null | UserDTO) {
      this.currentUser = next
      this._loginStateInited = true
    },

    /* 函数级注释：退出登录，清掉前端状态并通知后端销毁 Session。
       小白理解：先告诉后端“我退出了”，再把浏览器里记住的用户信息都清空。 */
    async doLogout() {
      try {
        await logout().catch(() => {})
      } finally {
        this.currentUser = null
        this._loginStateInited = false
        this._loginStateInitPromise = null
        try {
          sessionStorage.clear()
        } catch (e) {
          void e
        }
        try {
          localStorage.clear()
        } catch (e) {
          void e
        }
      }
    },
  },
})
