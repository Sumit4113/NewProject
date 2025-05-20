package com.example.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendFeedbackEmail(String subject, String email, String message) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

            helper.setTo("jdsumit01@gmail.com"); // Where you want to receive feedback
            helper.setSubject("New Feedback from " + subject);
            helper.setText("Sender Email: " + email + "<br><br>Message:<br>" + message, true);
            helper.setFrom("jdsumit01@gmail.com");

            mailSender.send(mimeMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
