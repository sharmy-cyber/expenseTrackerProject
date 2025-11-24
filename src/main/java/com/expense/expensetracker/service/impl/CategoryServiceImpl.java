package com.expense.expensetracker.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.expense.expensetracker.entity.Category;
import com.expense.expensetracker.entity.User;
import com.expense.expensetracker.repository.CategoryRepository;
import com.expense.expensetracker.service.CategoryService;
import com.expense.expensetracker.service.UserService;

@Service
public class CategoryServiceImpl implements CategoryService{

    @Autowired
    private UserService userService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public Category createCategory(Long userId, Category category) {
        User user = userService.getUserById(userId);
        if(categoryRepository.existsByNameAndUserId(category.getName(),userId)){
            throw new RuntimeException("Category already Exists!");
        }
        category.setUser(user);
        return categoryRepository.save(category);
    }

    @Override
    public Category updateCategory(Long categoryId, Category updatedCategory) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(()->new RuntimeException("Category not found!"));
        category.setName(updatedCategory.getName());
        return categoryRepository.save(category);
    }

    @Override
    public void deleteCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(()->new RuntimeException("Categrory not found!"));
        categoryRepository.delete(category);
    }

    @Override
    public Category getCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId).orElseThrow(()->new RuntimeException("Category not found!"));
    }

    @Override
    public List<Category> getAllCategoriesByUser(Long userId) {
        return categoryRepository.findByUserId(userId);
    }

    @Override
    public boolean categoryExists(Long userId, String name) {
        return categoryRepository.existsByNameAndUserId(name,userId);
    }
    
}
