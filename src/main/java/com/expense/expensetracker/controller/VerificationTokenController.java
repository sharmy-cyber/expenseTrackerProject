package com.expense.expensetracker.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.expense.expensetracker.entity.User;
import com.expense.expensetracker.entity.VerificationToken;
import com.expense.expensetracker.service.EmailService;
import com.expense.expensetracker.service.UserService;
import com.expense.expensetracker.service.VerificationTokenService;

@RestController
@CrossOrigin("*")
@RequestMapping("/token")
public class VerificationTokenController {
    @Autowired
    private UserService userService;

    @Autowired
    private VerificationTokenService verificationTokenService;

    @Autowired
    private EmailService emailService;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User user) {
       // User user = userService.registerUser(userDto);


        String token = UUID.randomUUID().toString();
        verificationTokenService.createVerificationToken(user, token);

        // send verification email
        String verificationUrl = "http://localhost:8080/api/auth/verify?token=" + token;
        //emailService.sendVerificationMail(user.getEmail(), verificationUrl);

        return ResponseEntity.ok("Registration successful! Check your email to verify account.");
    }

    @GetMapping("/verify")
    public ResponseEntity<String> verifyEmail(@RequestParam("token") String token) {
        String result = verificationTokenService.validateVerificationToken(token);

        if (result.equals("valid")) {
            User user = verificationTokenService.getVerificationToken(token).getUser();
            userService.updateUserStatus(user.getId(), true);
            verificationTokenService.deleteVerificationToken(token);

            return ResponseEntity.ok("Email verified successfully! You can now log in.");
        }

        if (result.equals("expired")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Token expired! Please request a new verification link.");
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Invalid verification token!");
    }

    @PostMapping("/resend-token")
    public ResponseEntity<String> resendToken(@RequestParam("oldToken") String oldToken) {
        VerificationToken newToken = verificationTokenService.generateNewVerificationToken(oldToken);

        if (newToken == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Invalid token!");
        }

        String verificationUrl = "http://localhost:8080/api/auth/verify?token=" + newToken.getToken();
        //emailService.sendVerificationMail(newToken.getUser().getEmail(), verificationUrl);

        return ResponseEntity.ok("A new verification link has been sent to your email.");
    }

    
}
