package com.yupi.user_center.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 角色实体。
 *
 * @author Ethan
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
