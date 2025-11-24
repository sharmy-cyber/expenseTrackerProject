package com.expense.expensetracker.service;

import java.util.List;

import com.expense.expensetracker.entity.Reminder;

public interface ReminderService {

    Reminder createReminder(Reminder reminder, Long userId);

    Reminder updateReminder(Long id, Reminder updatedReminder);

    void deleteReminder(Long id);

    Reminder getReminderById(Long id);

    List<Reminder> getRemindersByUser(Long userId);

    List<Reminder> getUpcomingReminders(Long userId);

    List<Reminder> getPendingRemindersToSend();

    Reminder markAsSent(Long id);
}

