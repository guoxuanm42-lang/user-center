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
 * 函数级注释：全局异常处理器。
 * 小白理解：Controller/Service 里只负责抛异常；这里负责把异常统一翻译成 ApiResponse。
 */
@RestControllerAdvice(basePackages = "com.yupi.user_center.controller")
public class GlobalExceptionHandler {

    /**
     * 函数级注释：处理自定义业务异常 BusinessException。
     * 小白理解：你主动抛的业务异常，会走这里，返回你设定的 code/message。
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Object>> handleBusinessException(BusinessException e) {
        ErrorCode errorCode = e.getErrorCode() == null ? ErrorCode.SYSTEM_ERROR : e.getErrorCode();
        HttpStatus status = errorCode.getHttpStatus() == null ? HttpStatus.INTERNAL_SERVER_ERROR : errorCode.getHttpStatus();
        return ResponseEntity.status(status).body(ApiResponse.error(errorCode, e.getMessage()));
    }

    /**
     * 函数级注释：处理参数异常 IllegalArgumentException。
     * 小白理解：你代码里 throw new IllegalArgumentException(...)，最终也会统一返回 JSON 错误结构。
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Object>> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(ErrorCode.PARAMS_ERROR, e.getMessage()));
    }

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

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Object>> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(ErrorCode.PARAMS_ERROR, "请求体解析失败"));
    }

    /**
     * 函数级注释：处理 ResponseStatusException（带 HTTP 状态码的异常）。
     * 小白理解：比如你抛 401/403，这里会保持状态码，并映射到统一的错误码。
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
     * 函数级注释：兜底处理所有未知异常。
     * 小白理解：没想到的异常也不会“直接爆栈给前端”，而是统一返回系统错误。
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(ErrorCode.SYSTEM_ERROR));
    }
}
