package com.expense.expensetracker.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.expense.expensetracker.entity.Category;
import com.expense.expensetracker.entity.Expense;
import com.expense.expensetracker.entity.RecurringExpense;
import com.expense.expensetracker.entity.User;
import com.expense.expensetracker.repository.CategoryRepository;
import com.expense.expensetracker.repository.ExpenseRepository;
import com.expense.expensetracker.repository.RecurringExpenseRepository;
import com.expense.expensetracker.repository.UserRepository;
import com.expense.expensetracker.service.RecurringExpenseService;

@Service
public class RecurringExpenseServiceImpl implements RecurringExpenseService {

    @Autowired
    private RecurringExpenseRepository recurringExpenseRepository;

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public RecurringExpense createRecurringExpense(RecurringExpense recurringExpense,Long userId) {
        recurringExpense.setActive(true);
        Category category = categoryRepository.findById(recurringExpense.getCategory().getId())
                .orElseThrow(() -> new RuntimeException("Category not found!"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        recurringExpense.setCategory(category);
        recurringExpense.setUser(user);
        return recurringExpenseRepository.save(recurringExpense);
    }

    @Override
    public RecurringExpense updateRecurringExpense(Long id, RecurringExpense updated) {
        RecurringExpense existing = recurringExpenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Recurring expense not found"));
        if (!Objects.equals(updated.getUser().getId(), existing.getUser().getId())) {
            User user = userRepository.findById(updated.getUser().getId()).orElseThrow(()->
            new RuntimeException("User not found"));
            existing.setUser(user);
        }

        if (!Objects.equals(updated.getCategory().getId(), existing.getCategory().getId())) {
            Category category = categoryRepository.findById(updated.getUser().getId()).orElseThrow(()->
            new RuntimeException("Category not found"));
            existing.setCategory(category);
        }
        existing.setTitle(updated.getTitle());
        existing.setAmount(updated.getAmount());
        existing.setStartDate(updated.getStartDate());
        existing.setFrequency(updated.getFrequency());
        existing.setActive(updated.isActive());

        return recurringExpenseRepository.save(existing);
    }

    @Override
    public void deleteRecurringExpense(Long id) {
        recurringExpenseRepository.deleteById(id);
    }

    @Override
    public RecurringExpense getRecurringExpenseById(Long id) {
        return recurringExpenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Recurring expense not found"));
    }

    @Override
    public List<RecurringExpense> getRecurringExpensesByUser(Long userId) {
        return recurringExpenseRepository.findByUserId(userId);
    }

    @Override
    @Scheduled(cron = "0 0 0 * * *")
    public void processRecurringExpenses() {
        List<RecurringExpense> recurringExpenses = recurringExpenseRepository.findByActiveTrue();
        LocalDate today = LocalDate.now();

        for (RecurringExpense rec : recurringExpenses) {
            if (shouldCreateToday(rec, today)) {

                Expense expense = new Expense();
                expense.setTitle(rec.getTitle());
                expense.setAmount(rec.getAmount());
                expense.setDate(today);
                expense.setCategory(rec.getCategory());
                expense.setUser(rec.getUser());
                expense.setDescription("Auto-generated recurring expense");

                expenseRepository.save(expense);
            }
        }
    }

    private boolean shouldCreateToday(RecurringExpense rec, LocalDate today) {
        LocalDate start = rec.getStartDate();

        switch (rec.getFrequency()) {

            case "DAILY":
                return !today.isBefore(start);

            case "WEEKLY":
                return !today.isBefore(start) &&
                        (today.getDayOfWeek() == start.getDayOfWeek());

            case "MONTHLY":
                return !today.isBefore(start) &&
                        (today.getDayOfMonth() == start.getDayOfMonth());

            case "YEARLY":
                return !today.isBefore(start) &&
                        (today.getDayOfMonth() == start.getDayOfMonth() &&
                                today.getMonth() == start.getMonth());
        }
        return false;
    }

    @Override
    public void updateStatus(Long id) {
       RecurringExpense recurringExpense = recurringExpenseRepository.findById(id).orElseThrow(()->new RuntimeException("Recurrence Expense not found"));
       recurringExpense.setActive(false);
       recurringExpenseRepository.save(recurringExpense);
    }
}
