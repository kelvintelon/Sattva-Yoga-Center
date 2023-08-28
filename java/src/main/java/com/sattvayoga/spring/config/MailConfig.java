package com.sattvayoga.spring.config;

import com.sattvayoga.dao.SecretManagerService;
import com.sattvayoga.model.JavaMailCredentials;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailConfig {

    @Autowired
    private SecretManagerService secretManagerService;

    @Bean
    public JavaMailSender javaMailSender() throws Throwable {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        JavaMailCredentials mailConfiguration = secretManagerService.getEmailPassword();

        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);
        mailSender.setUsername(mailConfiguration.getUsername());
        mailSender.setPassword(mailConfiguration.getPassword());

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.smtp.auth", true);
        props.put("mail.smtp.starttls.enable", true);

        return mailSender;
    }
}
