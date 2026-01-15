package com.yupi.user_center.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yupi.user_center.model.Role;
import com.yupi.user_center.model.request.RoleCreateRequest;
import com.yupi.user_center.service.RoleService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 管理员角色管理接口
 * 作用：提供角色列表查询、角色创建等能力。
 * 小白理解：这是“角色字典”的管理入口，只有管理员才能操作。
 */
@RestController
@RequestMapping("/admin/role")
@Validated
public class AdminRoleController {

    @Resource
    private RoleService roleService;

    /**
     * 查询角色列表（启用且未删除）
     * 小白理解：前端下拉框/多选框需要“全部角色”时，就调这个接口。
     */
    @GetMapping("/list")
    public List<Role> listRoles() {
        QueryWrapper<Role> qw = new QueryWrapper<>();
        qw.eq("status", 0);
        qw.eq("isDelete", 0);
        qw.orderByAsc("id");
        return roleService.list(qw);
    }

    /**
     * 创建角色（后续扩展：你想加新角色就调这个接口）
     * 小白理解：把 roleKey + roleName 填好提交，数据库里就多一个角色。
     */
    @PostMapping("/create")
    public Long createRole(@Valid @RequestBody RoleCreateRequest req) {
        String roleKey = req.getRoleKey();
        String roleName = req.getRoleName();

        roleKey = roleKey.trim().toUpperCase();
        roleName = roleName.trim();
        if (roleKey.length() > 64 || roleName.length() > 64) {
            throw new IllegalArgumentException("roleKey/roleName 过长");
        }

        QueryWrapper<Role> existQw = new QueryWrapper<>();
        existQw.eq("role_key", roleKey);
        existQw.eq("isDelete", 0);
        if (roleService.count(existQw) > 0) {
            throw new IllegalArgumentException("roleKey 已存在");
        }

        Role role = new Role();
        role.setRoleKey(roleKey);
        role.setRoleName(roleName);
        role.setDescription(req.getDescription());
        role.setStatus(0);
        boolean ok = roleService.save(role);
        return ok ? role.getId() : -1L;
    }
}
