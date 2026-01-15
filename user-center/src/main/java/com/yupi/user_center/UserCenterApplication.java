package com.yupi.user_center;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan("com.yupi.user_center.mapper")
public class UserCenterApplication {

    /**
     * 应用启动入口函数
     * 作用：启动 Spring Boot 应用，把内置 Web 服务运行起来。
     * 小白理解：就像双击打开一个程序，这一行代码就是“开机键”。
     */
    public static void main(String[] args) {
        SpringApplication.run(UserCenterApplication.class, args);
    }

}
