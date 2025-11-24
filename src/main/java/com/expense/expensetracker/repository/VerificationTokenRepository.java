package com.expense.expensetracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.expense.expensetracker.entity.VerificationToken;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken,Long>{
    VerificationToken findByToken(String token);
}