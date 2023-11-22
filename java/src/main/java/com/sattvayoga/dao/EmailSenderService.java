package com.sattvayoga.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

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

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom(secretManagerService.getEmailPassword().getUsername());
        helper.setTo(toEmail);
        helper.setSubject(subject);

        // Set the HTML content with the image embedded
        String htmlBody = "<html><body><img src='https://ff8bd341ea156819550dae909bbe7f1f7df2be12d326f1f104e8430-apidata.googleusercontent.com/download/storage/v1/b/sattva_logo/o/Sattva-Logo-Final-HighResLg.jpg?jk=AanfhSCWgg_nts4tIZaN4P3Fi6dSyebhHdprJfG9XJ-JNB6hMmF3wCli4QPCHxZr0qa1srefs1AAVHWqlzVxWXqaLJJOlRGj4LU1wtJEMcRyvg2o8iLZTHw_EfaHG2GH8-QToNknMRGBK2_NYYr5cOtplpVL3ST4YsxbrI2NSeqVn_DZV769sARKNS5bA91_HDPNJzhM7vH1zbOSP_qyouro7YT58StHT1u5jqfIenZGzOxsjwS3a35L1mxcotoRZpaMIqLjo_dPN531TAhvbGUCG913UQJ32w-exjCpfNlor4A8TXfYzDFVQdNl-mo1DW3ykPP7c0T7Kps3wfCxeNTZEvx-jN5PtCIKrAsTi_y91MfriuxOtm8YnOK0vSu6MTMmNZH0IDbeDIqaby9lvgAlf8yQXSdSEzsolvxX5-_ctoUHtT0iesaW84woUXhf50B1KmJ7j2EoRfjPbovxxPxMeiR61Om-z5ZnKM0nDaMro3w4p-sgqieRxDKMucJA-QIPhQoGl1f2alSFSA8ifibvSme0VdA6zXO1itZkreEGTNFuhNqyzCy0S2sC7_D-3k-Ku37QZGm4yLQAKKd9CSFQgF6CaL7TmGpnO0gGwR1NxFnZooxOJGjlHmsU9VftsJgTmqL0tak6z_fIRHNa3i2X8rIDiwTN7iDugnC62oqYE6ExRR9RhBOZv9lWlYguXna5WJiLk3lsA32krUDWa9cvlSpY7zSwPVh1qrAl6Dgz2KjsHy_TOm5SJs__4AFIiAX8m9aS4xjmg08t7mb54MZG-rW6kbjauvaqBfpVwqo4mb0deO99Nzn2a5u-H85VGdVFO5qJa6ovp0-nKfd1g83Cy2nMVafFTHaazbTFEx01U8G6ztlx2K0_LhUZ3pNVCK4CHZTKPz3NtxS9rrSR8kk4GrGlW07w507DnXbgV8_HaASPfdNOqptlN9tTNP1GdAGnxjgFqhD2yaG9-eLxiOdZ15QSdD6AEsTRjxyEDocaFaUqEKgcJZZV13GLHOkTNP9j3F42mKnGMqqWLioA4ED3j8Wl4IjLGdvs_Ty5qJnc7MCqREtmJxDVapLn7r5a5ut_lz8ckdo36z5pxQ1YgnuFIK7uHgRcb0MKcizEJQsXojd1v6wJ-ptUeGYiB4TFubL4RwqrPJa9MLJcuXoiAkA9MysWwGRRrQ1QI6Y9C6CYAcEt&isca=1' style='width:15%;'><br>" + body + "</body></html>";
        helper.setText(htmlBody, true);

        mailSender.send(message);
        System.out.println("Mail Send...");
    }
}
