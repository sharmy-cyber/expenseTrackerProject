package com.expense.expensetracker.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.expense.expensetracker.entity.Income;

public interface IncomeRepository extends JpaRepository<Income, Long> {

    List<Income> findByUserId(Long userId);

    List<Income> findByUserIdAndDateBetween(Long userId, LocalDate start, LocalDate end);

    @Query("SELECT SUM(i.amount) FROM Income i WHERE i.user.id = :userId")
    Double getTotalIncomeByUser(@Param("userId") Long userId);

    @Query("SELECT SUM(i.amount) FROM Income i WHERE i.user.id = :userId AND MONTH(i.date) = :month AND YEAR(i.date) = :year")
    Double getMonthlyIncome(@Param("userId") Long userId,
                            @Param("month") int month,
                            @Param("year") int year);
}
