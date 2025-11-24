package com.expense.expensetracker.service.impl;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.expense.expensetracker.entity.User;
import com.expense.expensetracker.entity.VerificationToken;
import com.expense.expensetracker.repository.VerificationTokenRepository;
import com.expense.expensetracker.service.UserService;
import com.expense.expensetracker.service.VerificationTokenService;

@Service
public class VerificationTokenServiceImpl implements VerificationTokenService{

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @Autowired
    private UserService userService;

    @Override
    public VerificationToken createVerificationToken(User user, String token) {
        VerificationToken verificationToken = new VerificationToken();
        //User user = userService.getUserById(userId);
        verificationToken.setUser(user);
        verificationToken.setToken(token);
        verificationToken.setExpiryDate(LocalDateTime.now().plusMinutes(15));
        return verificationTokenRepository.save(verificationToken);
    }

    @Override
    public VerificationToken getVerificationToken(String token) {
        return verificationTokenRepository.findByToken(token);
    }

    @Override
    public String validateVerificationToken(String token) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token);

        if (verificationToken == null) {
            return "invalid";
        }

        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            return "expired";
        }

        return "valid";
    }

    @Override
    public VerificationToken generateNewVerificationToken(String oldToken) {
        VerificationToken token = verificationTokenRepository.findByToken(oldToken);

        if (token == null) {
            return null;
        }

        String newToken = UUID.randomUUID().toString();
        token.setToken(newToken);
        token.setExpiryDate(LocalDateTime.now().plusHours(24));

        return verificationTokenRepository.save(token);
    }

    @Override
    public void deleteVerificationToken(String token) {
       VerificationToken verificationToken = verificationTokenRepository.findByToken(token);
       if(verificationToken !=null){
        verificationTokenRepository.delete(verificationToken);
       }
       
    }

    @Override
    public String validatePasswordResetToken(String token) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'validatePasswordResetToken'");
    }

    @Override
    public void deleteByUser(User user) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteByUser'");
    }
    
}
