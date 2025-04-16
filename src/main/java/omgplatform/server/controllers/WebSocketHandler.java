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

@Component
public class WebSocketHandler extends TextWebSocketHandler {

    private final Map<WebSocketSession, User> userSessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws IOException {
        // Do nothing until user sends their username
        session.sendMessage(new TextMessage("Welcome! Send your username to join."));
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
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

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        User user = userSessions.remove(session);
        if (user != null) {
            broadcast("System: " + user.getUsername() + " left the game.");
        }
    }

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
