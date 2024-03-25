package com.example.studenthub.assistance;

import java.util.Date;

public class Assistance {
    private String id;
    private String byUid;
    private Date date;
    private String description;
    private String category;

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    private String contact;

    // Constructors
    public Assistance() {
        // Default constructor required for Firestore to automatically convert documents to POJOs
    }

    public Assistance(String id, String byUid, Date date, String description, String category, String contact) {
        this.id = id;
        this.byUid = byUid;
        this.date = date;
        this.description = description;
        this.category = category;
        this.contact = contact;
    }

}
