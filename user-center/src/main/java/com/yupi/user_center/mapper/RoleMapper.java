package com.yupi.user_center.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yupi.user_center.model.Role;

/**
 * 角色表 Mapper
 * 作用：对 role 表做增删改查。
 * 小白理解：这是操作 role 表的“遥控器”，不用手写 SQL 也能查/增/改/删。
 */
public interface RoleMapper extends BaseMapper<Role> {
}

