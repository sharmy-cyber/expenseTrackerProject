package com.expense.expensetracker.service;

import com.expense.expensetracker.entity.User;
import com.expense.expensetracker.entity.VerificationToken;

public interface VerificationTokenService {
    VerificationToken createVerificationToken(User user, String token);

    VerificationToken getVerificationToken(String token);

    String validateVerificationToken(String token);

    VerificationToken generateNewVerificationToken(String oldToken);

    void deleteVerificationToken(String token);

    String validatePasswordResetToken(String token);

    void deleteByUser(User user);
}
