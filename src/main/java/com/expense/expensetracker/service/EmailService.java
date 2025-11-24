package com.expense.expensetracker.service;

public interface EmailService {
    void sendVerificationMail(String to, String verificationUrl);
    void sendResetPasswordMail(String to, String resetUrl);
    void sendEmail(String to, String subject, String body);
}
