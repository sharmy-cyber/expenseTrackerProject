package com.expense.expensetracker.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.expense.expensetracker.entity.Reminder;

public interface ReminderRepository extends JpaRepository<Reminder,Long>{
    List<Reminder> findByUserId(Long userId);
    @Query("SELECT r FROM Reminder r WHERE r.remindAt <= :now AND r.sent = false")
    List<Reminder> findPendingReminders(@Param("now") LocalDateTime now);
    List<Reminder> findByUserIdAndRemindAtAfter(Long userId, LocalDateTime now);
    
}
