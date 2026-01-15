package com.yupi.user_center;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import java.security.NoSuchAlgorithmException;
import org.springframework.util.DigestUtils;


@SpringBootTest
class UserCenterApplicationTests {

	@Test
	void testDigest() throws NoSuchAlgorithmException { 
		String newPassword=DigestUtils.md5DigestAsHex(("123456"+"mypassword").getBytes());
		System.out.println(newPassword);
	}
	@Test
	void contextLoads() {
	}

}
