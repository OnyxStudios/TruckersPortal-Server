package dev.onyxstudios.truckersportal.utils;

import dev.onyxstudios.truckersportal.TruckersPortal;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailUtils {

    public static void sendRemovalEmail(String email, String name) {
        String text = "Hey " + name + ", this is a notification to let you know that you have been removed from " + TruckersPortal.carrierProfile.name;
        sendEmail(email, text, "Removal Notification");
    }

    public static void sendRegistrationEmail(String email, String name, String password) {
        String domain = TruckersPortal.carrierProfile.domain;
        String text = "Hey " + name + ", You've been successfully registered at " + TruckersPortal.carrierProfile.name + ". You're login details are:\n\nUsername: " + email + "\nPassword: " + password + "\n\nTo login, please visit: " + (domain.contains("http") ? domain : "http://" + domain);
        sendEmail(email, text, "Password Changed Notification");
    }

    public static void sendPasswordChangeEmail(String email, String name) {
        String domain = TruckersPortal.carrierProfile.domain;
        String text = "Hey " + name + ",\nYour password has successfully been changed. If this wasn't you contact your panel admin immediately.\n\nOtherwise to login, please visit: " + (domain.contains("http") ? domain : "http://" + domain);
        sendEmail(email, text, "Portal Registration Notification");
    }

    public static void sendEmail(String email, String text, String title) {
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
            message.setSubject(title);
            message.setText(text);
            Transport.send(message);
        } catch (AddressException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
