package com.yupi.user_center.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yupi.user_center.model.UserRole;

/**
 * 用户-角色关联表 Mapper
 * 作用：对 user_role 表做增删改查。
 * 小白理解：这是操作 user_role 表的“遥控器”，用来给用户分配角色。
 */
public interface UserRoleMapper extends BaseMapper<UserRole> {
}

