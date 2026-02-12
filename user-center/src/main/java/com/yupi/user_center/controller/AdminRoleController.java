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
 * 管理员角色管理接口（角色列表查询与创建）。
 *
 * @author Ethan
 */
@RestController
@RequestMapping("/admin/role")
@Validated
public class AdminRoleController {

    @Resource
    private RoleService roleService;

    /**
     * 查询角色列表接口（启用且未删除）。
     *
     * <p>用途：前端下拉框/多选框加载可选角色时调用。</p>
     *
     * @return 统一返回结构，data 为角色列表
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
     * 创建角色接口。
     *
     * <p>用途：新增一个角色（roleKey 唯一）。</p>
     *
     * @param req 创建角色请求体（包含 roleKey、roleName、description）
     * @return 统一返回结构，data 为新创建的角色 id（失败返回 -1）
     * @throws IllegalArgumentException 角色参数不合法 / roleKey 已存在时抛出
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
