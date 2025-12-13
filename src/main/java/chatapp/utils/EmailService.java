package chatapp.utils;

import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

public class EmailService {

    // Configure these properties based on your email provider and project setup
    // Ideally use environment variables or a config file
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";
    private static final String USERNAME = "your_email@gmail.com"; // PLACEHOLDER
    private static final String PASSWORD = "your_app_password"; // PLACEHOLDER

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

            // Uncomment to actually send when credentials are valid
            // Transport.send(message);
            System.out.println("LOG: Email prepared for " + toEmail + " with password: " + newPassword);
            System.out
                    .println("NOTE: Email sending is commented out. Check EmailService.java to configure credentials.");

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
