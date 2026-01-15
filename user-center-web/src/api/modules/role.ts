import type { RoleDTO } from '../dto'
import { http } from '../core/http'

/* 函数级注释：管理员获取“角色列表”（用于多选框展示）。
   小白理解：先把所有角色拿回来，页面才能让你勾选。 */
export async function listRoles() {
  const res = await http.get('/admin/role/list')
  return res.data as Array<RoleDTO>
}

