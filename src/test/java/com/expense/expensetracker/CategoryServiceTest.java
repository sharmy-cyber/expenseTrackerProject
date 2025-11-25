package com.expense.expensetracker;

import com.expense.expensetracker.entity.Category;
import com.expense.expensetracker.repository.CategoryRepository;
import com.expense.expensetracker.service.CategoryService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateCategory() {
        Category category = new Category(null, "Food", "EXPENSE");
        when(categoryRepository.save(category)).thenReturn(new Category(1L, "Food", "EXPENSE"));

        Category created = categoryService.createCategory(1l,category);

        assertThat(created.getId()).isEqualTo(1L);
        verify(categoryRepository).save(category);
    }

    @Test
    void testGetCategoryById() {
        Category category = new Category(1L, "Groceries", "EXPENSE");
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        Category found = categoryService.getCategoryById(1L);

        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("Groceries");
    }

    @Test
    void testGetAllCategories() {
        when(categoryRepository.findAll()).thenReturn(
                Arrays.asList(
                        new Category(1L, "Food", "EXPENSE"),
                        new Category(2L, "Salary", "INCOME")
                )
        );

        assertThat(categoryService.getAllCategoriesByUser(1L)).hasSize(2);
    }

    @Test
    void testDeleteCategory() {
        Long id = 1L;

        categoryService.deleteCategory(id);

        verify(categoryRepository).deleteById(id);
    }
}
