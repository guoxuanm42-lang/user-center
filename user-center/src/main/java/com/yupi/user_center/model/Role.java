package com.yupi.user_center.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 角色实体
 * 作用：对应数据库 role 表，存放“有哪些角色”（管理员、司机等）。
 * 小白理解：这是“角色字典”，只负责记录角色本身，不记录谁拥有什么角色。
 */
@Data
@TableName("role")
public class Role {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("role_key")
    private String roleKey;

    @TableField("role_name")
    private String roleName;

    private String description;

    /**
     * 0-启用 1-禁用
     */
    private Integer status;

    @TableField("createTime")
    private LocalDateTime createTime;

    @TableField("updateTime")
    private LocalDateTime updateTime;

    @TableLogic(value = "0", delval = "1")
    @TableField("isDelete")
    private Integer isDelete;
}

