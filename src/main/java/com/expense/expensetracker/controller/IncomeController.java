package com.expense.expensetracker.controller;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.expense.expensetracker.entity.Income;
import com.expense.expensetracker.entity.User;
import com.expense.expensetracker.service.IncomeService;
import com.expense.expensetracker.service.UserService;

@Controller
@RequestMapping("/api/income")
public class IncomeController {

    @Autowired
    private IncomeService incomeService;

    @Autowired
    private UserService userService;

    @GetMapping("/view")
    public String showIncomePage(Model model,Principal principal) {
        User user = userService.findByEmail(principal.getName());
        model.addAttribute("incomeList", incomeService.getAllIncomeByUser(user.getId()));
        model.addAttribute("user", user);
        return "income-list";
    }
    
    @GetMapping("/addInocme/{userId}")
    public String getMethodName(Model model,@PathVariable Long userId) {
        model.addAttribute("userId", userId);
        model.addAttribute("income", new Income());
        return "income-add";
    }
    
    @PostMapping("/add/{userId}")
    public String addIncome(
            @ModelAttribute Income income,
            @PathVariable Long userId) {

    incomeService.addIncome(income, userId);
        return "redirect:/api/income/view";
    }

    @PostMapping("/update/{incomeId}")
    public ResponseEntity<Income> updateIncome(
            @PathVariable Long incomeId,
            @RequestBody Income updatedIncome) {

        Income income = incomeService.updateIncome(incomeId, updatedIncome);
        return ResponseEntity.ok(income);
    }

    @GetMapping("/delete/{incomeId}")
    public String deleteIncome(@PathVariable Long incomeId) {
        incomeService.deleteIncome(incomeId);
         return "redirect:/api/income/view";
    }

    @GetMapping("/{incomeId}")
    public ResponseEntity<Income> getIncomeById(@PathVariable Long incomeId) {
        return ResponseEntity.ok(incomeService.getIncomeById(incomeId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Income>> getIncomeByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(incomeService.getAllIncomeByUser(userId));
    }

    @GetMapping("/filter/{userId}")
    public ResponseEntity<List<Income>> filterByDateRange(
            @PathVariable Long userId,
            @RequestParam("start") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam("end") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {

        List<Income> result = incomeService.getIncomeByDateRange(userId, startDate, endDate);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/monthly/{userId}")
    public ResponseEntity<Double> getMonthlyIncome(
            @PathVariable Long userId,
            @RequestParam int month,
            @RequestParam int year) {

        Double amount = incomeService.getMonthlyIncome(userId, month, year);
        return ResponseEntity.ok(amount);
    }

    @GetMapping("/total/{userId}")
    public ResponseEntity<Double> getTotalIncome(@PathVariable Long userId) {
        return ResponseEntity.ok(incomeService.getTotalIncome(userId));
    }
}

