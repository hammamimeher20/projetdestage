package com.example.projetdestage.payload.request;

public class EmailRequest {
    private String to;

    private  String From;
    private String subject;
    private String body;

    // Default constructor


    // Constructor with all fields
    public EmailRequest(String recipient, String subject, String body) {
        this.to = recipient;
        this.subject = subject;
        this.body = body;
    }

    // Getters and setters

    public String getTo() {
        return to;
    }

    public void setTo(String recipient) {
        this.to = to;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }


    public String getFrom() { return From;
    }



}
