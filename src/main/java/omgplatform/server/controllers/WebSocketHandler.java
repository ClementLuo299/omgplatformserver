package omgplatform.server.controllers;

import omgplatform.server.entities.User;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket server logic.
 *
 * @authors Clement Luo,
 * @date April 15, 2025
 * @edited April 16, 2025
 * @since 1.0
 */
@Component
public class WebSocketHandler extends TextWebSocketHandler {

    //List of user sessions
    private final Map<WebSocketSession, User> userSessions = new ConcurrentHashMap<>();

    /**
     * Run after connection is established
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws IOException {
        //Send message
        session.sendMessage(new TextMessage("Welcome! Send your username to join."));
    }

    /**
     * Handle text messages
     */
    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        //Get content of message
        String payload = message.getPayload();

        System.out.println("Received: " + payload);

        // You can handle business logic, broadcast messages, etc.
        // For now, just echo the message back to the client
        try {
            session.sendMessage(new TextMessage("Echo: " + payload));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Run after connection is closed
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        User user = userSessions.remove(session);
        if (user != null) {
            broadcast("System: " + user.getUsername() + " left the game.");
        }
    }

    /**
     * Send a message in all sessions
     */
    private void broadcast(String message) {
        for (WebSocketSession s : userSessions.keySet()) {
            if (s.isOpen()) {
                try {
                    s.sendMessage(new TextMessage(message));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
