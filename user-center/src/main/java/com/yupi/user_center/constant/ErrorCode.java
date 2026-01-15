package com.yupi.user_center.constant;

import org.springframework.http.HttpStatus;

/**
 * 函数级注释：统一错误码枚举。
 * 小白理解：把“错误类型”统一成一套编号，前后端看到同一个 code 就知道是什么问题。
 */
public enum ErrorCode {

    SUCCESS(0, "OK", HttpStatus.OK),

    PARAMS_ERROR(40000, "请求参数错误", HttpStatus.BAD_REQUEST),
    NOT_LOGIN(40100, "未登录", HttpStatus.UNAUTHORIZED),
    NO_AUTH(40300, "无权限", HttpStatus.FORBIDDEN),
    NOT_FOUND(40400, "资源不存在", HttpStatus.NOT_FOUND),

    OPERATION_ERROR(50001, "操作失败", HttpStatus.BAD_REQUEST),
    SYSTEM_ERROR(50000, "系统内部错误", HttpStatus.INTERNAL_SERVER_ERROR);

    private final int code;
    private final String message;
    private final HttpStatus httpStatus;

    ErrorCode(int code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

    /**
     * 函数级注释：获取错误码数字。
     * 小白理解：比如 40100 表示“未登录”，前端可以用它做跳转或弹提示。
     */
    public int getCode() {
        return code;
    }

    /**
     * 函数级注释：获取默认提示语。
     * 小白理解：如果业务没有额外传 message，就用这句话。
     */
    public String getMessage() {
        return message;
    }

    /**
     * 函数级注释：获取推荐的 HTTP 状态码。
     * 小白理解：比如未登录对应 401，这样浏览器/前端也更容易判断。
     */
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}

