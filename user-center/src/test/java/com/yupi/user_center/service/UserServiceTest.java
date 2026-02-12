package com.yupi.user_center.service;


import com.yupi.user_center.exception.BusinessException;
import com.yupi.user_center.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


/**
 * 用户服务测试类。
 *
 * @author Ethan
 */
@SpringBootTest
public class UserServiceTest {

   @Autowired
    private UserService userService;

    /**
     * 测试新增用户（save）
     */
    @Test
    public void testAddUser() {

        // 1. 创建用户对象
        User user = new User();
        user.setName("testUser");
        user.setUserAccount("test_" + System.currentTimeMillis());
        user.setEmail("test@example.com");
        user.setUserPassword("123456");

        // （可选）更多字段：你如果 model 里有就加，没有就忽略
        // user.setAvatarUrl("https://example.com/avatar.png");
        // user.setGender(1);
        // user.setUserPassword("123456");
        // user.setPhone("13800000000");

        // 2. 调用 Service 保存
        boolean result = userService.save(user);

        // 3. 断言保存成功
        Assertions.assertTrue(result);

        // 4. 打印数据库生成的 ID（主键自动回填）
        System.out.println("Inserted user ID = " + user.getId());

        // 5. 再查一遍确认是否成功写入
        User dbUser = userService.getById(user.getId());
        Assertions.assertNotNull(dbUser);
        Assertions.assertEquals("testUser", dbUser.getName());
    }

    @Test
    /**
     * 测试用户注册逻辑。
     */
    void userRegister() {
    // 1. 密码为空
    Assertions.assertThrows(BusinessException.class, () -> userService.userRegister("yupi", "", "123456"));

    // 2. 两次密码不一致
    Assertions.assertThrows(BusinessException.class, () -> userService.userRegister("yupi", "12345678", "123456789"));

    // 3. 正常情况，注册成功
    String userAccount = "yupi_" + System.currentTimeMillis();
    long result = userService.userRegister(userAccount, "12345678", "12345678");
    Assertions.assertTrue(result > 0); // 成功返回用户id

    // 4. 账号重复（业务规则）
    Assertions.assertThrows(BusinessException.class, () -> userService.userRegister(userAccount, "12345678", "12345678"));
}

}
