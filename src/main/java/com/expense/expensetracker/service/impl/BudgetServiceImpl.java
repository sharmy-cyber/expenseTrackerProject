package com.expense.expensetracker.service.impl;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.expense.expensetracker.entity.Budget;
import com.expense.expensetracker.entity.Category;
import com.expense.expensetracker.entity.User;
import com.expense.expensetracker.repository.BudgetRepository;
import com.expense.expensetracker.repository.CategoryRepository;
import com.expense.expensetracker.repository.ExpenseRepository;
import com.expense.expensetracker.repository.UserRepository;
import com.expense.expensetracker.service.BudgetService;
import com.expense.expensetracker.service.CategoryService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BudgetServiceImpl implements BudgetService {

    private final BudgetRepository budgetRepository;
    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;
    private final CategoryService categoryService;

    @Override
    public Budget createBudget(Budget budget, Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Category category = categoryService.getCategoryById(budget.getCategory().getId());


        if (budget.getYear() < LocalDate.now().getYear()) {
            throw new RuntimeException("Cannot set budget for past years");
        }

        budgetRepository.findByCategoryIdAndMonthAndYearAndUserId(
                category.getId(),
                budget.getMonth(),
                budget.getYear(),
                userId
        ).ifPresent(b -> {
            throw new RuntimeException("Budget already exists for this category and month");
        });

        budget.setUser(user);
        budget.setCategory(category);

        return budgetRepository.save(budget);
    }

    @Override
    public Budget updateBudget(Long budgetId, Budget updatedBudget) {

        Budget existing = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new RuntimeException("Budget not found"));

        Category category = categoryService.getCategoryById(updatedBudget.getCategory().getId());

        existing.setLimitAmount(updatedBudget.getLimitAmount());
        existing.setMonth(updatedBudget.getMonth());
        existing.setYear(updatedBudget.getYear());
        existing.setCategory(category);

        return budgetRepository.save(existing);
    }

    @Override
    public void deleteBudget(Long budgetId) {
        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new RuntimeException("Budget not found"));
        budgetRepository.delete(budget);
    }


    @Override
    public Budget getBudgetById(Long budgetId) {
        return budgetRepository.findById(budgetId)
                .orElseThrow(() -> new RuntimeException("Budget not found"));
    }

    @Override
    public List<Budget> getBudgetsForMonth(int month, int year, Long userId) {
        return budgetRepository.findByUserIdAndMonthAndYear(userId, month, year);
    }


    @Override
    public Double getTotalSpentForCategory(Long categoryId, int month, int year, Long userId) {
        Double spent = expenseRepository.getTotalSpent(userId, categoryId, month, year);
        return spent != null ? spent : 0.0;
    }

    @Override
    public Double getRemainingBudget(Long categoryId, int month, int year, Long userId) {

        Budget budget = budgetRepository
                .findByCategoryIdAndMonthAndYearAndUserId(categoryId, month, year, userId)
                .orElseThrow(() -> new RuntimeException("Budget not found"));

        Double spent = getTotalSpentForCategory(categoryId, month, year, userId);

        return budget.getLimitAmount() - spent;
    }

    @Override
    public boolean isBudgetExceeded(Long categoryId, int month, int year, Long userId) {
        return getRemainingBudget(categoryId, month, year, userId) < 0;
    }

    @Override
    public List<Budget> getAllBudgetsByUser(Long userId) {
       return budgetRepository.findByUserId(userId);
    }
}
