import axios from 'axios'
import type { UserDTO } from '../dto'
import { getErrorCode } from '../core/error'
import { http } from '../core/http'

/* 函数级注释：用户登录接口，向后端 /user/login 发送账号密码。
   小白理解：把账号密码发给后端，浏览器自动保存“登录票”（Cookie）。 */
export async function login(userAccount: string, userPassword: string) {
  const res = await http.post('/user/login', { userAccount, userPassword })
  return res.data as null | UserDTO
}

/* 函数级注释：获取当前登录用户信息（后端从 Session 识别你是谁）。
   小白理解：浏览器会自动带上“登录票”，后端看票就知道你是谁，然后返回你的信息。 */
export async function getCurrentUser() {
  try {
    const res = await http.get('/user/current')
    return res.data as null | UserDTO
  } catch (e: unknown) {
    const code = getErrorCode(e)
    if (code === 40100) {
      return null
    }
    if (axios.isAxiosError(e) && e.response?.status === 401) {
      return null
    }
    throw e
  }
}

/* 函数级注释：用户退出登录接口，向后端 /user/logout 发送请求清除 Session。
   小白理解：告诉后端“我退出了”，后端把我的登录票作废。 */
export async function logout() {
  const res = await http.post('/user/logout')
  return res.data as boolean
}

/* 函数级注释：用户注册接口，向后端 /user/register 发送账号密码与确认密码。
   小白理解：把“我要注册的新账号和两次一致的密码”交给后端，成功会返回新用户的编号。 */
export async function register(userAccount: string, userPassword: string, checkPassword: string) {
  const res = await http.post('/user/register', { userAccount, userPassword, checkPassword })
  return res.data as number
}

