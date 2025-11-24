package com.expense.expensetracker.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.expense.expensetracker.entity.User;
import com.expense.expensetracker.repository.UserRepository;
import com.expense.expensetracker.service.EmailService;
import com.expense.expensetracker.service.UserService;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Override
    public User registerUser(User user) {
        if(userRepository.existsByEmail(user.getEmail())){
            throw new RuntimeException("Email already exists!");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        user.setEnabled(false);
        return userRepository.save(user);
    }

    @Override
    public void createVerificationToken(User user, String token) {
        throw new UnsupportedOperationException("Unimplemented method 'createVerificationToken'");
    }

    @Override
    public boolean verifyEmail(String token) {
        throw new UnsupportedOperationException("Unimplemented method 'verifyEmail'");
    }

    @Override
    public User findByEmail(String email) {
        User users = userRepository.findByEmail(email);
        return users;
    }

    @Override
    public void createPasswordResetToken(User user, String token) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createPasswordResetToken'");
    }

    @Override
    public boolean resetPassword(String token, String newPassword) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'resetPassword'");
    }

    @Override
    public User updateProfile(Long userId, String name,String email) {
       User existUser = userRepository.findById(userId).orElseThrow(()->new RuntimeException("User not found"));
       if(!Objects.equals(existUser.getEmail(), email)){
        if(userRepository.existsByEmail(email)) throw new RuntimeException("Email already Exists");
        existUser.setEmail(email);
       }
       if(!Objects.equals(existUser.getName(), name)){
        existUser.setName(name);
       }
       return userRepository.save(existUser);
    }

    @Override
    public void changePassword(Long userId,String currentPassword,String newPassword,String confirmPassword) {
        User user = userRepository.findById(userId).orElseThrow(()->new RuntimeException("User not found"));
        if(!passwordEncoder.matches(currentPassword,user.getPassword())){
            throw new RuntimeException("Current Password doesn't match");
        }
        if(!newPassword.equals(confirmPassword)){
            throw new RuntimeException("New password and confirm password doesn't match");
        }
        if(passwordEncoder.matches(newPassword, user.getPassword())){
            throw new RuntimeException("Old password and new password should not be the same");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    public void updateUserStatus(Long userId, boolean isEnabled) {
        User user = userRepository.findById(userId).orElseThrow(()->new RuntimeException("User not found"));
        user.setEnabled(isEnabled);
        userRepository.save(user);
    }

    @Override
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(()->new RuntimeException("User not found"));
        userRepository.delete(user);
    }

    @Override
    public List<User> getAllUsers() {
       return userRepository.findAll();
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(()->new RuntimeException("User not found"));
    }

    @Override
    public void updateUserProfile(Long userId, String name, String newEmail, HttpServletRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setName(name);

        // If email unchanged → simply save
        if (user.getEmail().equals(newEmail)) {
            userRepository.save(user);
            return;
        }
        if(userRepository.existsByEmail(newEmail)) throw new RuntimeException("Email already exists");
        // Store pending email
        user.setPendingEmail(newEmail);
        
        // Create token
        String token = UUID.randomUUID().toString();
        user.setVerificationToken(token);
        user.setVerificationExpiry(LocalDateTime.now().plusHours(2));
        user.setEnabled(false);
        userRepository.save(user);
        String appUrl = request.getRequestURL().toString().replace(request.getRequestURI(), "");
        // Send verification link
        String link = appUrl+"verify-email?token=" + token;

        String body = "Please verify your new email by clicking this link:\n" + link;
        emailService.sendEmail(newEmail, "Verify your email change", body);
    }

    @Override
    public boolean verifyEmailUpdate(String token) {

        User user = userRepository.findByVerificationToken(token);

        if (user == null) return false;
        if (user.getVerificationExpiry().isBefore(LocalDateTime.now())) return false;

        // Update final email
        user.setEmail(user.getPendingEmail());
        user.setPendingEmail(null);

        // Clear token
        user.setVerificationToken(null);
        user.setVerificationExpiry(null);
        user.setEnabled(true);
        userRepository.save(user);
        return true;
    }

}