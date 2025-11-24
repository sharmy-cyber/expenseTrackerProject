package com.expense.expensetracker.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.expense.expensetracker.entity.RecurringExpense;

public interface RecurringExpenseRepository extends JpaRepository<RecurringExpense, Long> {

    List<RecurringExpense> findByUserId(Long userId);

    List<RecurringExpense> findByActiveTrue();
}

