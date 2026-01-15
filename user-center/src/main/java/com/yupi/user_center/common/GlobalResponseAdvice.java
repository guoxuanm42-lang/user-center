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
 * 函数级注释：统一成功响应包装器。
 * 小白理解：Controller 正常 return 的对象/布尔值/列表，会在这里自动包一层 ApiResponse.success(...)。
 */
@RestControllerAdvice(basePackages = "com.yupi.user_center.controller")
public class GlobalResponseAdvice implements ResponseBodyAdvice<Object> {

    @Resource
    private ObjectMapper objectMapper;

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    /**
     * 函数级注释：在返回给前端之前，把数据统一包装成 ApiResponse。
     * 小白理解：这样前端就永远收到 {code,message,data} 结构，写代码更简单。
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
