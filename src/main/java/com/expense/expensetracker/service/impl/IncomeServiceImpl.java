package com.expense.expensetracker.service.impl;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.expense.expensetracker.entity.Income;
import com.expense.expensetracker.entity.User;
import com.expense.expensetracker.repository.IncomeRepository;
import com.expense.expensetracker.repository.UserRepository;
import com.expense.expensetracker.service.IncomeService;

@Service
public class IncomeServiceImpl implements IncomeService {

    @Autowired
    private IncomeRepository incomeRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Income addIncome(Income income, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User Not Found"));

        income.setUser(user);
        income.setDate(LocalDate.now());

        return incomeRepository.save(income);
    }

    @Override
    public Income updateIncome(Long incomeId, Income incomeDetails) {
        Income existingIncome = incomeRepository.findById(incomeId)
                .orElseThrow(() -> new RuntimeException("Income Not Found"));

        existingIncome.setAmount(incomeDetails.getAmount());
        existingIncome.setSource(incomeDetails.getSource());
       

        return incomeRepository.save(existingIncome);
    }

    @Override
    public void deleteIncome(Long incomeId) {
        Income income = incomeRepository.findById(incomeId)
                .orElseThrow(() -> new RuntimeException("Income Not Found"));

        incomeRepository.delete(income);
    }

    @Override
    public Income getIncomeById(Long incomeId) {
        return incomeRepository.findById(incomeId)
                .orElseThrow(() -> new RuntimeException("Income Not Found"));
    }

    @Override
    public List<Income> getAllIncomeByUser(Long userId) {
        return incomeRepository.findByUserId(userId);
    }

    @Override
    public List<Income> getIncomeByDateRange(Long userId, LocalDate startDate, LocalDate endDate) {
        return incomeRepository.findByUserIdAndDateBetween(userId, startDate, endDate);
    }

    @Override
    public Double getTotalIncome(Long userId) {
        return incomeRepository.getTotalIncomeByUser(userId);
    }

    @Override
    public Double getMonthlyIncome(Long userId, int month, int year) {
        return incomeRepository.getMonthlyIncome(userId, month, year);
    }
}

