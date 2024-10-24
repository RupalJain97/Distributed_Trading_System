package com.trading.config;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import java.util.concurrent.CopyOnWriteArrayList;

public class CustomWebSocketHandler extends TextWebSocketHandler {

    // List to keep track of connected WebSocket sessions
    private static final CopyOnWriteArrayList<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.add(session);
        System.out.println("New WebSocket connection established: " + session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        System.out.println("Received message: " + message.getPayload());

        // Echo the message back to the sender or broadcast to all clients
        for (WebSocketSession wsSession : sessions) {
            if (wsSession.isOpen()) {
                try {
                    wsSession.sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session);
        System.out.println("WebSocket connection closed: " + session.getId());
    }

    public void broadcast(String message) {
        for (WebSocketSession wsSession : sessions) {
            if (wsSession.isOpen()) {
                try {
                    System.out.println("Sending msg to session" + wsSession.getId());
                    wsSession.sendMessage(new TextMessage(message));
                } catch (Exception e) {
                    System.err.println("Error broadcasting message: " + e.getMessage());
                }
            }
        }
    }
}
