package com.thirdeye3.usermanager.services.impl;

import com.thirdeye3.usermanager.exceptions.EmailException;
import com.thirdeye3.usermanager.services.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;
    
    @Override
    @Async("taskExecutor")
    public void sendZipToEmail(String to, String subject, String messageBody,
                               String zipFileName, byte[] zipData) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(messageBody, false);
            helper.addAttachment(zipFileName, new ByteArrayResource(zipData));

            mailSender.send(message);

        } catch (MessagingException e) {
            throw new EmailException("Failed to send email with ZIP attachment: " + e.getMessage());
        }
    }
}
