package com.yupi.user_center.model.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.io.Serializable;

@Data
public class UserUpdateMyRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    @Pattern(regexp = "^(?!\\s*$).+", message = "用户名不能为空")
    private String name;

    @Pattern(regexp = "^$|^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$", message = "邮箱格式不正确")
    private String email;
    private String avatarUrl;

    @Min(value = 0, message = "性别参数不正确")
    @Max(value = 1, message = "性别参数不正确")
    private Integer gender;

    @Pattern(regexp = "^$|^[0-9]{7,20}$", message = "手机号格式不正确")
    private String phone;
}
