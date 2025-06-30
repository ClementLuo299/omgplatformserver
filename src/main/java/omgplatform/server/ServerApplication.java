package omgplatform.server;

import omgplatform.server.utils.LoggingUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Entry point for the OMG Platform Server Spring Boot application.
 *
 * @authors Clement Luo,
 * @date April 15, 2025
 * @edited June 29, 2025
 * @since 1.0
 */
@SpringBootApplication
public class ServerApplication {
	public static void main(String[] args) {
		LoggingUtil.info("Starting OMG Platform Server application");
		LoggingUtil.info("Application arguments: " + String.join(" ", args));
		
		try {
			ConfigurableApplicationContext context = SpringApplication.run(ServerApplication.class, args);
			LoggingUtil.info("OMG Platform Server application started successfully");
			LoggingUtil.info("Application context initialized with " + context.getBeanDefinitionNames().length + " beans");
			
			// Add shutdown hook for graceful logging
			Runtime.getRuntime().addShutdownHook(new Thread(() -> {
				LoggingUtil.info("Shutting down OMG Platform Server application");
				context.close();
				LoggingUtil.info("OMG Platform Server application shutdown complete");
			}));
			
		} catch (Exception e) {
			LoggingUtil.error("Failed to start OMG Platform Server application", e);
			System.exit(1);
		}
	}
}
