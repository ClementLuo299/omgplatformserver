package omgplatform.server.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import omgplatform.server.controllers.WebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * Configuration for WebSockets.
 *
 * @authors Clement Luo,
 * @date April 15, 2025
 * @edited June 29, 2025
 * @since 1.0
 */
@Configuration
@EnableWebSocket
@RequiredArgsConstructor
@Slf4j
public class WebSocketConfiguration implements WebSocketConfigurer {

    private final WebSocketHandler webSocketHandler;

    /**
     * Register a web socket handler
     */
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        log.info("Registering WebSocket handlers");
        
        try {
            //URL: /game
            registry.addHandler(webSocketHandler, "/game").setAllowedOrigins("*");
            log.info("WebSocket handler registered successfully for endpoint: /game");
            log.info("WebSocket configuration: All origins allowed for /game endpoint");
        } catch (Exception e) {
            log.error("Failed to register WebSocket handlers", e);
            throw e;
        }
    }
}
