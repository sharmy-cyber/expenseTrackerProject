package com.expense.expensetracker.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.expense.expensetracker.entity.PasswordResetToken;
import com.expense.expensetracker.entity.User;
import com.expense.expensetracker.service.EmailService;
import com.expense.expensetracker.service.PasswordResetService;
import com.expense.expensetracker.service.UserService;


@Controller
@RequestMapping("/password")
public class PasswordController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordResetService passwordResetService;

    @Autowired
    private EmailService emailService;

    @GetMapping("/forgot-password")
    public String forgotPasswordPage() {
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam String email, Model model) {

        User user = userService.findByEmail(email);

        if (user == null) {
            model.addAttribute("error", "Email not found!");
            return "forgot-password";
        }

        String token = UUID.randomUUID().toString();
        passwordResetService.createPasswordResetToken(user, token);

        String resetUrl = "http://localhost:8080/password/reset-password?token=" + token;

        emailService.sendResetPasswordMail(email, resetUrl);

        model.addAttribute("message", "Password reset link sent to your email!");
        return "forgot-password";
    }

    @GetMapping("/reset-password")
    public String resetPassword(@RequestParam String token, Model model) {

        String result = passwordResetService.validatePasswordResetToken(token);

        if (!result.equals("valid")) {
            model.addAttribute("error", result);
            return "reset-password";
        }

        model.addAttribute("token", token);
        return "reset-password";
    }

    // STEP 4 → Save New Password
    @PostMapping("/reset-password")
    public String updatePassword(
            @RequestParam String token,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            Model model) {

        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match!");
            model.addAttribute("token", token);
            return "reset-password";
        }

        PasswordResetToken prt = passwordResetService.getByToken(token);
        User user = prt.getUser();

        passwordResetService.updatePassword(user, newPassword);
        passwordResetService.deletePasswordToken(token);;

        model.addAttribute("message", "Password updated! You can now login.");
        return "login";
    }
}
