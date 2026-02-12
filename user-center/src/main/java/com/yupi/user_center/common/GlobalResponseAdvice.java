package com.yupi.user_center.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * 统一成功响应包装器（将 Controller 返回值包装为 ApiResponse）。
 *
 * @author Ethan
 */
@RestControllerAdvice(basePackages = "com.yupi.user_center.controller")
public class GlobalResponseAdvice implements ResponseBodyAdvice<Object> {

    @Resource
    private ObjectMapper objectMapper;

    /**
     * 是否启用响应包装。
     *
     * @param returnType 返回类型信息
     * @param converterType 消息转换器类型
     * @return 是否启用
     */
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    /**
     * 在写出响应体前统一包装为 ApiResponse。
     *
     * @param body 原始响应体
     * @param returnType 返回类型信息
     * @param selectedContentType 选中的媒体类型
     * @param selectedConverterType 选中的消息转换器类型
     * @param request 请求对象
     * @param response 响应对象
     * @return 包装后的响应体
     */
    @Override
    public Object beforeBodyWrite(
            Object body,
            MethodParameter returnType,
            MediaType selectedContentType,
            Class<? extends HttpMessageConverter<?>> selectedConverterType,
            ServerHttpRequest request,
            ServerHttpResponse response
    ) {
        if (body instanceof ApiResponse) {
            return body;
        }

        ApiResponse<Object> wrapped = ApiResponse.success(body);
        if (body instanceof String) {
            try {
                return objectMapper.writeValueAsString(wrapped);
            } catch (Exception e) {
                return "{\"code\":50000,\"message\":\"系统内部错误\",\"data\":null,\"timestamp\":" + System.currentTimeMillis() + "}";
            }
        }
        return wrapped;
    }
}
