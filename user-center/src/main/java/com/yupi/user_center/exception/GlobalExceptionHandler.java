package com.yupi.user_center.exception;

import com.yupi.user_center.common.ApiResponse;
import com.yupi.user_center.constant.ErrorCode;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

/**
 * 全局异常处理器（将异常统一转换为 ApiResponse）。
 *
 * @author Ethan
 */
@RestControllerAdvice(basePackages = "com.yupi.user_center.controller")
public class GlobalExceptionHandler {

    /**
     * 处理自定义业务异常 BusinessException。
     *
     * @param e 业务异常
     * @return 统一返回结构
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Object>> handleBusinessException(BusinessException e) {
        ErrorCode errorCode = e.getErrorCode() == null ? ErrorCode.SYSTEM_ERROR : e.getErrorCode();
        HttpStatus status = errorCode.getHttpStatus() == null ? HttpStatus.INTERNAL_SERVER_ERROR : errorCode.getHttpStatus();
        return ResponseEntity.status(status).body(ApiResponse.error(errorCode, e.getMessage()));
    }

    /**
     * 处理参数异常 IllegalArgumentException。
     *
     * @param e 参数异常
     * @return 统一返回结构
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Object>> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(ErrorCode.PARAMS_ERROR, e.getMessage()));
    }

    /**
     * 处理请求体参数校验异常 MethodArgumentNotValidException。
     *
     * @param e 参数校验异常
     * @return 统一返回结构
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String message = ErrorCode.PARAMS_ERROR.getMessage();
        if (e.getBindingResult() != null) {
            FieldError fieldError = e.getBindingResult().getFieldError();
            if (fieldError != null && fieldError.getDefaultMessage() != null && !fieldError.getDefaultMessage().isBlank()) {
                message = fieldError.getDefaultMessage();
            }
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(ErrorCode.PARAMS_ERROR, message));
    }

    /**
     * 处理约束校验异常 ConstraintViolationException（通常来自 query 参数校验）。
     *
     * @param e 约束校验异常
     * @return 统一返回结构
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleConstraintViolationException(ConstraintViolationException e) {
        String message = ErrorCode.PARAMS_ERROR.getMessage();
        if (e.getConstraintViolations() != null && !e.getConstraintViolations().isEmpty()) {
            String first = e.getConstraintViolations().iterator().next().getMessage();
            if (first != null && !first.isBlank()) {
                message = first;
            }
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(ErrorCode.PARAMS_ERROR, message));
    }

    /**
     * 处理请求体解析异常 HttpMessageNotReadableException。
     *
     * @param e 请求体解析异常
     * @return 统一返回结构
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Object>> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(ErrorCode.PARAMS_ERROR, "请求体解析失败"));
    }

    /**
     * 处理 ResponseStatusException（带 HTTP 状态码的异常）。
     *
     * @param e 带 HTTP 状态码的异常
     * @return 统一返回结构
     */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiResponse<Object>> handleResponseStatusException(ResponseStatusException e) {
        HttpStatus status = HttpStatus.resolve(e.getStatusCode().value());
        if (status == null) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        ErrorCode errorCode;
        if (status == HttpStatus.UNAUTHORIZED) {
            errorCode = ErrorCode.NOT_LOGIN;
        } else if (status == HttpStatus.FORBIDDEN) {
            errorCode = ErrorCode.NO_AUTH;
        } else if (status == HttpStatus.NOT_FOUND) {
            errorCode = ErrorCode.NOT_FOUND;
        } else if (status == HttpStatus.BAD_REQUEST) {
            errorCode = ErrorCode.PARAMS_ERROR;
        } else {
            errorCode = status.is5xxServerError() ? ErrorCode.SYSTEM_ERROR : ErrorCode.OPERATION_ERROR;
        }

        String message = e.getReason();
        if (message == null || message.isBlank()) {
            message = errorCode.getMessage();
        }
        return ResponseEntity.status(status).body(ApiResponse.error(errorCode, message));
    }

    /**
     * 兜底处理所有未知异常。
     *
     * @param e 未知异常
     * @return 统一返回结构
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(ErrorCode.SYSTEM_ERROR));
    }
}
