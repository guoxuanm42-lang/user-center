import type { UserDTO, UserUpdateMyPayload } from '../dto'
import { http } from '../core/http'

/* 函数级注释：用户更新自己的资料接口（必须登录）。
   小白理解：我在“个人中心-编辑资料”里改了信息，点保存就是调这个接口。 */
export async function updateMyUser(payload: UserUpdateMyPayload) {
  const res = await http.post('/user/update', payload)
  return res.data as UserDTO
}

