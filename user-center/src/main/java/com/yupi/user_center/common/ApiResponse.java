package com.yupi.user_center.common;

import com.yupi.user_center.constant.ErrorCode;

/**
 * 函数级注释：统一返回体。
 * 小白理解：不管成功还是失败，后端都返回同一种结构：{code, message, data, timestamp}。
 */
public class ApiResponse<T> {

    private int code;
    private String message;
    private T data;
    private long timestamp;

    public ApiResponse() {
    }

    public ApiResponse(int code, String message, T data, long timestamp) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = timestamp;
    }

    /**
     * 函数级注释：构造成功返回。
     * 小白理解：业务正常完成，把数据塞到 data 里返回即可。
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(ErrorCode.SUCCESS.getCode(), ErrorCode.SUCCESS.getMessage(), data, System.currentTimeMillis());
    }

    /**
     * 函数级注释：构造失败返回（用错误码默认提示）。
     * 小白理解：发生错误时，统一用错误码来生成失败响应。
     */
    public static <T> ApiResponse<T> error(ErrorCode errorCode) {
        return new ApiResponse<>(errorCode.getCode(), errorCode.getMessage(), null, System.currentTimeMillis());
    }

    /**
     * 函数级注释：构造失败返回（可自定义提示）。
     * 小白理解：同一个错误码可以配上更具体的提示，比如“账号至少 4 位”。
     */
    public static <T> ApiResponse<T> error(ErrorCode errorCode, String message) {
        String finalMessage = (message == null || message.isBlank()) ? errorCode.getMessage() : message;
        return new ApiResponse<>(errorCode.getCode(), finalMessage, null, System.currentTimeMillis());
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
