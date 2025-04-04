package com.roosoars.taskflow.repository;

import androidx.lifecycle.LiveData;

import com.roosoars.taskflow.db.AppDatabase;
import com.roosoars.taskflow.db.CategoryDao;
import com.roosoars.taskflow.model.Category;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Repository for handling category data operations
 * Follows the Repository Pattern to abstract data sources
 * Applies the Dependency Inversion Principle by depending on abstractions
 */
@Singleton
public class CategoryRepository {

    private final CategoryDao categoryDao;

    @Inject
    public CategoryRepository(AppDatabase database) {
        this.categoryDao = database.categoryDao();
    }

    // Get all categories
    public LiveData<List<Category>> getAllCategories() {
        return categoryDao.getAllCategories();
    }

    // Get category by ID
    public LiveData<Category> getCategoryById(long categoryId) {
        return categoryDao.getCategoryById(categoryId);
    }

    // Insert a category
    public void insert(Category category) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            long id = categoryDao.insert(category);
            category.setId(id);
        });
    }

    // Update a category
    public void update(Category category) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            categoryDao.update(category);
        });
    }

    // Delete a category
    public void delete(Category category) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            categoryDao.delete(category);
        });
    }

    // Check if category can be safely deleted (has no associated tasks)
    public boolean canDeleteCategory(long categoryId) {
        final boolean[] canDelete = {false};
        try {
            AppDatabase.databaseWriteExecutor.submit(() -> {
                int taskCount = categoryDao.getTaskCountForCategory(categoryId);
                canDelete[0] = (taskCount == 0);
            }).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return canDelete[0];
    }
}