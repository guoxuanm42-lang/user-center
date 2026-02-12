package com.yupi.user_center.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户视图对象（脱敏后的用户信息）。
 *
 * @author Ethan
 */
@Data
public class UserVO {
    private Long id;
    private String name;
    private String userAccount;
    private String email;
    private String avatarUrl;
    private Integer gender;
    private String phone;
    private Integer userStatus;
    private Integer userRole;
    private Long roleId;
    private String roleName;
    private String roleKey;
    private LocalDateTime createTime;
}

