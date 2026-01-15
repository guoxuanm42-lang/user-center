export type UserDTO = {
  id: number
  name: string
  userAccount?: string
  email?: string
  userPassword?: string
  avatarUrl?: string
  gender?: number | null
  phone?: string
  userStatus?: number
  userRole?: number
  roleId?: number
  roleName?: string
  roleKey?: string
  createTime?: string
}

export type UserUpdatePayload = {
  id: number
  name?: string
  userAccount?: string
  email?: string
  avatarUrl?: string
  gender?: number | null
  phone?: string
  userStatus?: number
  userRole?: number
}

export type UserUpdateMyPayload = {
  name?: string
  email?: string
  avatarUrl?: string
  gender?: number | null
  phone?: string
}

export type RoleDTO = {
  id: number
  roleKey: string
  roleName: string
  description?: string
  status?: number
}

export type UserAssignRolesPayload = {
  userId: number
  roleIds: number[]
}

