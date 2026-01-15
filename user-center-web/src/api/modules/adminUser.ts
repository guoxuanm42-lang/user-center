import type { UserAssignRolesPayload, UserDTO, UserUpdatePayload } from '../dto'
import { http } from '../core/http'

/* 函数级注释：管理员搜索用户，向 /admin/user/search 传 username 关键字。
   小白理解：输入搜索词，向后端问“有哪些人名字像这个”。 */
export async function searchUsers(keyword: string) {
  const res = await http.get('/admin/user/search', { params: { username: keyword } })
  return res.data as Array<UserDTO>
}

/* 函数级注释：管理员删除用户（按 id 删除）。
   小白理解：把要删除的用户编号发给后端，后端执行删除并返回成功/失败。 */
export async function deleteUser(id: number) {
  const res = await http.post('/admin/user/delete', JSON.stringify(id), {
    headers: { 'Content-Type': 'application/json' },
  })
  return res.data as boolean
}

/* 函数级注释：管理员更新用户信息（比如改名、改状态）。
   小白理解：把一整包“要改的数据”交给后端，后端更新后告诉你成功没。 */
export async function updateUser(payload: UserUpdatePayload) {
  const res = await http.post('/admin/user/update', payload)
  return res.data as boolean
}

/* 函数级注释：管理员查询某个用户当前拥有的角色 id。
   小白理解：打开分配弹窗时，需要先知道“这个用户现在勾了哪些角色”。 */
export async function getUserRoleIds(userId: number) {
  const res = await http.get('/admin/user/roles', { params: { userId } })
  return res.data as number[]
}

/* 函数级注释：管理员给用户分配角色（先删后插）。
   小白理解：把你勾选的角色 id 列表提交给后端，后端会更新 user_role 表。 */
export async function assignUserRoles(payload: UserAssignRolesPayload) {
  const res = await http.post('/admin/user/roles/assign', payload)
  return res.data as boolean
}

