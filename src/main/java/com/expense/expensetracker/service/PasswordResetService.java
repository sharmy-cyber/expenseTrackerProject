package com.expense.expensetracker.service;

import com.expense.expensetracker.entity.PasswordResetToken;
import com.expense.expensetracker.entity.User;

public interface PasswordResetService {

    void createPasswordResetToken(User user, String token);

    String validatePasswordResetToken(String token);

    void updatePassword(User user, String newPassword);

    PasswordResetToken getByToken(String token);

    void deletePasswordToken(String token);
}

