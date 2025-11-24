package com.expense.expensetracker.service;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.expense.expensetracker.entity.Expense;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.codec.Base64.OutputStream;

public interface ExpenseService {

    Expense createExpense(Expense expense, Long userId);

    Expense updateExpense(Long expenseId, Expense updatedExpense);

    void deleteExpense(Long expenseId);

    Expense getExpenseById(Long expenseId);

    List<Expense> getAllExpensesByUser(Long userId);

    List<Expense> getExpensesByDateRange(Long userId, LocalDate start, LocalDate end);

    List<Expense> getExpensesByCategory(Long userId, Long categoryId);

    List<Expense> getExpensesByAmountRange(Long userId, double minAmount, double maxAmount);

    List<Expense> searchExpenses(Long userId, String keyword);

    List<Expense> getExpensesSortedByDateInDesc(Long userId);

    List<Expense> getExpensesSortedByAmount(Long userId);

    List<Expense> getExpensesSortedByCategory(Long userId);

    Double getTotalExpenseForMonth(Long userId, int month, int year);

    Map<String, Double> getTotalExpenseByCategory(Long userId, int month, int year);

    Map<LocalDate, Double> getDailyExpenseSummary(Long userId, int month, int year);

    Map<Integer, Double> getMonthlyExpenseSummary(Long userId, int year);

    //byte[] exportExpensesToPDF(Long userId);

   // byte[] exportExpensesToExcel(Long userId);

    boolean isCategoryBudgetExceeded(Long userId, Long categoryId, int month, int year);

    boolean isMonthlyBudgetExceeded(Long userId, int month, int year);

    void generateRecurringExpenses(Long userId);
    List<Expense> getByMonth(Long userId,int month,int year);
    List<Expense> filterExpenses(Long userId, String keyword, Integer month, Integer year, Long categoryId);
    public void exportExpensesToCSV(PrintWriter writer);
}

