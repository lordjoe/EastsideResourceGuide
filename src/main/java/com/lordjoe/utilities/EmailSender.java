package com.lordjoe.utilities;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;

public class EmailSender {

    // Encrypted Gmail App Password (replace with real encryption in production)


    // Gmail SMTP settings
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final int SMTP_PORT = 587;
    private static final String FROM_EMAIL = "lordjoe2000@gmail.com";
    private static final String FROM_NAME = "Resource Guide Support";
    private static final String SMTP_USERNAME = "lordjoe2000@gmail.com";
    private static final String SMTP_PASSWORD = Encrypt.decryptString(Encrypt.EastsideResource);  // Decrypted app password

    public static void send(String to, String subject, String body) {
          try {
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", SMTP_HOST);
            props.put("mail.smtp.port", SMTP_PORT);

            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(SMTP_USERNAME, SMTP_PASSWORD);
                }
            });

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_EMAIL, FROM_NAME));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject(subject);
            message.setText(body);

            Transport.send(message);
            System.out.println("Email sent to " + to);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to send email to " + to, e);
        }
    }
}
