package com.yupi.user_center.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户-角色关联实体
 * 作用：对应数据库 user_role 表，记录“哪个用户拥有哪些角色”。
 * 小白理解：这是“绑定关系表”，用它把 user 和 role 这两张表连起来。
 */
@Data
@TableName("user_role")
public class UserRole {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private Long userId;

    @TableField("role_id")
    private Long roleId;

    @TableField("createTime")
    private LocalDateTime createTime;
}

