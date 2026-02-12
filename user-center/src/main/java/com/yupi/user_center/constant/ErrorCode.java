package com.yupi.user_center.constant;

import org.springframework.http.HttpStatus;

/**
 * 统一错误码枚举。
 *
 * @author Ethan
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
     * 获取错误码数字。
     *
     * @return 错误码数字
     */
    public int getCode() {
        return code;
    }

    /**
     * 获取默认提示语。
     *
     * @return 默认提示语
     */
    public String getMessage() {
        return message;
    }

    /**
     * 获取推荐的 HTTP 状态码。
     *
     * @return HTTP 状态码
     */
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}

