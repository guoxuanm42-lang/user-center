package com.yupi.user_center.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yupi.user_center.mapper.UserRoleMapper;
import com.yupi.user_center.model.UserRole;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 用户-角色关联服务实现类
 * 作用：提供“分配角色 / 查询角色”等业务实现。
 * 小白理解：这里写的是具体做法，比如“先删再插”。
 */
@Service
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole> implements UserRoleService {

    @Override
    @Transactional
    public boolean assignRoles(Long userId, List<Long> roleIds) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("userId 不能为空");
        }

        QueryWrapper<UserRole> removeQw = new QueryWrapper<>();
        removeQw.eq("user_id", userId);
        this.remove(removeQw);

        if (roleIds == null || roleIds.isEmpty()) {
            return true;
        }

        List<Long> uniqueRoleIds = roleIds.stream()
                .filter(Objects::nonNull)
                .filter(id -> id > 0)
                .distinct()
                .collect(Collectors.toList());
        if (uniqueRoleIds.isEmpty()) {
            return true;
        }
        if (uniqueRoleIds.size() > 1) {
            throw new IllegalArgumentException("一个用户只能分配一个角色");
        }

        Long roleId = uniqueRoleIds.get(0);
        UserRole ur = new UserRole();
        ur.setUserId(userId);
        ur.setRoleId(roleId);
        return this.save(ur);
    }

    @Override
    public List<Long> listRoleIdsByUserId(Long userId) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("userId 不能为空");
        }
        QueryWrapper<UserRole> qw = new QueryWrapper<>();
        qw.eq("user_id", userId);
        List<UserRole> list = this.list(qw);
        return list.stream()
                .map(UserRole::getRoleId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
    }
}
