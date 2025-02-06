package greencity.service;

import jakarta.mail.MessagingException;

public interface EmailService {
    public void sendEmail(String toEmail, String subject, String body) throws MessagingException;
}
