package com.example.projetdestage.email;

import org.springframework.stereotype.Service;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;


@Service
public class EmailServiceImpl implements EmailSender {

    public void sendSimpleEmail(String toEmail,
                                String subject,
                                String body
    ) {
        // Créer une instance de Properties pour configurer la session JavaMail
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        // Créer une instance de javax.mail.Session
        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("organizationprod@gmail.com", "gkmz ixys hyro fugu");
            }
        });

        try {
            // Créer un objet MimeMessage pour représenter l'e-mail
            Message message = new MimeMessage(session);

            // Configurer l'e-mail en définissant le destinataire, l'expéditeur, le sujet et le corps
            message.setFrom();
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject(subject);
            message.setText(body);

            // Envoyer l'e-mail
            Transport.send(message);

            System.out.println("E-mail envoyé avec succès!");

        } catch (MessagingException e) {
            System.out.println("Erreur lors de l'envoi de l'e-mail: " + e.getMessage());
        }


    }

    @Override
    public void sendEmail(String to, String subject, String body) {

    }

    @Override
    public void send(String to, String email) {

    }
  /*public void receiveMail() {
        Properties properties = new Properties();
        properties.setProperty("mail.store.protocol", "imaps");
        properties.setProperty("mail.imap.ssl.enable", "true");

        try {
            Session session = Session.getDefaultInstance(properties, null);
            Store store = session.getStore();
            store.connect("imap.gmail.com", "*******@organization.tn", "*****code*****");
            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);

            Message[] messages = inbox.getMessages();
            for (Message message : messages) {

                System.out.println("From: " + Arrays.toString(message.getFrom()));
                System.out.println("Subject: " + message.getSubject());
                System.out.println("Sent Date: " + message.getSentDate());
                System.out.println("Content: " + message.getContent().toString());
            }

            inbox.close(false);
            store.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

}








