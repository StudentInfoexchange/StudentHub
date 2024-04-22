package com.example.studenthub.chat;

import java.util.Date;

public class ChatOverview {

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    private String groupId;
    private String msg;
    private String title;
    private Date updatedAt;

    // Constructors
    public ChatOverview() {
        // Default constructor required for Firestore to automatically convert documents to POJOs
    }

    public ChatOverview(String groupId, String msg, String title, Date updatedAt) {
        this.groupId = groupId;
        this.msg = msg;
        this.title = title;
        this.updatedAt = updatedAt;
    }

    // Getters and setters
    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}
