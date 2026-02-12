package com.yupi.user_center.exception;

import com.yupi.user_center.constant.ErrorCode;

/**
 * 自定义业务异常（携带错误码）。
 *
 * @author Ethan
 */
public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;

    /**
     * 构造业务异常。
     *
     * @param errorCode 错误码枚举
     */
    public BusinessException(ErrorCode errorCode) {
        super(errorCode == null ? null : errorCode.getMessage());
        this.errorCode = errorCode;
    }

    /**
     * 构造业务异常（可指定提示信息）。
     *
     * @param errorCode 错误码枚举
     * @param message 提示信息
     */
    public BusinessException(ErrorCode errorCode, String message) {
        super(message == null || message.isBlank()
                ? (errorCode == null ? null : errorCode.getMessage())
                : message);
        this.errorCode = errorCode;
    }

    /**
     * 获取错误码枚举。
     *
     * @return 错误码枚举
     */
    public ErrorCode getErrorCode() {
        return errorCode;
    }

    /**
     * 获取错误码数字。
     *
     * @return 错误码数字
     */
    public int getCode() {
        return errorCode == null ? ErrorCode.SYSTEM_ERROR.getCode() : errorCode.getCode();
    }
}
