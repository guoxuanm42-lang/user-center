package com.yupi.user_center.config;

import com.yupi.user_center.interceptor.AdminAuthInterceptor;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 函数级注释：WebMVC 配置（注册拦截器等）。
 * 小白理解：这是“把保安安排到门口”的地方，告诉系统哪些路径要先被拦截检查。
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Resource
    private AdminAuthInterceptor adminAuthInterceptor;

    /**
     * 函数级注释：注册拦截器，并指定拦截路径。
     * 小白理解：只要访问 /admin/**，都会先经过 AdminAuthInterceptor 检查管理员权限。
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(adminAuthInterceptor)
                .addPathPatterns("/admin/**");
    }
}

