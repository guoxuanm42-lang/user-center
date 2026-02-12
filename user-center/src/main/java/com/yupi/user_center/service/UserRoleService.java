package com.yupi.user_center.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yupi.user_center.model.UserRole;

import java.util.List;

/**
 * 用户-角色关联服务接口。
 *
 * @author Ethan
 */
public interface UserRoleService extends IService<UserRole> {

    /**
     * 给用户重新分配角色（先删后插，保证最终结果和勾选一致）。
     *
     * @param userId 用户 id
     * @param roleIds 角色 id 列表
     * @return 是否分配成功
     * @throws IllegalArgumentException userId 不合法 / 违反单角色约束时抛出
     */
    boolean assignRoles(Long userId, List<Long> roleIds);

    /**
     * 查询用户拥有的角色 id 列表。
     *
     * @param userId 用户 id
     * @return 角色 id 列表
     * @throws IllegalArgumentException userId 不合法时抛出
     */
    List<Long> listRoleIdsByUserId(Long userId);
}
