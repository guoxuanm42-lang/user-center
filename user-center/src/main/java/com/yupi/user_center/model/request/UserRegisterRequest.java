package com.yupi.user_center.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * 注册请求体
 * 作用：承载前端提交的注册参数 userAccount、userPassword、checkPassword
 * 小白理解：就是一个“装账号和两次密码”的小盒子，后端用它来接收数据
 */
@Data
public class UserRegisterRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "账号不能为空")
    @Size(min = 4, max = 64, message = "账号长度必须在 4~64 位")
    @Pattern(regexp = "^[A-Za-z0-9_]+$", message = "账号只能包含字母、数字、下划线")
    private String userAccount;

    @NotBlank(message = "密码不能为空")
    @Size(min = 8, max = 64, message = "密码长度必须在 8~64 位")
    private String userPassword;

    @NotBlank(message = "确认密码不能为空")
    @Size(min = 8, max = 64, message = "确认密码长度必须在 8~64 位")
    private String checkPassword;
}
