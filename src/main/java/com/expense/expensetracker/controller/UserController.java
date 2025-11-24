package com.expense.expensetracker.controller;

import java.security.Principal;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.expense.expensetracker.entity.User;
import com.expense.expensetracker.service.EmailService;
import com.expense.expensetracker.service.UserService;
import com.expense.expensetracker.service.VerificationTokenService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/user")
@CrossOrigin("*")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private VerificationTokenService tokenService;

    @Autowired
    private EmailService emailService;

    @GetMapping("/dashboard")
    public String showDashboard(Model model, Principal principal) {
        User user = userService.findByEmail(principal.getName());
        model.addAttribute("user", user);
        return "dashboard";
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        User savedUser = userService.registerUser(user);
        String token = UUID.randomUUID().toString();
        tokenService.createVerificationToken(savedUser, token);
        String url = "http://localhost:8080/api/auth/verify?token=" + token;
        emailService.sendVerificationMail(savedUser.getEmail(), url);
        return ResponseEntity.ok().body("Registration successful. Please check your email to verify.");
    }

    @GetMapping("/getByEmail/{email}")
    public ResponseEntity<?> getUserByEmail(@PathVariable String email) {
        return ResponseEntity.ok().body(userService.findByEmail(email));
    }

    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok().body("User deleted successfully");
    }

    @GetMapping("/get")
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok().body(userService.getAllUsers());
    }

    @GetMapping("/get/{userId}")
    public ResponseEntity<?> getUserById(@PathVariable Long userId) {
        return ResponseEntity.ok().body(userService.getUserById(userId));
    }

    @GetMapping("/profile/{userId}")
    public String showProfilePage(Model model, @PathVariable Long userId) {
        User user = userService.getUserById(userId);
        model.addAttribute("user", user);
        return "profile";
    }

    @GetMapping("/updateProfile/{userId}")
    public String showUpdateProfilePage(Model model, @PathVariable Long userId) {
        User user = userService.getUserById(userId);
        model.addAttribute("user", user);
        return "edit-profile";
    }

    @PostMapping("/update-profile/{id}")
    public String updateProfile(
            @PathVariable Long id,
            @RequestParam String name,
            @RequestParam String email, HttpServletRequest request) {
        
        userService.updateUserProfile(id, name, email,request);
        return "redirect:/user/profile/" + id;
    }

    @GetMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestParam String token) {
        boolean success = userService.verifyEmailUpdate(token);

        if (success)
            return ResponseEntity.ok("Email verified successfully!");

        return ResponseEntity.badRequest().body("Invalid or expired verification token.");
    }

    @PostMapping("/update/{userId}")
    public String updateUserProfile(@PathVariable Long userId, @ModelAttribute User user) {
        userService.updateProfile(userId, user.getName(), user.getEmail());
        return "redirect:/user/profile/" + user.getId();
    }

    @GetMapping("/password/update/{userId}")
    public String showUpdatePasswordPage(Model model, @PathVariable Long userId) {
        model.addAttribute("userId", userId);
        return "change-password";
    }

    @PostMapping("/updatePassword/{userId}")
    public String updatePassword(
            @PathVariable Long userId,
            @RequestParam String currentPassword,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword) {

        userService.changePassword(userId, currentPassword, newPassword, confirmPassword);

        return "redirect:/user/profile/" + userId + "?passwordChanged";
    }

}
