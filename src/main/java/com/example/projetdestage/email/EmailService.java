package com.example.projetdestage.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


@Service ("emailService")
public class EmailService {

    private JavaMailSender javaMailSender;
    @Autowired
    public EmailService(  JavaMailSender javaMailSender) {

        this.javaMailSender = javaMailSender;
    }
    public void sendEmail(String email, String welcomeMail, String s) {

    }

    public boolean sendSimpleEmail(String recipient, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(recipient);
            message.setSubject(subject);
            message.setText(body);
            javaMailSender.send(message);
            return true; // Email sent successfully
        } catch (MailException e) {
            e.printStackTrace();
            return false; // Failed to send email
        }
    }
}
