package com.expense.expensetracker.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.expense.expensetracker.entity.Budget;

public interface BudgetRepository extends JpaRepository<Budget,Long>{
    List<Budget> findByUserIdAndMonthAndYear(Long userId, Integer month, Integer year);

    Optional<Budget> findByIdAndUserId(Long budgetId, Long userId);

    Optional<Budget> findByCategoryIdAndMonthAndYearAndUserId(Long categoryId, Integer month, Integer year, Long userId);

    List<Budget> findByUserId(Long userId);
}
