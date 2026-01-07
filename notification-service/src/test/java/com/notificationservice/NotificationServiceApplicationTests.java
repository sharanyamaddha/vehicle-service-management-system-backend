package com.notificationservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@org.springframework.test.context.TestPropertySource(properties = {
		"MAIL_USERNAME=test",
		"MAIL_PASSWORD=test"
})
class NotificationServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
