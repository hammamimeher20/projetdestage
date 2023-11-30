package com.example.projetdestage.email;


import org.springframework.scheduling.annotation.Async;

public interface EmailSender {
    void sendEmail(String to, String subject, String body);

    @Async
    void send(String to, String email);
}

