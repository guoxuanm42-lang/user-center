package com.yupi.user_center.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.yupi.user_center.model.User;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 用户服务接口
 * 作用：继承 MyBatis-Plus 的通用 IService，获得 save、update、remove、getById 等常用方法。
 * 小白理解：这是“增删改查”的总控台，直接用现成按钮，不需要自己造轮子。
 */
public interface UserService extends IService<User> {

    

/**
 * 用户注册
 *
 * @param userAccount   用户账户
 * @param userPassword  用户密码
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
     * 用户脱敏
     * @param originUser 原始用户对象
     * @return 脱敏后的用户对象
     */
    User getSafetyUser(User originUser);
    }