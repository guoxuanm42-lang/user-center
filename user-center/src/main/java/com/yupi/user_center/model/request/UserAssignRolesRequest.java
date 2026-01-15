package com.yupi.user_center.model.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 给用户分配角色请求体
 * 作用：管理员在后台勾选角色后，把 userId + roleIds 提交给后端。
 * 小白理解：告诉后端“这个用户现在应该拥有这些角色”。
 */
@Data
public class UserAssignRolesRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull(message = "userId 不能为空")
    @Positive(message = "userId 必须大于 0")
    private Long userId;
    private List<Long> roleIds;
}
