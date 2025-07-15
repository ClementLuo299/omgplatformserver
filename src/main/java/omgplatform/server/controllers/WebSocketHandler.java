package omgplatform.server.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import omgplatform.server.dto.WebSocketMessage;
import omgplatform.server.entities.User;
import omgplatform.server.services.UserService;
import omgplatform.server.utils.JWTUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket handler for real-time communication.
 *
 * This handler manages:
 * - WebSocket connections and sessions
 * - User authentication via JWT tokens
 * - Real-time messaging between users
 * - Connection lifecycle management
 *
 * @authors Clement Luo,
 * @date April 15, 2025
 * @edited July 14, 2025
 * @since 1.0
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketHandler extends TextWebSocketHandler {

    // List of authenticated user sessions
    private final Map<WebSocketSession, User> authenticatedSessions = new ConcurrentHashMap<>();
    
    // Object mapper for JSON serialization
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    // Services
    private final UserService userService;
    private final JWTUtil jwtUtil;

    /**
     * Handle new WebSocket connection establishment.
     * 
     * Sends a welcome message to the client and logs connection details.
     *
     * @param session The WebSocket session that was established
     * @throws IOException if message sending fails
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws IOException {
        String sessionId = session.getId();
        log.info("WebSocket connection established", Map.of(
            "sessionId", sessionId,
            "remoteAddress", session.getRemoteAddress() != null ? session.getRemoteAddress().toString() : "unknown",
            "activeConnections", authenticatedSessions.size() + 1
        ));
        
        // Send welcome message
        WebSocketMessage welcomeMsg = WebSocketMessage.system("Welcome! Please authenticate to start messaging.");
        sendMessage(session, welcomeMsg);
    }

    /**
     * Handle incoming text messages from WebSocket clients.
     * 
     * Processes different message types:
     * - AUTH: User authentication with JWT token
     * - MESSAGE: General messaging between users
     * - BROADCAST: System-wide announcements
     *
     * @param session The WebSocket session
     * @param message The incoming text message
     * @throws Exception if message processing fails
     */
    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String sessionId = session.getId();
        String payload = message.getPayload();
        
        log.debug("Processing WebSocket message", Map.of(
            "sessionId", sessionId,
            "messageLength", payload.length(),
            "messageContent", payload
        ));

        try {
            // Parse the incoming message
            WebSocketMessage wsMessage = objectMapper.readValue(payload, WebSocketMessage.class);
            
            // Handle different message types
            switch (wsMessage.getType()) {
                case "AUTH":
                    handleAuthentication(session, wsMessage);
                    break;
                case "MESSAGE":
                    handleChatMessage(session, wsMessage);
                    break;
                case "BROADCAST":
                    handleBroadcastMessage(session, wsMessage);
                    break;
                default:
                    sendError(session, "Unknown message type: " + wsMessage.getType());
            }
            
        } catch (Exception e) {
            log.error("Error processing WebSocket message", e);
            sendError(session, "Invalid message format: " + e.getMessage());
        }
    }

    /**
     * Handle WebSocket connection closure.
     * 
     * Removes the user from authenticated sessions and broadcasts
     * a departure message to other connected users.
     *
     * @param session The WebSocket session that was closed
     * @param status The close status with reason
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String sessionId = session.getId();
        User user = authenticatedSessions.remove(session);
        
        log.info("WebSocket connection closed", Map.of(
            "sessionId", sessionId,
            "closeStatus", status.getCode(),
            "closeReason", status.getReason(),
            "user", user != null ? user.getUsername() : "anonymous",
            "activeConnections", authenticatedSessions.size()
        ));
        
        if (user != null) {
            // Broadcast user departure message
            WebSocketMessage leaveMsg = WebSocketMessage.system(user.getUsername() + " has disconnected.");
            broadcastToAuthenticated(leaveMsg);
            
            log.info("User disconnected: " + user.getUsername());
        }
    }

    /**
     * Handle user authentication via JWT token.
     * 
     * Validates the JWT token and adds the user to authenticated sessions
     * if the token is valid.
     *
     * @param session The WebSocket session
     * @param wsMessage The authentication message containing JWT token
     */
    private void handleAuthentication(WebSocketSession session, WebSocketMessage wsMessage) {
        try {
            String token = (String) wsMessage.getPayload();
            String username = jwtUtil.getUsernameFromToken(token);
            
            if (username != null && !jwtUtil.isTokenExpired(token)) {
                var userOptional = userService.findByUsername(username);
                if (userOptional.isPresent()) {
                    User user = userOptional.get();
                    authenticatedSessions.put(session, user);
                    
                    // Send authentication success message
                    WebSocketMessage authSuccess = WebSocketMessage.system("Authentication successful! Welcome, " + username);
                    sendMessage(session, authSuccess);
                    
                    // Broadcast user joined message
                    WebSocketMessage joinMsg = WebSocketMessage.system(username + " has joined the chat.");
                    broadcastToAuthenticated(joinMsg);
                    
                    log.info("User authenticated: " + username);
                } else {
                    sendError(session, "User not found");
                }
            } else {
                sendError(session, "Invalid authentication token");
            }
        } catch (Exception e) {
            log.error("Authentication error", e);
            sendError(session, "Authentication failed: " + e.getMessage());
        }
    }

    /**
     * Handle chat messages between users.
     * 
     * Broadcasts the message to all authenticated users if the sender
     * is authenticated.
     *
     * @param session The WebSocket session
     * @param wsMessage The chat message
     */
    private void handleChatMessage(WebSocketSession session, WebSocketMessage wsMessage) {
        User user = authenticatedSessions.get(session);
        if (user == null) {
            sendError(session, "Authentication required");
            return;
        }
        
        // Create message with sender information
        WebSocketMessage chatMsg = WebSocketMessage.of("MESSAGE", wsMessage.getPayload());
        chatMsg.setSender(user.getUsername());
        
        // Broadcast to all authenticated users
        broadcastToAuthenticated(chatMsg);
        
        log.info("Chat message from " + user.getUsername() + ": " + wsMessage.getPayload());
    }

    /**
     * Handle broadcast messages (system announcements).
     * 
     * Only authenticated users can send broadcast messages.
     *
     * @param session The WebSocket session
     * @param wsMessage The broadcast message
     */
    private void handleBroadcastMessage(WebSocketSession session, WebSocketMessage wsMessage) {
        User user = authenticatedSessions.get(session);
        if (user == null) {
            sendError(session, "Authentication required");
            return;
        }
        
        // Create system broadcast message
        WebSocketMessage broadcastMsg = WebSocketMessage.system((String) wsMessage.getPayload());
        broadcastToAuthenticated(broadcastMsg);
        
        log.info("Broadcast message from " + user.getUsername() + ": " + wsMessage.getPayload());
    }

    /**
     * Send a message to a specific WebSocket session.
     *
     * @param session The target WebSocket session
     * @param message The message to send
     */
    private void sendMessage(WebSocketSession session, WebSocketMessage message) {
        try {
            String jsonMessage = objectMapper.writeValueAsString(message);
            session.sendMessage(new TextMessage(jsonMessage));
        } catch (IOException e) {
            log.error("Failed to send message to session: " + session.getId(), e);
        }
    }

    /**
     * Send an error message to a specific WebSocket session.
     *
     * @param session The target WebSocket session
     * @param error The error message to send
     */
    private void sendError(WebSocketSession session, String error) {
        WebSocketMessage errorMsg = WebSocketMessage.error(error);
        sendMessage(session, errorMsg);
    }

    /**
     * Broadcast a message to all authenticated WebSocket sessions.
     * 
     * Sends the message to all connected and authenticated users,
     * logging success and failure counts for monitoring.
     *
     * @param message The message to broadcast
     */
    private void broadcastToAuthenticated(WebSocketMessage message) {
        log.debug("Broadcasting message to authenticated sessions", Map.of(
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
                    log.error("Failed to broadcast message to session: " + session.getId(), e);
                    failureCount++;
                }
            }
        }
        
        log.info("Broadcast completed", Map.of(
            "messageType", message.getType(),
            "successfulSends", successCount,
            "failedSends", failureCount,
            "totalSessions", authenticatedSessions.size()
        ));
    }

    /**
     * Get the current number of active WebSocket connections.
     *
     * @return Number of authenticated sessions
     */
    public int getConnectionCount() {
        return authenticatedSessions.size();
    }

    /**
     * Get the authenticated user for a specific WebSocket session.
     *
     * @param session The WebSocket session
     * @return The authenticated user, or null if not authenticated
     */
    public User getAuthenticatedUser(WebSocketSession session) {
        return authenticatedSessions.get(session);
    }
}
