package com.yupi.user_center.interceptor;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yupi.user_center.constant.UserConstant;
import com.yupi.user_center.model.Role;
import com.yupi.user_center.model.User;
import com.yupi.user_center.model.UserRole;
import com.yupi.user_center.service.RoleService;
import com.yupi.user_center.service.UserRoleService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 函数级注释：管理员接口鉴权拦截器（给 /admin/** 用）。
 * 小白理解：相当于“保安”，所有 /admin 开头的请求先过来检查：你是不是管理员？
 */
@Component
public class AdminAuthInterceptor implements HandlerInterceptor {

    private static final String ROLE_KEY_ADMIN = "ADMIN";

    @Resource
    private RoleService roleService;

    @Resource
    private UserRoleService userRoleService;

    /**
     * 函数级注释：请求进入 Controller 前执行鉴权。
     * 小白理解：在真正执行接口方法之前，先检查 Session 里有没有登录用户；再检查是不是管理员。
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        Object userObject = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        User user = (User) userObject;
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "未登录");
        }

        if (user.getUserRole() != null && user.getUserRole() == UserConstant.ADMIN_ROLE) {
            return true;
        }

        QueryWrapper<Role> roleQw = new QueryWrapper<>();
        roleQw.eq("role_key", ROLE_KEY_ADMIN);
        roleQw.eq("isDelete", 0);
        roleQw.eq("status", 0);
        Role adminRole = roleService.getOne(roleQw);
        if (adminRole == null || adminRole.getId() == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权限");
        }

        QueryWrapper<UserRole> urQw = new QueryWrapper<>();
        urQw.eq("user_id", user.getId());
        urQw.eq("role_id", adminRole.getId());
        boolean isAdmin = userRoleService.count(urQw) > 0;
        if (!isAdmin) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权限");
        }
        return true;
    }
}

