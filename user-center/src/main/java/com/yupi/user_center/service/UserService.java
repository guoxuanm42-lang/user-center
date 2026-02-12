package com.yupi.user_center.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.yupi.user_center.model.User;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 用户服务接口（用户注册、登录与脱敏）。
 *
 * @author Ethan
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册。
     *
     * @param userAccount 用户账户
     * @param userPassword 用户密码
     * @param checkPassword 校验密码
     * @return 新用户 id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);

    /**
     * 用户登录
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @param request      HttpServletRequest对象，用于记录登录状态
     * @return 脱敏后的用户信息
     */
    User userLogin(String userAccount, String userPassword, HttpServletRequest request);


    /**
     * 用户脱敏（移除敏感字段）。
     *
     * @param originUser 原始用户对象
     * @return 脱敏后的用户对象
     */
    User getSafetyUser(User originUser);
}
