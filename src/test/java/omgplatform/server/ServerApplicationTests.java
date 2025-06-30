package omgplatform.server;

import omgplatform.server.utils.LoggingUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

/**
 * Test class for the OMG Platform Server Spring Boot application.
 *
 * @authors Clement Luo,
 * @date April 15, 2025
 * @edited June 29, 2025
 * @since 1.0
 */
@SpringBootTest
class ServerApplicationTests {

	@Test
	void contextLoads() {
		LoggingUtil.info("Starting Spring Boot context load test");
		
		try {
			LoggingUtil.debug("Testing if Spring application context loads successfully");
			// The @SpringBootTest annotation will automatically test context loading
			LoggingUtil.info("Spring Boot context loaded successfully");
		} catch (Exception e) {
			LoggingUtil.error("Failed to load Spring Boot context", e);
			throw e;
		}
	}

	@Test
	void testLoggingUtility() {
		LoggingUtil.info("Testing logging utility functionality");
		
		LoggingUtil.debug("This is a debug message");
		LoggingUtil.info("This is an info message");
		LoggingUtil.warn("This is a warning message");
		
		// Test logging with context
		LoggingUtil.info("Test with context", Map.of(
			"testName", "testLoggingUtility",
			"timestamp", System.currentTimeMillis(),
			"status", "running"
		));
		
		LoggingUtil.info("Logging utility test completed successfully");
	}

}
