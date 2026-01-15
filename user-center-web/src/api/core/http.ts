import axios from 'axios'
import { isApiEnvelope } from './error'
import type { ApiError } from './types'

/* 函数级注释：创建一个带基础配置的 Axios 实例，用于全局发请求。
   小白理解：这是“统一打电话的手机设置”，比如默认前缀、自动带登录票。 */
export const http = axios.create({
  baseURL: import.meta.env.VITE_API_BASE || '/',
  withCredentials: true,
  timeout: 15000,
})

http.interceptors.response.use(
  (res) => {
    const payload = res.data as unknown
    if (isApiEnvelope(payload)) {
      const code = Number(payload.code)
      const message = typeof payload.message === 'string' ? payload.message : String(payload.message ?? '')
      if (code === 0) {
        res.data = payload.data
        return res
      }
      const err: ApiError = new Error(message || '请求失败')
      err.code = code
      return Promise.reject(err)
    }
    return res
  },
  (e: unknown) => {
    if (axios.isAxiosError(e)) {
      const payload = e.response?.data as unknown
      if (isApiEnvelope(payload)) {
        const code = Number(payload.code)
        const message = typeof payload.message === 'string' ? payload.message : String(payload.message ?? '')
        const err: ApiError = new Error(message || '请求失败')
        err.code = Number.isFinite(code) ? code : -1
        return Promise.reject(err)
      }
    }
    return Promise.reject(e)
  },
)

