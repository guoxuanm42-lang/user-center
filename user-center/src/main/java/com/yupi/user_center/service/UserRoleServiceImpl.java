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
 * 用户-角色关联服务实现类。
 *
 * @author Ethan
 */
@Service
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole> implements UserRoleService {

    /**
     * 给用户重新分配角色（先删后插，保证最终结果和勾选一致）。
     *
     * @param userId 用户 id
     * @param roleIds 角色 id 列表
     * @return 是否分配成功
     * @throws IllegalArgumentException userId 不合法 / 违反单角色约束时抛出
     */
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

    /**
     * 查询用户拥有的角色 id 列表。
     *
     * @param userId 用户 id
     * @return 角色 id 列表
     * @throws IllegalArgumentException userId 不合法时抛出
     */
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
