package com.expense.expensetracker.controller;

import java.security.Principal;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.expense.expensetracker.entity.Budget;
import com.expense.expensetracker.entity.User;
import com.expense.expensetracker.service.BudgetService;
import com.expense.expensetracker.service.CategoryService;
import com.expense.expensetracker.service.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/api/budgets")
@RequiredArgsConstructor
public class BudgetController {

    @Autowired
    private BudgetService budgetService;

    @Autowired
    private UserService userService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/view")
    public String showBudgetPage(Model model,Principal principal){
        User user = userService.findByEmail(principal.getName());
        model.addAttribute("budgets", budgetService.getAllBudgetsByUser(user.getId()));
        model.addAttribute("user", user);
        return "budget";
    }

    @GetMapping("/add/{userId}")
    public String showAddBudgetPage(Model model,@PathVariable Long userId){
        model.addAttribute("budget", new Budget());
        model.addAttribute("categories", categoryService.getAllCategoriesByUser(userId));
        model.addAttribute("userId", userId);
        return "budget-add";
    }

    @PostMapping("/create/{userId}")
    public String createBudget(@ModelAttribute Budget budget,
                                          @PathVariable Long userId) {
        budgetService.createBudget(budget, userId);
        return "redirect:/api/budgets/view";
    }

    @GetMapping("/edit/{budgetId}/{userId}")
    public String showEditBudgetPage(Model model,@PathVariable Long budgetId,@PathVariable Long userId){
        model.addAttribute("budget", budgetService.getBudgetById(budgetId));
        model.addAttribute("categories", categoryService.getAllCategoriesByUser(userId));
        return "budget-edit";
    }

    @PostMapping("/update/{budgetId}")
    public String updateBudget(@PathVariable Long budgetId,
                                          @ModelAttribute Budget budget) {

        budgetService.updateBudget(budgetId, budget);
        return "redirect:/api/budgets/view";
    }

    @GetMapping("/delete/{budgetId}")
    public String deleteBudget(@PathVariable Long budgetId) {

        budgetService.deleteBudget(budgetId);
        return "redirect:/api/budgets/view";
    }

    @GetMapping("/{budgetId}")
    public ResponseEntity<?> getBudget(@PathVariable Long budgetId,
                                       @RequestParam Long userId) {

        Budget budget = budgetService.getBudgetById(budgetId);
        return ResponseEntity.ok(budget);
    }

    @GetMapping("/month")
    public ResponseEntity<?> getBudgetsForMonth(@RequestParam int month,
                                                @RequestParam int year,
                                                @RequestParam Long userId) {

        return ResponseEntity.ok(budgetService.getBudgetsForMonth(month, year, userId));
    }

    @GetMapping("/remaining")
    public ResponseEntity<?> getRemaining(@RequestParam Long categoryId,
                                          @RequestParam int month,
                                          @RequestParam int year,
                                          @RequestParam Long userId) {

        return ResponseEntity.ok(budgetService.getRemainingBudget(categoryId, month, year, userId));
    }

    @GetMapping("/exceeded")
    public ResponseEntity<?> isExceeded(@RequestParam Long categoryId,
                                        @RequestParam int month,
                                        @RequestParam int year,
                                        @RequestParam Long userId) {

        return ResponseEntity.ok(budgetService.isBudgetExceeded(categoryId, month, year, userId));
    }

    // Trigger alert check manually
    /*@PostMapping("/check-alerts")
    public ResponseEntity<?> checkAlerts(@RequestParam Long userId) {

        budgetService.checkBudgetAndNotify(userId);
        return ResponseEntity.ok("Budget alerts checked successfully");
    }*/
}
