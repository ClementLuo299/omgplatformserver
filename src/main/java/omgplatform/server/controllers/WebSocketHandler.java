package omgplatform.server.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import omgplatform.server.dto.GameMessage;
import omgplatform.server.dto.WebSocketMessage;
import omgplatform.server.entities.User;
import omgplatform.server.services.GameService;
import omgplatform.server.services.UserService;
import omgplatform.server.utils.JWTUtil;
import omgplatform.server.utils.LoggingUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Enhanced WebSocket server logic for game functionality.
 *
 * @authors Clement Luo,
 * @date April 15, 2025
 * @edited June 29, 2025
 * @since 1.0
 */
@Component
public class WebSocketHandler extends TextWebSocketHandler {

    // List of authenticated user sessions
    private final Map<WebSocketSession, User> authenticatedSessions = new ConcurrentHashMap<>();
    
    // Object mapper for JSON serialization
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    // Services
    private final GameService gameService;
    private final UserService userService;
    private final JWTUtil jwtUtil;
    
    @Autowired
    public WebSocketHandler(GameService gameService, UserService userService, JWTUtil jwtUtil) {
        this.gameService = gameService;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        LoggingUtil.info("WebSocket handler initialized with dependencies");
    }

    /**
     * Run after connection is established
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws IOException {
        String sessionId = session.getId();
        LoggingUtil.websocketEvent("CONNECT", sessionId, "New WebSocket connection established");
        LoggingUtil.info("WebSocket connection established", Map.of(
            "sessionId", sessionId,
            "remoteAddress", session.getRemoteAddress() != null ? session.getRemoteAddress().toString() : "unknown",
            "activeConnections", authenticatedSessions.size() + 1
        ));
        
        // Send welcome message
        WebSocketMessage welcomeMsg = WebSocketMessage.system("Welcome! Please authenticate to join the game.");
        sendMessage(session, welcomeMsg);
    }

    /**
     * Handle text messages
     */
    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String sessionId = session.getId();
        String payload = message.getPayload();
        
        LoggingUtil.websocketEvent("MESSAGE", sessionId, "Received message: " + payload);
        LoggingUtil.debug("Processing WebSocket message", Map.of(
            "sessionId", sessionId,
            "messageLength", payload.length(),
            "messageContent", payload
        ));

        try {
            // Parse the incoming message
            WebSocketMessage wsMessage = objectMapper.readValue(payload, WebSocketMessage.class);
            
            // Handle different message types
            switch (wsMessage.getType()) {
                case "JOIN":
                    handleJoinGame(session, wsMessage);
                    break;
                case "CHAT":
                    handleChatMessage(session, wsMessage);
                    break;
                case "MOVE":
                    handlePlayerMove(session, wsMessage);
                    break;
                case "GET_STATE":
                    handleGetGameState(session);
                    break;
                case "GET_PLAYERS":
                    handleGetPlayers(session);
                    break;
                default:
                    sendError(session, "Unknown message type: " + wsMessage.getType());
            }
            
        } catch (Exception e) {
            LoggingUtil.error("Error processing WebSocket message", e);
            sendError(session, "Invalid message format: " + e.getMessage());
        }
    }

    /**
     * Handle player joining the game
     */
    private void handleJoinGame(WebSocketSession session, WebSocketMessage message) {
        try {
            GameMessage.JoinGame joinData = objectMapper.convertValue(message.getPayload(), GameMessage.JoinGame.class);
            
            if (joinData.getUsername() == null || joinData.getToken() == null) {
                sendError(session, "Username and token are required");
                return;
            }
            
            // Validate JWT token
            try {
                jwtUtil.validateToken(joinData.getToken());
            } catch (Exception e) {
                sendError(session, "Invalid authentication token");
                return;
            }
            
            // Get user from database
            User user = userService.findByUsername(joinData.getUsername()).orElse(null);
            if (user == null) {
                sendError(session, "User not found");
                return;
            }
            
            // Check if user is already connected
            if (gameService.isPlayerOnline(user.getUsername())) {
                sendError(session, "User is already online");
                return;
            }
            
            // Add user to authenticated sessions
            authenticatedSessions.put(session, user);
            
            // Add player to game
            GameMessage.PlayerInfo playerInfo = gameService.addPlayer(user);
            
            // Send success response
            WebSocketMessage response = WebSocketMessage.of("JOIN_SUCCESS", Map.of(
                "player", playerInfo,
                "message", "Successfully joined the game!"
            ));
            response.setSender(user.getUsername());
            sendMessage(session, response);
            
            // Broadcast player joined message
            WebSocketMessage broadcastMsg = WebSocketMessage.system(user.getUsername() + " joined the game!");
            broadcastToAuthenticated(broadcastMsg);
            
            // Send current game state to the new player
            WebSocketMessage gameStateMsg = WebSocketMessage.of("GAME_STATE", gameService.getGameState());
            sendMessage(session, gameStateMsg);
            
            LoggingUtil.info("Player joined game successfully", Map.of(
                "username", user.getUsername(),
                "sessionId", session.getId()
            ));
            
        } catch (Exception e) {
            LoggingUtil.error("Error handling join game", e);
            sendError(session, "Failed to join game: " + e.getMessage());
        }
    }

    /**
     * Handle chat messages
     */
    private void handleChatMessage(WebSocketSession session, WebSocketMessage message) {
        User user = authenticatedSessions.get(session);
        if (user == null) {
            sendError(session, "Not authenticated");
            return;
        }
        
        try {
            GameMessage.ChatMessage chatData = objectMapper.convertValue(message.getPayload(), GameMessage.ChatMessage.class);
            
            if (chatData.getMessage() == null || chatData.getMessage().trim().isEmpty()) {
                sendError(session, "Message cannot be empty");
                return;
            }
            
            // Create chat message
            WebSocketMessage chatMsg = WebSocketMessage.of("CHAT", Map.of(
                "message", chatData.getMessage(),
                "room", chatData.getRoom() != null ? chatData.getRoom() : "main"
            ));
            chatMsg.setSender(user.getUsername());
            
            // Broadcast to all authenticated users
            broadcastToAuthenticated(chatMsg);
            
            LoggingUtil.debug("Chat message broadcasted", Map.of(
                "username", user.getUsername(),
                "message", chatData.getMessage()
            ));
            
        } catch (Exception e) {
            LoggingUtil.error("Error handling chat message", e);
            sendError(session, "Failed to send chat message");
        }
    }

    /**
     * Handle player movement
     */
    private void handlePlayerMove(WebSocketSession session, WebSocketMessage message) {
        User user = authenticatedSessions.get(session);
        if (user == null) {
            sendError(session, "Not authenticated");
            return;
        }
        
        try {
            GameMessage.PlayerMove moveData = objectMapper.convertValue(message.getPayload(), GameMessage.PlayerMove.class);
            
            // Update player position
            boolean success = gameService.updatePlayerPosition(
                user.getUsername(), 
                moveData.getX(), 
                moveData.getY(), 
                moveData.getDirection()
            );
            
            if (success) {
                // Broadcast movement to all players
                WebSocketMessage moveMsg = WebSocketMessage.of("PLAYER_MOVE", Map.of(
                    "username", user.getUsername(),
                    "x", moveData.getX(),
                    "y", moveData.getY(),
                    "direction", moveData.getDirection()
                ));
                broadcastToAuthenticated(moveMsg);
                
                LoggingUtil.debug("Player movement broadcasted", Map.of(
                    "username", user.getUsername(),
                    "x", moveData.getX(),
                    "y", moveData.getY(),
                    "direction", moveData.getDirection()
                ));
            } else {
                sendError(session, "Failed to update position");
            }
            
        } catch (Exception e) {
            LoggingUtil.error("Error handling player move", e);
            sendError(session, "Failed to process movement");
        }
    }

    /**
     * Handle get game state request
     */
    private void handleGetGameState(WebSocketSession session) {
        User user = authenticatedSessions.get(session);
        if (user == null) {
            sendError(session, "Not authenticated");
            return;
        }
        
        try {
            WebSocketMessage stateMsg = WebSocketMessage.of("GAME_STATE", gameService.getGameState());
            sendMessage(session, stateMsg);
        } catch (Exception e) {
            LoggingUtil.error("Error getting game state", e);
            sendError(session, "Failed to get game state");
        }
    }

    /**
     * Handle get players request
     */
    private void handleGetPlayers(WebSocketSession session) {
        User user = authenticatedSessions.get(session);
        if (user == null) {
            sendError(session, "Not authenticated");
            return;
        }
        
        try {
            WebSocketMessage playersMsg = WebSocketMessage.of("PLAYERS_LIST", gameService.getOnlinePlayers());
            sendMessage(session, playersMsg);
        } catch (Exception e) {
            LoggingUtil.error("Error getting players list", e);
            sendError(session, "Failed to get players list");
        }
    }

    /**
     * Run after connection is closed
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String sessionId = session.getId();
        User user = authenticatedSessions.remove(session);
        
        LoggingUtil.websocketEvent("DISCONNECT", sessionId, "Connection closed with status: " + status.getCode());
        LoggingUtil.info("WebSocket connection closed", Map.of(
            "sessionId", sessionId,
            "closeStatus", status.getCode(),
            "closeReason", status.getReason(),
            "user", user != null ? user.getUsername() : "anonymous",
            "activeConnections", authenticatedSessions.size()
        ));
        
        if (user != null) {
            // Remove player from game
            gameService.removePlayer(user.getUsername());
            
            // Broadcast player left message
            WebSocketMessage leaveMsg = WebSocketMessage.system(user.getUsername() + " left the game.");
            broadcastToAuthenticated(leaveMsg);
            
            LoggingUtil.info("User disconnected: " + user.getUsername());
        }
    }

    /**
     * Send a message to a specific session
     */
    private void sendMessage(WebSocketSession session, WebSocketMessage message) {
        try {
            String jsonMessage = objectMapper.writeValueAsString(message);
            session.sendMessage(new TextMessage(jsonMessage));
        } catch (IOException e) {
            LoggingUtil.error("Failed to send message to session: " + session.getId(), e);
        }
    }

    /**
     * Send error message to a session
     */
    private void sendError(WebSocketSession session, String error) {
        WebSocketMessage errorMsg = WebSocketMessage.error(error);
        sendMessage(session, errorMsg);
    }

    /**
     * Broadcast message to all authenticated sessions
     */
    private void broadcastToAuthenticated(WebSocketMessage message) {
        LoggingUtil.debug("Broadcasting message to authenticated sessions", Map.of(
            "messageType", message.getType(),
            "targetSessions", authenticatedSessions.size()
        ));
        
        int successCount = 0;
        int failureCount = 0;
        
        for (WebSocketSession session : authenticatedSessions.keySet()) {
            if (session.isOpen()) {
                try {
                    sendMessage(session, message);
                    successCount++;
                } catch (Exception e) {
                    LoggingUtil.error("Failed to broadcast message to session: " + session.getId(), e);
                    failureCount++;
                }
            }
        }
        
        LoggingUtil.info("Broadcast completed", Map.of(
            "messageType", message.getType(),
            "successfulSends", successCount,
            "failedSends", failureCount,
            "totalSessions", authenticatedSessions.size()
        ));
    }

    /**
     * Get current connection count
     */
    public int getConnectionCount() {
        return authenticatedSessions.size();
    }

    /**
     * Get authenticated user for session
     */
    public User getAuthenticatedUser(WebSocketSession session) {
        return authenticatedSessions.get(session);
    }
}
