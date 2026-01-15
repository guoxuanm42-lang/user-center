package com.yupi.user_center.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yupi.user_center.mapper.RoleMapper;
import com.yupi.user_center.model.Role;
import org.springframework.stereotype.Service;

/**
 * 角色服务实现类
 * 作用：把 MyBatis-Plus 的通用 CRUD 能力封装成 RoleService。
 * 小白理解：这里主要是“把现成的增删改查能力接起来”，让 Controller 可以直接调用。
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {
}

