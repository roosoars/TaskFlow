package com.roosoars.taskflow.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.roosoars.taskflow.model.Category;
import com.roosoars.taskflow.repository.CategoryRepository;

import java.util.List;

import javax.inject.Inject;

/**
 * ViewModel for Category-related operations
 * Follows MVVM architecture by separating UI state from business logic
 */
public class CategoryViewModel extends ViewModel {

    private final CategoryRepository categoryRepository;

    @Inject
    public CategoryViewModel(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    // Get all categories
    public LiveData<List<Category>> getAllCategories() {
        return categoryRepository.getAllCategories();
    }

    // Get category by ID
    public LiveData<Category> getCategoryById(long categoryId) {
        return categoryRepository.getCategoryById(categoryId);
    }

    // Insert category
    public void insert(Category category) {
        categoryRepository.insert(category);
    }

    // Update category
    public void update(Category category) {
        categoryRepository.update(category);
    }

    // Delete category
    public void delete(Category category) {
        categoryRepository.delete(category);
    }

    // Check if category can be safely deleted
    public boolean canDeleteCategory(long categoryId) {
        return categoryRepository.canDeleteCategory(categoryId);
    }
}