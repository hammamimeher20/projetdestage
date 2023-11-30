package com.example.projetdestage.email;


import com.example.projetdestage.models.User;
import com.example.projetdestage.payload.request.EmailRequest;
import com.example.projetdestage.repository.UserRepository;
import com.example.projetdestage.security.jwt.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping ("/api/email")
public class EmailController {
    @Autowired
    JavaMailSender javaMailSender;
    @Autowired
    UserRepository userRepository;
    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/sendEmail")
    public ResponseEntity<String> sendSimpleEmail(@RequestBody EmailRequest emailRequest) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(emailRequest.getFrom());
            message.setTo(emailRequest.getTo());
            message.setSubject(emailRequest.getSubject());
            message.setText(emailRequest.getBody());
            javaMailSender.send(message);
            return ResponseEntity.ok("Email sent successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send email.");
        }
    }



}
