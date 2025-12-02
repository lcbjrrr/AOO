package acme.mq;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.Properties;


import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import jakarta.mail.PasswordAuthentication;

import java.util.Properties;

@Component
public class EmailMessageListener {
    @Value( "${sender.email}")
    String SENDER_EMAIL;
    @Value( "${sender.pass}")
    String SENDER_PASSWORD;
    @Value( "${recipient.email}")
    String recipientEmail;
    @Value( "${subject}")
    String subject;
    @Value( "${smtp.host}")
    String smtpHost = "mail.smtp2go.com";
    @Value( "${smtp.port}")
    String smtpPort = "2525";
    String body = """
            Hi,
            
            Your PDF is ready to download:
            
            https://arxiv.org/pdf/{{FILE}}
            
            """;

    @JmsListener(destination = "${email.queue.name}")
    public void receiveMessage(String path) {
        String filename = path.substring(path.lastIndexOf('/') + 1);
        System.out.println("Consumer received message: " + filename);

        Properties props = new Properties();
        props.put("mail.smtp.host", smtpHost);
        props.put("mail.smtp.port", smtpPort);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SENDER_EMAIL, SENDER_PASSWORD);
            }
        });

        try {
            MimeMessage email = new MimeMessage(session);

            email.setFrom(new InternetAddress(SENDER_EMAIL));
            email.addRecipient(Message.RecipientType.TO, new InternetAddress(recipientEmail));
            email.setSubject(subject);
            email.setText(body.replace("{{FILE}}" ,filename));
            Transport.send(email);
            System.out.println("Email sent successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error sending email: " + e.getMessage());
        }

    }
}