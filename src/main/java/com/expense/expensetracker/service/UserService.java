package com.expense.expensetracker.service;

import java.util.List;

import com.expense.expensetracker.entity.User;

import jakarta.servlet.http.HttpServletRequest;

public interface UserService {
    User registerUser(User user);
    void createVerificationToken(User user, String token);
    boolean verifyEmail(String token);
    User findByEmail(String email);
    void createPasswordResetToken(User user, String token);
    boolean resetPassword(String token, String newPassword);
    User updateProfile(Long userId, String name,String email);
    void changePassword(Long userId,String currentPassword, String newPassword,String confirmPassword);
    void deleteUser(Long userId);
    List<User> getAllUsers();
    User getUserById(Long id);
    void updateUserStatus(Long userId,boolean isEnabled);
    void updateUserProfile(Long userId, String name, String newEmail, HttpServletRequest request);
    boolean verifyEmailUpdate(String token);
}