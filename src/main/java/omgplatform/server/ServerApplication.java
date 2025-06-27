package omgplatform.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the OMG Platform Server Spring Boot application.
 *
 * @authors Clement Luo,
 * @date April 15, 2025
 * @edited April 16, 2025
 * @since 1.0
 */
@SpringBootApplication
public class ServerApplication {
	public static void main(String[] args) {
		SpringApplication.run(ServerApplication.class, args);
	}
}
