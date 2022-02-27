package com.summer.simplespringioc;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 测试用例
 */
@SpringBootTest
class SimplespringiocApplicationTests {

	@Test
	void contextLoads() {
		SimpleBeanIocContainer simpleBeanIocContainer = new SimpleBeanIocContainer();
		simpleBeanIocContainer.init("ioctest.xml");
	}

}
