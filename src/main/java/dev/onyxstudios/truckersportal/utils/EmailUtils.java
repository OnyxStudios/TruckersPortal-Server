package dev.onyxstudios.truckersportal.utils;

import dev.onyxstudios.truckersportal.TruckersPortal;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailUtils {

    public static void sendRegistrationEmail(String email, String name, String password) {
        String domain = TruckersPortal.carrierProfile.domain;
        String text = "Hey " + name + ", You've been successfully registered at " + TruckersPortal.carrierProfile.name + ". You're login details are:\n\nUsername: " + email + "\nPassword: " + password + "\n\nTo login, please visit: " + (domain.contains("http") ? domain : "http://" + domain);

        Properties properties = new Properties();
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(TruckersPortal.USERNAME, TruckersPortal.PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(TruckersPortal.USERNAME));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(email));
            message.setSubject("Portal Registration Notification");
            message.setText(text);
            Transport.send(message);
        } catch (AddressException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
