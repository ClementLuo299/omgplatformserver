package omgplatform.server.config;

import omgplatform.server.controllers.WebSocketHandler;
import omgplatform.server.utils.LoggingUtil;
import org.springframework.beans.factory.annotation.Autowired;
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
public class WebSocketConfiguration implements WebSocketConfigurer {

    private final WebSocketHandler webSocketHandler;

    @Autowired
    public WebSocketConfiguration(WebSocketHandler webSocketHandler) {
        this.webSocketHandler = webSocketHandler;
        LoggingUtil.info("Initializing WebSocket configuration");
    }

    /**
     * Register a web socket handler
     */
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        LoggingUtil.info("Registering WebSocket handlers");
        
        try {
            //URL: /game
            registry.addHandler(webSocketHandler, "/game").setAllowedOrigins("*");
            LoggingUtil.info("WebSocket handler registered successfully for endpoint: /game");
            LoggingUtil.info("WebSocket configuration: All origins allowed for /game endpoint");
        } catch (Exception e) {
            LoggingUtil.error("Failed to register WebSocket handlers", e);
            throw e;
        }
    }
}
