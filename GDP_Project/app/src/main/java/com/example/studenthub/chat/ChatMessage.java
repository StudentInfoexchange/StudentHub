package com.example.studenthub.chat;

public class ChatMessage {
    private String message;
    private String senderId;
    private long timestamp;

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    private String senderName;

    public ChatMessage() {
        // Required empty public constructor
    }

    public ChatMessage(String message, String senderId, long timestamp, String  senderName) {
        this.message = message;
        this.senderId = senderId;
        this.timestamp = timestamp;
        this.senderName = senderName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    // Getters and setters
}
