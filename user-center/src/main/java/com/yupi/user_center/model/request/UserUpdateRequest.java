package com.yupi.user_center.model.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * 管理员更新用户请求体。
 *
 * @author Ethan
 */
@Data
public class UserUpdateRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull(message = "用户 id 不能为空")
    @Positive(message = "用户 id 必须大于 0")
    private Long id;

    @Pattern(regexp = "^(?!\\s*$).+", message = "用户名不能为空")
    private String name;

    @Size(min = 4, max = 64, message = "账号长度必须在 4~64 位")
    @Pattern(regexp = "^[A-Za-z0-9_]+$", message = "账号只能包含字母、数字、下划线")
    private String userAccount;

    @Pattern(regexp = "^$|^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$", message = "邮箱格式不正确")
    private String email;
    private String avatarUrl;

    @Min(value = 0, message = "性别参数不正确")
    @Max(value = 1, message = "性别参数不正确")
    private Integer gender;

    @Pattern(regexp = "^$|^[0-9]{7,20}$", message = "手机号格式不正确")
    private String phone;

    @Min(value = 0, message = "状态参数不正确")
    @Max(value = 1, message = "状态参数不正确")
    private Integer userStatus;

    @Min(value = 0, message = "角色参数不正确")
    @Max(value = 1, message = "角色参数不正确")
    private Integer userRole;
}

