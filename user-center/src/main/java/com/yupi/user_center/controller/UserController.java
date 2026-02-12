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
 * 用户相关接口（注册、登录、获取当前用户、更新个人资料等）。
 *
 * @author Ethan
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    /**
     * 用户注册接口。
     *
     * <p>用途：前端提交注册信息，后端完成校验、落库，并返回新用户 id。</p>
     *
     * @param req 注册请求体（包含账号、密码、确认密码）
     * @return 统一返回结构，data 为新用户 id
     */
    @PostMapping("/register")
    public Long userRegister(@Valid @RequestBody UserRegisterRequest req) {
        return userService.userRegister(req.getUserAccount(), req.getUserPassword(), req.getCheckPassword());
    }

    /**
     * 用户登录接口。
     *
     * <p>用途：前端提交账号密码，后端校验成功后写入 Session 登录态，并返回脱敏用户信息。</p>
     *
     * @param req 登录请求体（包含账号、密码）
     * @param request Http 请求对象（用于写入 Session）
     * @return 统一返回结构，data 为脱敏后的用户信息
     */
    @PostMapping("/login")
    public UserVO userLogin(@Valid @RequestBody UserLoginRequest req, HttpServletRequest request) {
        User user = userService.userLogin(req.getUserAccount(), req.getUserPassword(), request);
        return toUserVO(user);
    }

    /**
     * 获取当前登录用户接口。
     *
     * <p>用途：从 Session 读取登录态用户，返回脱敏后的用户信息。</p>
     *
     * @param request Http 请求对象（用于读取 Session）
     * @return 统一返回结构，data 为脱敏后的用户信息
     * @throws ResponseStatusException 未登录时抛出（401）
     */
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
     * 用户退出登录接口。
     *
     * <p>用途：清除 Session 中的登录态。</p>
     *
     * @param request Http 请求对象（用于获取 Session）
     * @return 统一返回结构，data 为是否退出成功
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
     * 用户更新个人资料接口（必须登录）。
     *
     * <p>用途：允许登录用户更新个人可编辑信息（用户名、邮箱、手机、头像、性别）。</p>
     *
     * @param req 更新请求体（包含可编辑字段）
     * @param request Http 请求对象（用于获取当前登录用户）
     * @return 统一返回结构，data 为更新后的脱敏用户信息
     * @throws ResponseStatusException 未登录（401）/被封禁（403）/更新失败（500）时抛出
     * @throws IllegalArgumentException 请求参数为空时抛出
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
     * 会话有效期查询接口。
     *
     * <p>用途：返回当前 Session 的最大不活动时间（秒），可用于验证超时配置。</p>
     *
     * @param request Http 请求对象（用于获取 Session）
     * @return 统一返回结构，data 为 Session 最大不活动时间（秒）
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
