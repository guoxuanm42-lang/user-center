package com.yupi.user_center;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.mybatis.spring.annotation.MapperScan;

/**
 * 用户中心后端应用启动类。
 *
 * @author Ethan
 */
@SpringBootApplication
@MapperScan("com.yupi.user_center.mapper")
public class UserCenterApplication {

    /**
     * 应用启动入口。
     *
     * @param args 启动参数
     */
    public static void main(String[] args) {
        SpringApplication.run(UserCenterApplication.class, args);
    }

}
