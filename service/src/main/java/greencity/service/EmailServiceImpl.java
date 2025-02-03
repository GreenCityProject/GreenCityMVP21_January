package greencity.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String senderEmail;

    @Override
    public void sendEmail(String toEmail, String subject, String body) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        String htmlBody = generateEmailContent(body);

        helper.setFrom(senderEmail);
        helper.setTo(toEmail);
        helper.setSubject(subject);
        helper.setText(htmlBody, true);

        mailSender.send(message);
    }

    private String generateEmailContent(String body) {
        Context context = new Context();
        context.setVariable("body", body);
        return templateEngine.process("email-template", context);
    }
}
