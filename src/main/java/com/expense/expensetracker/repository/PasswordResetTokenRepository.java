package com.expense.expensetracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.expense.expensetracker.entity.PasswordResetToken;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    PasswordResetToken findByToken(String token);
}
