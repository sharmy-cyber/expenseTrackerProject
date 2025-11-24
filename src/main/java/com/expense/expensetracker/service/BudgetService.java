package com.expense.expensetracker.service;

import java.util.List;

import com.expense.expensetracker.entity.Budget;

public interface BudgetService {

    Budget createBudget(Budget budget, Long userId);

    Budget updateBudget(Long budgetId, Budget budget);

    void deleteBudget(Long budgetId);

    Budget getBudgetById(Long budgetId);

    List<Budget> getBudgetsForMonth(int month, int year, Long userId);

    Double getTotalSpentForCategory(Long categoryId, int month, int year, Long userId);

    Double getRemainingBudget(Long categoryId, int month, int year, Long userId);

    boolean isBudgetExceeded(Long categoryId, int month, int year, Long userId);

    List<Budget> getAllBudgetsByUser(Long userId);
    //void checkBudgetAndNotify(Long userId); 
}

