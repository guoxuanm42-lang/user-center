package com.yupi.user_center.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yupi.user_center.model.Role;

/**
 * 角色服务接口
 * 作用：提供 role 表的业务能力（查询角色列表、创建角色等）。
 * 小白理解：这是“角色业务处理层”，在 Mapper 的基础上再加业务规则校验。
 */
public interface RoleService extends IService<Role> {
}

