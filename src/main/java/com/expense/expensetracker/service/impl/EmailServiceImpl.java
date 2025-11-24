package com.expense.expensetracker.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.expense.expensetracker.service.EmailService;

import jakarta.mail.internet.MimeMessage;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void sendVerificationMail(String to, String verificationUrl) {

        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(to);
        mail.setSubject("Verify your SmartSpend Account");
        mail.setText(
                "Welcome to SmartSpend!\n\n" +
                        "Please click the link below to verify your account:\n" +
                        verificationUrl +
                        "\n\nThis link will expire in 15 minutes.");

        mailSender.send(mail);
    }

    public void sendResetPasswordMail(String to, String resetUrl) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(to);
        msg.setSubject("Reset Your Password");
        msg.setText("Click the link below to reset your password:\n" + resetUrl);

        mailSender.send(msg);
    }

    @Override
    public void sendEmail(String to, String subject, String body) {

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setSubject(subject);

            // If content contains HTML tags, detect it automatically
            boolean isHtml = body.contains("<") && body.contains(">");

            helper.setText(body, isHtml);

            mailSender.send(message);

        } catch (Exception e) {
            throw new RuntimeException("Failed to send email: " + e.getMessage());
        }
    }

}
