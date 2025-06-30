package omgplatform.server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Game-specific message payloads for WebSocket communication.
 *
 * @authors Clement Luo,
 * @date June 29, 2025
 * @since 1.0
 */
public class GameMessage {
    
    /**
     * Join game request/response
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class JoinGame {
        private String username;
        private String token; // JWT token for authentication
    }
    
    /**
     * Chat message
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatMessage {
        private String message;
        private String room; // Optional room identifier
    }
    
    /**
     * Player movement
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PlayerMove {
        private int x;
        private int y;
        private String direction; // UP, DOWN, LEFT, RIGHT
    }
    
    /**
     * Game state update
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GameState {
        private String gameId;
        private String status; // WAITING, PLAYING, FINISHED
        private java.util.List<PlayerInfo> players;
        private Object gameData; // Game-specific data
    }
    
    /**
     * Player information
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PlayerInfo {
        private String username;
        private String status; // ONLINE, OFFLINE, PLAYING
        private int x;
        private int y;
        private Long lastSeen;
    }
    
    /**
     * Room information
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RoomInfo {
        private String roomId;
        private String name;
        private int maxPlayers;
        private int currentPlayers;
        private String status; // OPEN, FULL, CLOSED
    }
} 