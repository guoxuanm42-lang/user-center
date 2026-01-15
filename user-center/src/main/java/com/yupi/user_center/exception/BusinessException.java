package com.yupi.user_center.exception;

import com.yupi.user_center.constant.ErrorCode;

/**
 * 函数级注释：自定义业务异常。
 * 小白理解：当你发现用户输入不对、权限不够、未登录等“业务问题”，就抛这个异常；
 * 全局异常处理器会把它转成统一 JSON 返回给前端。
 */
public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode == null ? null : errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public BusinessException(ErrorCode errorCode, String message) {
        super(message == null || message.isBlank()
                ? (errorCode == null ? null : errorCode.getMessage())
                : message);
        this.errorCode = errorCode;
    }

    /**
     * 函数级注释：获取错误码枚举。
     * 小白理解：异常处理器靠它决定返回的 code 和 HTTP 状态码。
     */
    public ErrorCode getErrorCode() {
        return errorCode;
    }

    /**
     * 函数级注释：获取错误码数字。
     * 小白理解：前端通常就是看这个 code 来展示提示或执行跳转。
     */
    public int getCode() {
        return errorCode == null ? ErrorCode.SYSTEM_ERROR.getCode() : errorCode.getCode();
    }
}
