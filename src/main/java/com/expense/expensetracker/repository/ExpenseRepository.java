package com.expense.expensetracker.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.expense.expensetracker.entity.Expense;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
        List<Expense> findByUserId(Long userId);

        List<Expense> findByUserIdAndDateBetween(Long userId, LocalDate start, LocalDate end);

        List<Expense> findByUserIdAndCategoryId(Long userId, Long categoryId);

        List<Expense> findByUserIdAndAmountBetween(Long userId, Double min, Double max);

        @Query("SELECT e FROM Expense e WHERE e.user.id = :userId " +
                        "AND (LOWER(e.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
                        "OR LOWER(e.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
        List<Expense> search(@Param("userId") Long userId, @Param("keyword") String keyword);

        List<Expense> findByUserIdOrderByDateDesc(Long userId);

        List<Expense> findByUserIdOrderByDateAsc(Long userId);

        List<Expense> findByUserIdOrderByAmountAsc(Long userId);

        List<Expense> findByUserIdOrderByAmountDesc(Long userId);

        List<Expense> findByUserIdOrderByCategoryNameAsc(Long userId);

        @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.user.id = :userId AND MONTH(e.date) = :month AND YEAR(e.date) = :year")
        Double getTotalExpenseForMonth(@Param("userId") Long userId,
                        @Param("month") int month,
                        @Param("year") int year);

        @Query("SELECT e.category.name, SUM(e.amount) FROM Expense e " +
                        "WHERE e.user.id = :userId AND MONTH(e.date) = :month AND YEAR(e.date) = :year " +
                        "GROUP BY e.category.name")
        List<Object[]> getTotalExpenseByCategory(@Param("userId") Long userId,
                        @Param("month") int month,
                        @Param("year") int year);

        @Query("SELECT e.date, SUM(e.amount) FROM Expense e " +
                        "WHERE e.user.id = :userId AND MONTH(e.date) = :month AND YEAR(e.date) = :year " +
                        "GROUP BY e.date ORDER BY e.date ASC")
        List<Object[]> getDailyExpenseSummary(@Param("userId") Long userId,
                        @Param("month") int month,
                        @Param("year") int year);

        @Query("SELECT MONTH(e.date), SUM(e.amount) FROM Expense e " +
                        "WHERE e.user.id = :userId AND YEAR(e.date) = :year " +
                        "GROUP BY MONTH(e.date)")
        List<Object[]> getMonthlyExpenseSummary(@Param("userId") Long userId,
                        @Param("year") int year);

        @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.user.id = :userId AND e.category.id = :categoryId " +
                        "AND MONTH(e.date) = :month AND YEAR(e.date) = :year")
        Double getCategoryExpenseTotal(@Param("userId") Long userId,
                        @Param("categoryId") Long categoryId,
                        @Param("month") int month,
                        @Param("year") int year);

        @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.user.id = :userId AND MONTH(e.date) = :month AND YEAR(e.date) = :year")
        Double getMonthlyExpenseTotal(@Param("userId") Long userId,
                        @Param("month") int month,
                        @Param("year") int year);

        // List<Expense> findByUserIdAndIsRecurringTrue(Long userId);
        /*
         * Optional<Budget> findByUserIdAndCategoryIdAndMonthAndYear(
         * Long userId, Long categoryId, int month, int year);
         */

        @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.user.id = :userId " +
                        "AND e.category.id = :categoryId AND MONTH(e.date) = :month AND YEAR(e.date) = :year")
        Double getTotalExpenseByCategory(@Param("userId") Long userId,
                        @Param("categoryId") Long categoryId,
                        @Param("month") int month,
                        @Param("year") int year);

        @Query("SELECT e FROM Expense e WHERE e.user.id = :userId "
                        + "AND MONTH(e.date) = :month "
                        + "AND YEAR(e.date) = :year")
        List<Expense> getByMonth(@Param("userId") Long userId,
                        @Param("month") int month,
                        @Param("year") int year);

        @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.user.id = :userId AND e.category.id = :categoryId AND MONTH(e.date) = :month AND YEAR(e.date) = :year")
        Double getTotalSpent(Long userId, Long categoryId, Integer month, Integer year);
}
