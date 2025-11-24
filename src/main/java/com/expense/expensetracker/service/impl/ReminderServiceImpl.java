package com.expense.expensetracker.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.expense.expensetracker.entity.Reminder;
import com.expense.expensetracker.entity.User;
import com.expense.expensetracker.repository.ReminderRepository;
import com.expense.expensetracker.repository.UserRepository;
import com.expense.expensetracker.service.ReminderService;

@Service
public class ReminderServiceImpl implements ReminderService {

    @Autowired
    private ReminderRepository reminderRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Reminder createReminder(Reminder reminder, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        reminder.setUser(user);
        reminder.setSent(false);
        return reminderRepository.save(reminder);
    }

    @Override
    public Reminder updateReminder(Long id, Reminder updatedReminder) {
        Reminder reminder = reminderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reminder not found"));

        reminder.setMessage(updatedReminder.getMessage());
        reminder.setRemindAt(updatedReminder.getRemindAt());
        return reminderRepository.save(reminder);
    }

    @Override
    public void deleteReminder(Long id) {
        if (!reminderRepository.existsById(id)) {
            throw new RuntimeException("Reminder not found");
        }
        reminderRepository.deleteById(id);
    }

    @Override
    public Reminder getReminderById(Long id) {
        return reminderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reminder not found"));
    }

    @Override
    public List<Reminder> getRemindersByUser(Long userId) {
        return reminderRepository.findByUserId(userId);
    }

    @Override
    public List<Reminder> getUpcomingReminders(Long userId) {
        return reminderRepository.findByUserIdAndRemindAtAfter(userId, LocalDateTime.now());
    }

    @Override
    public List<Reminder> getPendingRemindersToSend() {
        return reminderRepository.findPendingReminders(LocalDateTime.now());
    }

    @Override
    public Reminder markAsSent(Long id) {
        Reminder reminder = reminderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reminder not found"));

        reminder.setSent(true);
        return reminderRepository.save(reminder);
    }
}

