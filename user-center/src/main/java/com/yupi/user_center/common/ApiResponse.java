package com.yupi.user_center.common;

import com.yupi.user_center.constant.ErrorCode;

/**
 * 统一返回体。
 *
 * @author Ethan
 */
public class ApiResponse<T> {

    private int code;
    private String message;
    private T data;
    private long timestamp;

    /**
     * 无参构造方法。
     */
    public ApiResponse() {
    }

    /**
     * 全参构造方法。
     *
     * @param code 业务码
     * @param message 提示信息
     * @param data 返回数据
     * @param timestamp 时间戳（毫秒）
     */
    public ApiResponse(int code, String message, T data, long timestamp) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = timestamp;
    }

    /**
     * 构造成功返回。
     *
     * @param data 返回数据
     * @return 统一返回结构
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(ErrorCode.SUCCESS.getCode(), ErrorCode.SUCCESS.getMessage(), data, System.currentTimeMillis());
    }

    /**
     * 构造失败返回（使用错误码默认提示）。
     *
     * @param errorCode 错误码枚举
     * @return 统一返回结构
     */
    public static <T> ApiResponse<T> error(ErrorCode errorCode) {
        return new ApiResponse<>(errorCode.getCode(), errorCode.getMessage(), null, System.currentTimeMillis());
    }

    /**
     * 构造失败返回（可自定义提示）。
     *
     * @param errorCode 错误码枚举
     * @param message 自定义提示信息（为空则使用错误码默认提示）
     * @return 统一返回结构
     */
    public static <T> ApiResponse<T> error(ErrorCode errorCode, String message) {
        String finalMessage = (message == null || message.isBlank()) ? errorCode.getMessage() : message;
        return new ApiResponse<>(errorCode.getCode(), finalMessage, null, System.currentTimeMillis());
    }

    /**
     * 获取业务码。
     *
     * @return 业务码
     */
    public int getCode() {
        return code;
    }

    /**
     * 设置业务码。
     *
     * @param code 业务码
     */
    public void setCode(int code) {
        this.code = code;
    }

    /**
     * 获取提示信息。
     *
     * @return 提示信息
     */
    public String getMessage() {
        return message;
    }

    /**
     * 设置提示信息。
     *
     * @param message 提示信息
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * 获取返回数据。
     *
     * @return 返回数据
     */
    public T getData() {
        return data;
    }

    /**
     * 设置返回数据。
     *
     * @param data 返回数据
     */
    public void setData(T data) {
        this.data = data;
    }

    /**
     * 获取时间戳（毫秒）。
     *
     * @return 时间戳（毫秒）
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * 设置时间戳（毫秒）。
     *
     * @param timestamp 时间戳（毫秒）
     */
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
