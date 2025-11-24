package com.expense.expensetracker.service;

import java.util.List;

import com.expense.expensetracker.entity.RecurringExpense;

public interface RecurringExpenseService {

    RecurringExpense createRecurringExpense(RecurringExpense recurringExpense,Long userId);

    RecurringExpense updateRecurringExpense(Long id, RecurringExpense updated);

    void deleteRecurringExpense(Long id);

    RecurringExpense getRecurringExpenseById(Long id);

    List<RecurringExpense> getRecurringExpensesByUser(Long userId);

    void processRecurringExpenses();

    void updateStatus(Long id);
}

