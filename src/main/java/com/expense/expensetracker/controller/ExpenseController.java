package com.expense.expensetracker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.expense.expensetracker.entity.Category;
import com.expense.expensetracker.entity.Expense;
import com.expense.expensetracker.entity.User;
import com.expense.expensetracker.service.CategoryService;
import com.expense.expensetracker.service.ExpenseService;
import com.expense.expensetracker.service.UserService;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/api/expenses")
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    @Autowired
    private UserService userService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/view")
    public String showExpenseForm(Model model, Principal principal) {
        User user = userService.findByEmail(principal.getName());
        model.addAttribute("expenses", expenseService.getAllExpensesByUser(user.getId()));
        return "expenses";
    }

    @GetMapping("/showAddForm")
    public String showAddExpenseForm(Model model, Principal principal) {
        User user = userService.findByEmail(principal.getName());
        model.addAttribute("user", user);
        model.addAttribute("categories", categoryService.getAllCategoriesByUser(user.getId()));
        return "add-expense";
    }

    @PostMapping("/{userId}/add")
    public String createExpense(
            @PathVariable Long userId,
            @ModelAttribute Expense expense) {

        expenseService.createExpense(expense, userId);
        return "redirect:/api/expenses/view";
    }

    @GetMapping("/{userId}/all")
    public ResponseEntity<List<Expense>> getAllExpenses(@PathVariable Long userId) {
        return ResponseEntity.ok(expenseService.getAllExpensesByUser(userId));
    }

    @GetMapping("/edit/{id}")
    public String showEditPage(@PathVariable Long id, Model model,Principal principal) {
        User user = userService.findByEmail(principal.getName());
        Expense expense = expenseService.getExpenseById(id);
        List<Category> categories = categoryService.getAllCategoriesByUser(user.getId());
        model.addAttribute("expense", expense);
        model.addAttribute("categories", categories);

        return "expense-edit";
    }

    @PostMapping("/update/{expenseId}")
    public String updateExpense(
            @PathVariable Long expenseId,
            @ModelAttribute Expense expense) {
        expenseService.updateExpense(expenseId, expense);
        return "redirect:/api/expenses/view";
    }

    @GetMapping("/delete/{expenseId}")
    public String deleteExpense(@PathVariable Long expenseId) {
        expenseService.deleteExpense(expenseId);
        return "redirect:/api/expenses/view";
    }

    @GetMapping("/{userId}/sort/date")
    public ResponseEntity<List<Expense>> sortByDate(@PathVariable Long userId) {
        return ResponseEntity.ok(expenseService.getExpensesSortedByDateInDesc(userId));
    }

    @GetMapping("/{userId}/sort/amount")
    public ResponseEntity<List<Expense>> sortByAmount(@PathVariable Long userId) {
        return ResponseEntity.ok(expenseService.getExpensesSortedByAmount(userId));
    }

    @GetMapping("/{userId}/sort/category")
    public ResponseEntity<List<Expense>> sortByCategory(@PathVariable Long userId) {
        return ResponseEntity.ok(expenseService.getExpensesSortedByCategory(userId));
    }

    @GetMapping("/{userId}/category/{categoryId}")
    public ResponseEntity<List<Expense>> getByCategory(
            @PathVariable Long userId,
            @PathVariable Long categoryId) {

        return ResponseEntity.ok(expenseService.getExpensesByCategory(userId, categoryId));
    }

    @GetMapping("/{userId}/month")
    public ResponseEntity<List<Expense>> getByMonth(
            @PathVariable Long userId,
            @RequestParam int month,
            @RequestParam int year) {

        return ResponseEntity.ok(expenseService.getByMonth(userId, month, year));
    }

    @GetMapping("/{userId}/summary/category")
    public ResponseEntity<Map<String, Double>> getTotalByCategory(
            @PathVariable Long userId,
            @RequestParam int month,
            @RequestParam int year) {

        return ResponseEntity.ok(expenseService.getTotalExpenseByCategory(userId, month, year));
    }

     @GetMapping("/download/csv")
    public void downloadCSV(HttpServletResponse response) throws IOException {

        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=expenses.csv");

        expenseService.exportExpensesToCSV(response.getWriter());
    }

    /*
     * @GetMapping("/{userId}/export/pdf")
     * public ResponseEntity<byte[]> exportToPDF(@PathVariable Long userId) {
     * 
     * byte[] pdf = expenseService.exportExpensesToPDF(userId);
     * 
     * return ResponseEntity.ok()
     * .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=expenses.pdf")
     * .contentType(MediaType.APPLICATION_PDF)
     * .body(pdf);
     * }
     * 
     * @GetMapping("/{userId}/export/excel")
     * public ResponseEntity<byte[]> exportToExcel(@PathVariable Long userId) {
     * 
     * byte[] excel = expenseService.exportExpensesToExcel(userId);
     * 
     * return ResponseEntity.ok()
     * .header(HttpHeaders.CONTENT_DISPOSITION,
     * "attachment; filename=expenses.xlsx")
     * .contentType(MediaType.APPLICATION_OCTET_STREAM)
     * .body(excel);
     * }
     */

    @GetMapping("/filter")
    public String filterExpenses(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Long categoryId,
            Model model,
            Principal principal) {

        Long userId = userService.findByEmail(principal.getName()).getId();

        List<Expense> expenses = expenseService.filterExpenses(userId, keyword, month, year, categoryId);

        model.addAttribute("expenses", expenses);
        model.addAttribute("categories", categoryService.getAllCategoriesByUser(userId));

        model.addAttribute("keyword", keyword);
        model.addAttribute("month", month);
        model.addAttribute("year", year);
        model.addAttribute("categoryId", categoryId);

        // For dropdown year list
        model.addAttribute("years",
                IntStream.rangeClosed(2020, LocalDate.now().getYear()).boxed().toList());

        return "expenses"; // your page name
    }

    @GetMapping("/{userId}/budget/category")
    public ResponseEntity<Boolean> isCategoryBudgetExceeded(
            @PathVariable Long userId,
            @RequestParam Long categoryId,
            @RequestParam int month,
            @RequestParam int year) {

        return ResponseEntity.ok(
                expenseService.isCategoryBudgetExceeded(userId, categoryId, month, year));
    }

    @GetMapping("/{userId}/budget/monthly")
    public ResponseEntity<Boolean> isMonthlyBudgetExceeded(
            @PathVariable Long userId,
            @RequestParam int month,
            @RequestParam int year) {

        return ResponseEntity.ok(
                expenseService.isMonthlyBudgetExceeded(userId, month, year));
    }

    @PostMapping("/{userId}/recurring/generate")
    public ResponseEntity<?> generateRecurring(@PathVariable Long userId) {
        expenseService.generateRecurringExpenses(userId);
        return ResponseEntity.ok("Recurring expenses generated successfully!");
    }
}
