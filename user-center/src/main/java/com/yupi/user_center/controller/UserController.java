package com.yupi.user_center.controller;

import com.yupi.user_center.constant.UserConstant;
import com.yupi.user_center.model.User;
import com.yupi.user_center.model.request.UserLoginRequest;
import com.yupi.user_center.model.request.UserRegisterRequest;
import com.yupi.user_center.model.request.UserUpdateMyRequest;
import com.yupi.user_center.model.vo.UserVO;
import com.yupi.user_center.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * 用户接口
 * 作用：提供用户注册与登录的 HTTP 接口
 * 小白理解：这是“门口的接待员”，把前端发来的数据交给后台服务处理
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @PostMapping("/register")
    public Long userRegister(@Valid @RequestBody UserRegisterRequest req) {
        return userService.userRegister(req.getUserAccount(), req.getUserPassword(), req.getCheckPassword());
    }

    @PostMapping("/login")
    public UserVO userLogin(@Valid @RequestBody UserLoginRequest req, HttpServletRequest request) {
        User user = userService.userLogin(req.getUserAccount(), req.getUserPassword(), request);
        return toUserVO(user);
    }

    @GetMapping("/current")
    public UserVO getCurrentUser(HttpServletRequest request) {
        Object userObject = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        User user = (User) userObject;
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "NOT_LOGIN");
        }
        return toUserVO(userService.getSafetyUser(user));
    }

    /**
     * 用户退出登录
     * 作用：清除 Session 中的登录态
     * 小白理解：告诉后端“我退出了”，把登录票作废
     */
    @PostMapping("/logout")
    public Boolean userLogout(HttpServletRequest request) {
        try {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.removeAttribute(UserConstant.USER_LOGIN_STATE);
                session.invalidate();
            }
        } catch (IllegalStateException ignored) {
        }
        return true;
    }

    /**
     * 用户更新自己的资料（必须登录）
     * 作用：允许用户修改自己的部分可编辑信息（用户名、邮箱、手机、头像、性别）
     * 小白理解：我在“编辑资料”里改了内容，点保存就走这个接口
     */
    @PostMapping("/update")
    public UserVO updateMyUser(@Valid @RequestBody UserUpdateMyRequest req, HttpServletRequest request) {
        Object userObject = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        User loginUser = (User) userObject;
        if (loginUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "NOT_LOGIN");
        }
        if (loginUser.getUserStatus() != null && loginUser.getUserStatus() == 1) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "USER_BANNED");
        }
        if (req == null) {
            throw new IllegalArgumentException("请求参数不能为空");
        }

        User toUpdate = new User();
        toUpdate.setId(loginUser.getId());

        if (req.getName() != null) {
            toUpdate.setName(req.getName());
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

        boolean ok = userService.updateById(toUpdate);
        if (!ok) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "UPDATE_FAILED");
        }

        User fresh = userService.getById(loginUser.getId());
        User safetyUser = userService.getSafetyUser(fresh);
        request.getSession().setAttribute(UserConstant.USER_LOGIN_STATE, safetyUser);
        return toUserVO(safetyUser);
    }




/**
     * 会话有效期查询接口
     * 作用：返回当前 Session 的最大不活动时间（秒），用于验证超时配置
     * 小白理解：告诉你“这个登录会话多久不操作会自动过期”
     */
    @GetMapping("/session/ttl")
    public Integer getSessionTtl(HttpServletRequest request) {
        return request.getSession().getMaxInactiveInterval();
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
