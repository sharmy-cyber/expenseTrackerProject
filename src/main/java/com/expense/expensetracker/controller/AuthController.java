package com.expense.expensetracker.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.expense.expensetracker.entity.User;
import com.expense.expensetracker.entity.VerificationToken;
import com.expense.expensetracker.service.EmailService;
import com.expense.expensetracker.service.UserService;
import com.expense.expensetracker.service.VerificationTokenService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@CrossOrigin("*")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private VerificationTokenService verificationTokenService;

    @Autowired
    private EmailService emailService;

    @GetMapping("/register")
    public String viewRegisterPage(Model model){
        model.addAttribute("user", new User());
        return "index";
    }

    @GetMapping("/login")
    public String viewLoginPage(Model model){
        return "login";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute User user,Model model, HttpServletRequest request) {

        User savedUser = userService.registerUser(user);
        String token = UUID.randomUUID().toString();
        verificationTokenService.createVerificationToken(savedUser, token);
        String appUrl = request.getRequestURL().toString().replace(request.getRequestURI(), "");
        String verificationUrl =appUrl+"/verify?token=" + token;
        emailService.sendVerificationMail(savedUser.getEmail(), verificationUrl);

        model.addAttribute("message", "Registration successful! Check your email to verify account.");
        return "registration-success";
    }

    @GetMapping("/verify")
    public String verify(@RequestParam("token") String token,Model model) {

        String result = verificationTokenService.validateVerificationToken(token);

        if(result.equals("valid")) {

            VerificationToken vt = verificationTokenService.getVerificationToken(token);
            User user = vt.getUser();
            userService.updateUserStatus(user.getId(), true);
            verificationTokenService.deleteVerificationToken(token);

            return "verification-success";
        }

        if(result.equals("expired")) {
            model.addAttribute("message", "Your token has expired. Please request a new one.");
            return "verification-error";
        }

        model.addAttribute("message", "Invalid verification token.");
        return "verification-error";
    }

    @GetMapping("/forgot-password")
    public String showForgetPassword(@ModelAttribute Model model){
        return "forgot-password";
    }
}

