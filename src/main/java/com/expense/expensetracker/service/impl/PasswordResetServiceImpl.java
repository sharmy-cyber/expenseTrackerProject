package com.expense.expensetracker.service.impl;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.expense.expensetracker.entity.PasswordResetToken;
import com.expense.expensetracker.entity.User;
import com.expense.expensetracker.repository.PasswordResetTokenRepository;
import com.expense.expensetracker.repository.UserRepository;
import com.expense.expensetracker.service.PasswordResetService;

@Service
public class PasswordResetServiceImpl implements PasswordResetService {

    @Autowired
    private PasswordResetTokenRepository tokenRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void createPasswordResetToken(User user, String token) {
        PasswordResetToken prt = new PasswordResetToken(
                token,
                user,
                LocalDateTime.now().plusMinutes(15));
        tokenRepo.save(prt);
    }

    @Override
    public String validatePasswordResetToken(String token) {
        PasswordResetToken prt = tokenRepo.findByToken(token);

        if (prt == null)
            return "invalid";

        if (prt.getExpiryDate().isBefore(LocalDateTime.now()))
            return "expired";

        return "valid";
    }

    @Override
    public PasswordResetToken getByToken(String token) {
        return tokenRepo.findByToken(token);
    }

    @Override
    public void updatePassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepo.save(user);
    }

    @Override
    public void deletePasswordToken(String token) {
        PasswordResetToken prt = tokenRepo.findByToken(token);
        if (prt != null) {
            tokenRepo.delete(prt);
        }
    }
}
