package com.yupi.user_center.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yupi.user_center.mapper.RoleMapper;
import com.yupi.user_center.model.Role;
import org.springframework.stereotype.Service;

/**
 * 角色服务实现类。
 *
 * @author Ethan
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {
}
