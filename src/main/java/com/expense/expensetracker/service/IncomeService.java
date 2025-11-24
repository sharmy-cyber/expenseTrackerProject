package com.expense.expensetracker.service;

import java.time.LocalDate;
import java.util.List;

import com.expense.expensetracker.entity.Income;

public interface IncomeService {

    Income addIncome(Income income, Long userId);

    Income updateIncome(Long incomeId, Income incomeDetails);

    void deleteIncome(Long incomeId);

    Income getIncomeById(Long incomeId);

    List<Income> getAllIncomeByUser(Long userId);

    List<Income> getIncomeByDateRange(Long userId, LocalDate startDate, LocalDate endDate);

    Double getTotalIncome(Long userId);

    Double getMonthlyIncome(Long userId, int month, int year);
}
