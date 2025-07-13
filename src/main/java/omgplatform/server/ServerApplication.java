package omgplatform.server;

import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class ServerApplication {
	public static void main(String[] args) {
		log.info("Starting OMG Platform Server application");
		log.info("Application arguments: {}", String.join(" ", args));
		
		try {
			ConfigurableApplicationContext context = SpringApplication.run(ServerApplication.class, args);
			log.info("OMG Platform Server application started successfully");
			log.info("Application context initialized with {} beans", context.getBeanDefinitionNames().length);
			
			// Add shutdown hook for graceful logging
			Runtime.getRuntime().addShutdownHook(new Thread(() -> {
				log.info("Shutting down OMG Platform Server application");
				context.close();
				log.info("OMG Platform Server application shutdown complete");
			}));
			
		} catch (Exception e) {
			log.error("Failed to start OMG Platform Server application", e);
			System.exit(1);
		}
	}
}
