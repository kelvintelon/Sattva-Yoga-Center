package com.sattvayoga.dao;

import com.sattvayoga.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
public class EmailSenderService {

    private final JavaMailSender mailSender;

    @Autowired
    public EmailSenderService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Autowired
    private SecretManagerService secretManagerService;

    public void sendEmail(String toEmail, String subject, String body) throws Throwable {
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setFrom(secretManagerService.getEmailPassword().getUsername());
//        // message.setFrom("emailresetsattvayoga@gmail.com");
//        message.setTo(toEmail);
//        body = "<img src='https://i.imgur.com/YFxtS94.jpg'>" + "\n" + body;
//        message.setText(body);
//        message.setSubject(subject);
//        mailSender.send(message);
//        System.out.println("Mail Send...");

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(secretManagerService.getEmailPassword().getUsername());
            helper.setTo(toEmail);
            helper.setSubject(subject);

            // Set the HTML content with the image embedded
            String htmlBody = "<html><body><img src='https://storage.googleapis.com/sattva_logo/Sattva-Logo-Final-HighResLg.jpg' style='width:15%;'><br>" + body + "</body></html>";
            helper.setText(htmlBody, true);

            mailSender.send(message);
            System.out.println("Mail Send...");
        } catch (Exception e) {
            System.out.println("Error message: " + e.getMessage());
            System.out.println("Cause: " + e.getCause());
            throw new CustomException("Error sending email.");
        }
    }
}
