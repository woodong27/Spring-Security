package com.example.jwt;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {"spring.datasource.url=jdbc:mysql://localhost:3306/security?useSSL=false"})
class JwtApplicationTests {

	@Test
	void contextLoads() {
	}

}
