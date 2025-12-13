package chatapp.utils;

import io.github.cdimascio.dotenv.Dotenv;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

public class EmailService {
    private static final Dotenv dotenv = Dotenv.load();

    // Configure these properties based on your email provider and project setup
    // Ideally use environment variables or a config file
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";
    private static final String USERNAME = dotenv.get("GMAIL"); // PLACEHOLDER
    private static final String PASSWORD = dotenv.get("APP_PASS"); // PLACEHOLDER

    public static void sendPasswordReset(String toEmail, String newPassword) {
        Properties prop = new Properties();
        prop.put("mail.smtp.host", SMTP_HOST);
        prop.put("mail.smtp.port", SMTP_PORT);
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true"); // TLS

        Session session = Session.getInstance(prop,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(USERNAME, PASSWORD);
                    }
                });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(USERNAME));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(toEmail));
            message.setSubject("Password Reset - HCMUS Chat App");
            message.setText("Your password has been reset.\n\n"
                    + "New Password: " + newPassword + "\n\n"
                    + "Please login and change your password immediately.");

            Transport.send(message);
            System.out.println("LOG: Email prepared for " + toEmail + " with password: " + newPassword);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
