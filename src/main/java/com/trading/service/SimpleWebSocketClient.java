package com.trading.service;

import jakarta.websocket.ClientEndpoint;
import jakarta.websocket.OnMessage;
import jakarta.websocket.Session;

@ClientEndpoint
public class SimpleWebSocketClient {

    private final String messageToSend;

    public SimpleWebSocketClient(String messageToSend) {
        this.messageToSend = messageToSend;
    }

    @OnMessage
    public void onMessage(String message) {
        System.out.println("Received from server: " + message);
    }

    public void sendMessage(Session session) {
        try {
            session.getBasicRemote().sendText(messageToSend);
        } catch (Exception e) {
            System.err.println("Error sending message: " + e.getMessage());
        }
    }
}
