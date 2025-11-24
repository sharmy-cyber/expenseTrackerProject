package com.expense.expensetracker.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import com.expense.expensetracker.entity.Category;
import com.expense.expensetracker.entity.User;
import com.expense.expensetracker.service.CategoryService;
import com.expense.expensetracker.service.UserService;


@Controller
@RequestMapping("/api/category")
@CrossOrigin("*")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private UserService userService;
    
    @GetMapping("/view")
    public String showCategoryForm(Model model,Principal principal) {
        User user = userService.findByEmail(principal.getName());
        model.addAttribute("user", user);
        model.addAttribute("categories", categoryService.getAllCategoriesByUser(user.getId()));
        return "category";
    }
    
     @GetMapping("/viewEditPage/{categoryId}")
    public String showEditCategoryForm(Model model,@PathVariable Long categoryId) {
        model.addAttribute("category", categoryService.getCategoryById(categoryId));
        return "edit-category";
    }

    @PostMapping("/create/{userId}")
    public String createCategory(@PathVariable Long userId,
            @ModelAttribute Category category) {
       categoryService.createCategory(userId, category);
       return "redirect:/api/category/view";
    }

    @PostMapping("/update/{categoryId}")
    public String updateCategory(@PathVariable Long categoryId,
            @ModelAttribute Category category) {
                categoryService.updateCategory(categoryId, category);
        return "redirect:/api/category/view";
    }

    @GetMapping("/delete/{categoryId}")
    public String deleteCategory(@PathVariable Long categoryId) {
        categoryService.deleteCategory(categoryId);
        return "redirect:/api/category/view";
    }

    @GetMapping("/get/{categoryId}")
    public ResponseEntity<?> getById(@PathVariable Long categoryId) {
        return ResponseEntity.ok(categoryService.getCategoryById(categoryId));
    }

    @GetMapping("/getAll/user/{userId}")
    public ResponseEntity<?> getAllByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(categoryService.getAllCategoriesByUser(userId));
    }

}
