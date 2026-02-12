package com.yupi.user_center.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * 创建角色请求体。
 *
 * @author Ethan
 */
@Data
public class RoleCreateRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "roleKey 不能为空")
    @Size(max = 64, message = "roleKey 不能超过 64 位")
    private String roleKey;

    @NotBlank(message = "roleName 不能为空")
    @Size(max = 64, message = "roleName 不能超过 64 位")
    private String roleName;
    private String description;
}
