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
 * 管理员接口鉴权拦截器（拦截 /admin/** 请求）。
 *
 * @author Ethan
 */
@Component
public class AdminAuthInterceptor implements HandlerInterceptor {

    private static final String ROLE_KEY_ADMIN = "ADMIN";

    @Resource
    private RoleService roleService;

    @Resource
    private UserRoleService userRoleService;

    /**
     * 请求进入 Controller 前执行鉴权。
     *
     * @param request Http 请求对象
     * @param response Http 响应对象
     * @param handler 处理器对象
     * @return 是否放行
     * @throws ResponseStatusException 未登录（401）/无权限（403）时抛出
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
