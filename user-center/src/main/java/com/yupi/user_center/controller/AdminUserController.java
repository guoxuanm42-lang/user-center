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
 * 管理员用户管理接口
 * 作用：提供管理员对用户的查询、删除、更新能力
 * 小白理解：这是“后台管理入口”，只有管理员才可以用
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
     * 管理员查询用户列表
     * 作用：按用户名关键词模糊搜索用户，并返回脱敏后的用户信息
     * 小白理解：管理员输入一个关键词，查“名字像这个”的用户
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
     * 管理员删除用户
     * 作用：按用户 id 删除用户（逻辑删除）
     * 小白理解：管理员点“删除”，后端就把这个用户标记为已删除
     */
    @PostMapping("/delete")
    public boolean deleteUser(@RequestBody Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("请求参数不能为空");
        }
        return userService.removeById(id);
    }

    /**
     * 管理员更新用户信息
     * 作用：更新用户可编辑字段（账号、邮箱、手机、角色、状态等）
     * 小白理解：管理员在弹窗里改了资料，点保存后把新数据写回数据库
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
     * 查询某个用户当前拥有的角色 id 列表
     * 小白理解：用于前端做“角色多选框回显”，告诉你这个用户目前勾选了哪些角色。
     */
    @GetMapping("/roles")
    public List<Long> getUserRoleIds(Long userId) {
        return userRoleService.listRoleIdsByUserId(userId);
    }

    /**
     * 给用户分配角色（先删后插）
     * 小白理解：把这个用户原来的角色清掉，然后把你提交的角色重新绑定上去。
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
