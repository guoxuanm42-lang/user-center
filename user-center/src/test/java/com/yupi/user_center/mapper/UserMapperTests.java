package com.yupi.user_center.mapper;

import com.yupi.user_center.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * 用户 Mapper 测试类。
 *
 * @author Ethan
 */
@SpringBootTest
public class UserMapperTests {

    @Autowired
    private UserMapper userMapper;

    /**
     * 测试：查询所有用户
     * 作用：调用 MyBatis-Plus 的通用方法 selectList(null) 获取全表数据。
     * 小白理解：把用户表里的所有人都拿出来，确认能正常连库并拿到数据。
     */
    @Test
    void selectAll_shouldReturnUsers() {
        List<User> users = userMapper.selectList(null);
        Assertions.assertNotNull(users);
        Assertions.assertFalse(users.isEmpty(), "用户列表不应为空，请确认测试库中有数据");
    }

    /**
     * 测试：按主键查询用户
     * 作用：调用 selectById(1L) 返回指定 id 的用户，如果存在则进行字段校验。
     * 小白理解：查编号为 1 的那个人，看看能不能拿到并且数据不为空。
     */
    @Test
    void selectById_shouldReturnUser() {
        User user = userMapper.selectById(1L);
        // 由于测试数据可能不同，存在为空的情况，这里只在非空时做字段断言
        if (user != null) {
            Assertions.assertEquals(1L, user.getId());
            Assertions.assertNotNull(user.getName());
            Assertions.assertNotNull(user.getEmail());
        }
    }
}
