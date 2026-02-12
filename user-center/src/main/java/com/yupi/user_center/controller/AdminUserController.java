package com.yupi.user_center.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.yupi.user_center.model.Role;
import com.yupi.user_center.model.User;
import com.yupi.user_center.model.UserRole;
import com.yupi.user_center.model.request.UserAssignRolesRequest;
import com.yupi.user_center.model.request.UserUpdateRequest;
import com.yupi.user_center.model.vo.UserVO;
import com.yupi.user_center.service.RoleService;
import com.yupi.user_center.service.UserService;
import com.yupi.user_center.service.UserRoleService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 管理员用户管理接口（用户查询、更新、删除与角色分配）。
 *
 * @author Ethan
 */
@RestController
@RequestMapping("/admin/user")
@Validated
public class AdminUserController {

    @Resource
    private UserService userService;

    @Resource
    private RoleService roleService;

    @Resource
    private UserRoleService userRoleService;

    /**
     * 管理员查询用户列表接口。
     *
     * <p>用途：按用户名关键词模糊搜索用户，并返回脱敏后的用户信息列表。</p>
     *
     * @param username 用户名关键词（可为空）
     * @return 统一返回结构，data 为脱敏后的用户列表
     */
    @GetMapping("/search")
    public List<UserVO> searchUsers(String username) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(username)) {
            queryWrapper.like("username", username);
        }
        List<User> userList = userService.list(queryWrapper);
        List<User> safetyList = userList.stream().map(userService::getSafetyUser).collect(Collectors.toList());
        fillRoleInfo(safetyList);
        return safetyList.stream().map(AdminUserController::toUserVO).collect(Collectors.toList());
    }

    /**
     * 管理员删除用户接口。
     *
     * <p>用途：按用户 id 删除用户（逻辑删除）。</p>
     *
     * @param id 用户 id
     * @return 统一返回结构，data 为是否删除成功
     * @throws IllegalArgumentException id 为空或不合法时抛出
     */
    @PostMapping("/delete")
    public boolean deleteUser(@RequestBody Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("请求参数不能为空");
        }
        return userService.removeById(id);
    }

    /**
     * 管理员更新用户信息接口。
     *
     * <p>用途：更新用户可编辑字段（账号、邮箱、手机、角色、状态等）。</p>
     *
     * @param req 更新请求体（包含用户 id 与待更新字段）
     * @return 统一返回结构，data 为是否更新成功
     * @throws IllegalArgumentException 用户不存在 / 账号已存在等校验失败时抛出
     */
    @PostMapping("/update")
    public boolean updateUser(@Valid @RequestBody UserUpdateRequest req) {
        User exist = userService.getById(req.getId());
        if (exist == null) {
            throw new IllegalArgumentException("用户不存在");
        }

        User toUpdate = new User();
        toUpdate.setId(req.getId());

        if (req.getName() != null) {
            toUpdate.setName(req.getName());
        }

        if (req.getUserAccount() != null) {
            String userAccount = req.getUserAccount();
            QueryWrapper<User> qw = new QueryWrapper<>();
            qw.eq("userAccount", userAccount);
            qw.ne("id", req.getId());
            if (userService.count(qw) > 0) {
                throw new IllegalArgumentException("账号已存在");
            }
            toUpdate.setUserAccount(userAccount);
        }

        if (req.getEmail() != null) {
            toUpdate.setEmail(req.getEmail());
        }

        if (req.getPhone() != null) {
            toUpdate.setPhone(req.getPhone());
        }

        if (req.getAvatarUrl() != null) {
            toUpdate.setAvatarUrl(req.getAvatarUrl());
        }

        if (req.getGender() != null) {
            toUpdate.setGender(req.getGender());
        }

        if (req.getUserRole() != null) {
            toUpdate.setUserRole(req.getUserRole());
        }

        if (req.getUserStatus() != null) {
            toUpdate.setUserStatus(req.getUserStatus());
        }

        return userService.updateById(toUpdate);
    }

    /**
     * 查询用户当前拥有的角色 id 列表接口。
     *
     * <p>用途：用于前端角色选择回显（返回该用户当前绑定的角色 id 列表）。</p>
     *
     * @param userId 用户 id
     * @return 统一返回结构，data 为角色 id 列表
     */
    @GetMapping("/roles")
    public List<Long> getUserRoleIds(Long userId) {
        return userRoleService.listRoleIdsByUserId(userId);
    }

    /**
     * 给用户分配角色接口（先删后插）。
     *
     * <p>用途：把用户原有的角色解绑，再按请求体中的 roleIds 重新绑定。</p>
     *
     * @param req 分配角色请求体（包含 userId 与 roleIds）
     * @return 统一返回结构，data 为是否分配成功
     * @throws IllegalArgumentException 用户不存在 / 角色不存在或禁用 / 违反单角色约束时抛出
     */
    @PostMapping("/roles/assign")
    public boolean assignRoles(@Valid @RequestBody UserAssignRolesRequest req) {
        User exist = userService.getById(req.getUserId());
        if (exist == null) {
            throw new IllegalArgumentException("用户不存在");
        }

        List<Long> roleIds = req.getRoleIds();
        long uniqueCount = roleIds == null ? 0 : roleIds.stream().filter(id -> id != null && id > 0).distinct().count();
        if (uniqueCount > 1) {
            throw new IllegalArgumentException("一个用户只能分配一个角色");
        }
        if (roleIds != null && !roleIds.isEmpty() && uniqueCount > 0) {
            QueryWrapper<Role> roleQw = new QueryWrapper<>();
            roleQw.in("id", roleIds);
            roleQw.eq("isDelete", 0);
            roleQw.eq("status", 0);
            long count = roleService.count(roleQw);
            if (count != roleIds.stream().filter(id -> id != null && id > 0).distinct().count()) {
                throw new IllegalArgumentException("包含不存在或已禁用的角色");
            }
        }

        return userRoleService.assignRoles(req.getUserId(), roleIds);
    }

    /**
     * 给用户列表补充“单角色”信息（roleId / roleName / roleKey）
     * 小白理解：用户表里没有角色名字，所以要去 user_role + role 里查出来再塞回去。
     */
    private void fillRoleInfo(List<User> safetyUsers) {
        if (safetyUsers == null || safetyUsers.isEmpty()) {
            return;
        }
        List<Long> userIds = safetyUsers.stream()
                .map(User::getId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        if (userIds.isEmpty()) {
            return;
        }

        QueryWrapper<UserRole> urQw = new QueryWrapper<>();
        urQw.in("user_id", userIds);
        urQw.orderByAsc("id");
        List<UserRole> relations = userRoleService.list(urQw);

        Map<Long, Long> userIdToRoleId = new HashMap<>();
        for (UserRole ur : relations) {
            if (ur == null || ur.getUserId() == null || ur.getRoleId() == null) {
                continue;
            }
            userIdToRoleId.putIfAbsent(ur.getUserId(), ur.getRoleId());
        }

        Set<Long> roleIds = userIdToRoleId.values().stream().filter(Objects::nonNull).collect(Collectors.toSet());
        if (roleIds.isEmpty()) {
            return;
        }

        QueryWrapper<Role> roleQw = new QueryWrapper<>();
        roleQw.in("id", roleIds);
        roleQw.eq("isDelete", 0);
        roleQw.eq("status", 0);
        List<Role> roleList = roleService.list(roleQw);
        Map<Long, Role> roleMap = roleList.stream()
                .filter(r -> r != null && r.getId() != null)
                .collect(Collectors.toMap(Role::getId, r -> r, (a, b) -> a));

        for (User u : safetyUsers) {
            if (u == null || u.getId() == null) {
                continue;
            }
            Long roleId = userIdToRoleId.get(u.getId());
            if (roleId == null) {
                continue;
            }
            Role role = roleMap.get(roleId);
            if (role == null) {
                continue;
            }
            u.setRoleId(roleId);
            u.setRoleName(role.getRoleName());
            u.setRoleKey(role.getRoleKey());
        }
    }

    private static UserVO toUserVO(User user) {
        if (user == null) {
            return null;
        }
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setName(user.getName());
        vo.setUserAccount(user.getUserAccount());
        vo.setEmail(user.getEmail());
        vo.setAvatarUrl(user.getAvatarUrl());
        vo.setGender(user.getGender());
        vo.setPhone(user.getPhone());
        vo.setUserStatus(user.getUserStatus());
        vo.setUserRole(user.getUserRole());
        vo.setRoleId(user.getRoleId());
        vo.setRoleName(user.getRoleName());
        vo.setRoleKey(user.getRoleKey());
        vo.setCreateTime(user.getCreateTime());
        return vo;
    }

}
