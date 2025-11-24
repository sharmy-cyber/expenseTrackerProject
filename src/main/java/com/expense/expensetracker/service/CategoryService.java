package com.expense.expensetracker.service;

import java.util.List;

import com.expense.expensetracker.entity.Category;

public interface CategoryService {
    Category createCategory(Long userId, Category category);

    Category updateCategory(Long categoryId, Category category);

    void deleteCategory(Long categoryId);

    Category getCategoryById(Long categoryId);

    List<Category> getAllCategoriesByUser(Long userId);

    boolean categoryExists(Long userId, String name);
}
