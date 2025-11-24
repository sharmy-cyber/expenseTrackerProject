package com.expense.expensetracker.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.expense.expensetracker.entity.User;

public interface UserRepository extends JpaRepository<User,Long>{
    boolean existsByEmail(String email);

    User findByEmail(String email);

    User findByVerificationToken(String verificationToken);

}
