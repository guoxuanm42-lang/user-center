package com.yupi.user_center.constant;

/**
 * 用户常量定义。
 *
 * @author Ethan
 */
public interface UserConstant {

    /**
     * 用户登录态键（Session attribute key）。
     */
    String USER_LOGIN_STATE = "USER_LOGIN_STATE";

    /**
     * 默认角色（普通用户）。
     */
    int DEFAULT_ROLE = 0;

    /**
     * 管理员角色。
     */
    int ADMIN_ROLE = 1;
}
