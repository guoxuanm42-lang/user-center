package com.yupi.user_center.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

@Data
@TableName("user")
public class User {


    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    @TableField("username")
    private String name;
    @TableField("userAccount")
    private String userAccount;
    private String email;
    @TableField("userPassword")
    private String userPassword;
    @TableField("avatarUrl")
    private String avatarUrl;
    @TableField("gender")
    private Integer gender;
    @TableField("phone")
    private String phone;
    @TableField("userStatus")
    private Integer userStatus;

    // 普通用户-0  管理员-1
    @TableField("userRole")
    private Integer userRole;
    @TableField("createTime")
    private java.time.LocalDateTime createTime;
    @TableLogic(value = "0", delval = "1")
    @TableField("isDelete")
    private Integer isDelete;

    @TableField(exist = false)
    private Integer age;

    @TableField(exist = false)
    private Long roleId;

    @TableField(exist = false)
    private String roleName;

    @TableField(exist = false)
    private String roleKey;
}


