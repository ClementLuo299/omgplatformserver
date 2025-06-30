package omgplatform.server.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Base WebSocket message structure for client-server communication.
 *
 * @authors Clement Luo,
 * @date June 29, 2025
 * @since 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WebSocketMessage {
    
    /**
     * Type of message (e.g., "JOIN", "MOVE", "CHAT", "GAME_STATE")
     */
    private String type;
    
    /**
     * Sender's username (optional, set by server)
     */
    private String sender;
    
    /**
     * Message payload (can be any JSON object)
     */
    private Object payload;
    
    /**
     * Timestamp when message was created
     */
    private Long timestamp;
    
    /**
     * Error message (if applicable)
     */
    private String error;
    
    /**
     * Create a new WebSocket message with current timestamp
     */
    public static WebSocketMessage of(String type, Object payload) {
        return new WebSocketMessage(type, null, payload, System.currentTimeMillis(), null);
    }
    
    /**
     * Create an error message
     */
    public static WebSocketMessage error(String error) {
        return new WebSocketMessage("ERROR", null, null, System.currentTimeMillis(), error);
    }
    
    /**
     * Create a system message
     */
    public static WebSocketMessage system(String message) {
        return new WebSocketMessage("SYSTEM", "System", message, System.currentTimeMillis(), null);
    }
} 