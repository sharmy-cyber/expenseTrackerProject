package com.expense.expensetracker.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.expense.expensetracker.entity.Reminder;
import com.expense.expensetracker.entity.User;
import com.expense.expensetracker.service.ReminderService;
import com.expense.expensetracker.service.UserService;

@Controller
@RequestMapping("/api/reminders")
public class ReminderController {

    @Autowired
    private ReminderService reminderService;

    @Autowired
    private UserService userService;

    @GetMapping("/view")
    public String showRemainderPage(Model model,Principal principal){
        User user = userService.findByEmail(principal.getName());
        model.addAttribute("reminders", reminderService.getRemindersByUser(user.getId()));
        model.addAttribute("user", user);
        return "reminders";
    }

    @GetMapping("/add/{userId}")
    public String showAddRemainderPage(Model model,@PathVariable Long userId){
        model.addAttribute("userId", userId);
        model.addAttribute("reminder", new Reminder());
        return "add-reminder";
    }
    
    @PostMapping("/create/{userId}")
    public String createReminder(
            @ModelAttribute Reminder reminder,
            @PathVariable Long userId) {
        reminderService.createReminder(reminder, userId);
        return "redirect:/api/reminders/view";
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<Reminder> updateReminder(
            @PathVariable Long id,
            @RequestBody Reminder updatedReminder) {

        return ResponseEntity.ok(reminderService.updateReminder(id, updatedReminder));
    }

    @GetMapping("/delete/{id}")
    public String deleteReminder(@PathVariable Long id) {
        reminderService.deleteReminder(id);
        return "redirect:/api/reminders/view";
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reminder> getReminder(@PathVariable Long id) {
        return ResponseEntity.ok(reminderService.getReminderById(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Reminder>> getByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(reminderService.getRemindersByUser(userId));
    }

    @GetMapping("/upcoming/{userId}")
    public ResponseEntity<List<Reminder>> upcoming(@PathVariable Long userId) {
        return ResponseEntity.ok(reminderService.getUpcomingReminders(userId));
    }

    @GetMapping("/pending")
    public ResponseEntity<List<Reminder>> pending() {
        return ResponseEntity.ok(reminderService.getPendingRemindersToSend());
    }

    @PutMapping("/sent/{id}")
    public ResponseEntity<Reminder> markSent(@PathVariable Long id) {
        return ResponseEntity.ok(reminderService.markAsSent(id));
    }
}

