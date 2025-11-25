package com.expense.expensetracker.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.expense.expensetracker.service.EmailService;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;

import jakarta.mail.internet.MimeMessage;

/*@Service
public class EmailServiceImpl implements EmailService {

    @Value("${sendgrid.api.key}")
    private String sendGridApiKey;

    @Override
    public void sendVerificationMail(String toEmail, String verificationUrl) {

        String subject = "Verify your SmartSpend Account";

        String body =
                "<h2>Welcome to SmartSpend!</h2>" +
                "<p>Please click the link below to verify your account:</p>" +
                "<a href=\"" + verificationUrl + "\">Verify Email</a>" +
                "<p>This link will expire in 15 minutes.</p>";

        sendEmail(toEmail, subject, body);
    }

    @Override
    public void sendResetPasswordMail(String toEmail, String resetUrl) {

        String subject = "Reset Your Password";

        String body =
                "<h3>Password Reset Request</h3>" +
                "<p>Click the link below to reset your password:</p>" +
                "<a href=\"" + resetUrl + "\">Reset Password</a>";

        sendEmail(toEmail, subject, body);
    }

    @Override
    public void sendEmail(String toEmail, String subject, String body) {

        try {
            Email from = new Email("noreply@yourdomain.com"); // must match SendGrid verified sender
            Email to = new Email(toEmail);

            Content content = new Content("text/html", body);
            Mail mail = new Mail(from, subject, to, content);

            SendGrid sg = new SendGrid(sendGridApiKey);
            Request request = new Request();

            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sg.api(request);

            System.out.println("Email Status: " + response.getStatusCode());
            System.out.println("Email Body: " + response.getBody());

        } catch (Exception e) {
            throw new RuntimeException("Failed to send email: " + e.getMessage());
        }
    }
}
*/

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

