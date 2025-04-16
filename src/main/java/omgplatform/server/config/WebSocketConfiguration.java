package omgplatform.server.config;

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
 */
@Configuration
@EnableWebSocket
public class WebSocketConfiguration implements WebSocketConfigurer {

    /**
     * Register a web socket handler
     */
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        //URL: /game
        registry.addHandler(new WebSocketHandler(), "/game").setAllowedOrigins("*");
    }
}
