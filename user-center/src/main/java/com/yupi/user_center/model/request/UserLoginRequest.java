package com.yupi.user_center.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * 登录请求体。
 *
 * @author Ethan
 */
@Data
public class UserLoginRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "账号不能为空")
    @Size(min = 4, max = 64, message = "账号长度必须在 4~64 位")
    @Pattern(regexp = "^[A-Za-z0-9_]+$", message = "账号只能包含字母、数字、下划线")
    private String userAccount;

    @NotBlank(message = "密码不能为空")
    @Size(min = 8, max = 64, message = "密码长度必须在 8~64 位")
    private String userPassword;
}
