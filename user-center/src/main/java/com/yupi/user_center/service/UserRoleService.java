package com.yupi.user_center.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yupi.user_center.model.UserRole;

import java.util.List;

/**
 * 用户-角色关联服务接口
 * 作用：提供“给用户分配角色 / 查询用户角色”等业务能力。
 * 小白理解：这是“用户角色绑定”的总控台。
 */
public interface UserRoleService extends IService<UserRole> {

    /**
     * 给用户重新分配角色（先删后插，保证最终结果和勾选一致）
     * 小白理解：先把这个用户以前的角色全清掉，再把你现在勾选的角色重新绑定上去。
     */
    boolean assignRoles(Long userId, List<Long> roleIds);

    /**
     * 查询用户拥有的角色 id 列表
     * 小白理解：返回这个用户勾选了哪些角色（只返回 id，方便前端回显）。
     */
    List<Long> listRoleIdsByUserId(Long userId);
}

