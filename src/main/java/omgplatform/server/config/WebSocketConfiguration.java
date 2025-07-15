package omgplatform.server.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import omgplatform.server.controllers.WebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * WebSocket configuration for the OMG Platform server.
 * 
 * This class configures:
 * - WebSocket endpoint registration
 * - Cross-origin access for WebSocket connections
 * - Real-time communication capabilities
 * 
 * The WebSocket endpoint enables:
 * - Real-time bidirectional communication between client and server
 * - Live updates without HTTP polling
 * - Real-time messaging and notifications
 * 
 * @authors Clement Luo,
 * @date April 15, 2025
 * @edited July 14, 2025
 * @since 1.0
 */
@Configuration
@EnableWebSocket
@RequiredArgsConstructor
@Slf4j
public class WebSocketConfiguration implements WebSocketConfigurer {

    private final WebSocketHandler webSocketHandler;

    /**
     * Registers WebSocket handlers and configures endpoint mappings.
     * 
     * This method sets up:
     * - WebSocket endpoint at /websocket for real-time communication
     * - Cross-origin access to allow connections from any origin
     * - Handler registration for processing WebSocket messages
     * 
     * The /websocket endpoint is used for:
     * - Real-time messaging
     * - Live notifications
     * - Real-time updates
     * - Bidirectional communication
     * 
     * Note: In production, consider restricting allowed origins for security
     *
     * @param registry WebSocketHandlerRegistry for registering handlers
     */
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        log.info("Registering WebSocket handlers");
        
        try {
            // Register WebSocket handler for the websocket endpoint
            // This enables real-time bidirectional communication
            registry.addHandler(webSocketHandler, "/websocket")
                // Allow connections from any origin (development setting)
                // In production, specify exact origins for security
                .setAllowedOrigins("*");
            
            log.info("WebSocket handler registered successfully for endpoint: /websocket");
            log.info("WebSocket configuration: All origins allowed for /websocket endpoint");
        } catch (Exception e) {
            log.error("Failed to register WebSocket handlers", e);
            throw e;
        }
    }
}
