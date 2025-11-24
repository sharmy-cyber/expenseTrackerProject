package com.expense.expensetracker.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.expense.expensetracker.entity.RecurringExpense;
import com.expense.expensetracker.entity.User;
import com.expense.expensetracker.service.CategoryService;
import com.expense.expensetracker.service.RecurringExpenseService;
import com.expense.expensetracker.service.UserService;

@Controller
@RequestMapping("/api/recurring")
public class RecurringExpenseController {

    @Autowired
    private RecurringExpenseService recurringExpenseService;

    @Autowired
    private UserService userService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/view")
    public String showRecurringExpense(Model model,Principal principal){
        User user = userService.findByEmail(principal.getName());
        model.addAttribute("categories", categoryService.getAllCategoriesByUser(user.getId()));
        model.addAttribute("user", user);
        model.addAttribute("recurringList", recurringExpenseService.getRecurringExpensesByUser(user.getId()));
        return "recurring-expense";
    }

    @GetMapping("/add/{userId}")
    public String showAddRecurringExpense(Model model,@PathVariable Long userId){
        model.addAttribute("recurringExpense", new RecurringExpense());
        model.addAttribute("categories", categoryService.getAllCategoriesByUser(userId));
        model.addAttribute("userId", userId);
        return "add-recurring-expense";
    }


    @PostMapping("/create/{userId}")
    public String create(@ModelAttribute RecurringExpense recurringExpense,@PathVariable Long userId) {
         recurringExpenseService.createRecurringExpense(recurringExpense,userId);
         return "redirect:/api/recurring/view";
    }

    @GetMapping("/user/{userId}")
    public List<RecurringExpense> getByUser(@PathVariable Long userId) {
        return recurringExpenseService.getRecurringExpensesByUser(userId);
    }

    @PostMapping("/update/{id}")
    public RecurringExpense update(@PathVariable Long id,
                                   @RequestBody RecurringExpense recurringExpense) {
        return recurringExpenseService.updateRecurringExpense(id, recurringExpense);
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        recurringExpenseService.deleteRecurringExpense(id);
        return "redirect:/api/recurring/view";
    }

    @GetMapping("/updateStatus/{recurrenceId}")
    public String setStatusInactive(@PathVariable Long recurrenceId){
        recurringExpenseService.updateStatus(recurrenceId);
        return "redirect:/api/recurring/view";
    }
}
