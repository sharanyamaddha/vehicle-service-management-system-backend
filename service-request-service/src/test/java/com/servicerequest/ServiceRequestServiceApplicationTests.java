package com.servicerequest;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
		"razorpay.key-id=test_key",
		"razorpay.key-secret=test_secret"
})
class ServiceRequestServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
