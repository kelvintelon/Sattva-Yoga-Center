package com.sattvayoga.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

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
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(secretManagerService.getEmailPassword().getUsername());
        // message.setFrom("emailresetsattvayoga@gmail.com");
        message.setTo(toEmail);
        message.setText(body);
        message.setSubject(subject);
        mailSender.send(message);
        System.out.println("Mail Send...");
    }
}
