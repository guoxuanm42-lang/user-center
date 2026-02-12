package com.yupi.user_center.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 用户接口测试类。
 *
 * @author Ethan
 */
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTests {

    @Autowired
    private MockMvc mockMvc;

    /**
     * 接口测试：GET /user/session/ttl 返回 200
     * 作用：验证服务端路由可用，并且 Session 能正常创建与返回超时时间。
     * 小白理解：随便访问一个不需要登录的接口，看后端能不能正常回结果。
     */
    @Test
    void sessionTtl_shouldReturnOk() throws Exception {
        mockMvc.perform(get("/user/session/ttl"))
                .andExpect(status().isOk());
    }

    /**
     * 接口测试：GET /user/current 未登录返回 401
     * 作用：验证“未登录不能获取当前用户”的逻辑生效。
     * 小白理解：你没登录就去问“我是谁”，后端应该拒绝你。
     */
    @Test
    void currentUser_shouldReturnUnauthorized_whenNotLogin() throws Exception {
        mockMvc.perform(get("/user/current"))
                .andExpect(status().isUnauthorized());
    }
}
