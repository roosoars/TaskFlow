package com.roosoars.taskflow.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.roosoars.taskflow.model.Category;
import com.roosoars.taskflow.repository.CategoryRepository;

import java.util.List;

import javax.inject.Inject;

public class CategoryViewModel extends ViewModel {

    private final CategoryRepository categoryRepository;
    private final MutableLiveData<String> actionFeedback = new MutableLiveData<>();

    @Inject
    public CategoryViewModel(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public LiveData<List<Category>> getAllCategories() {
        return categoryRepository.getAllCategories();
    }

    public LiveData<Category> getCategoryById(long categoryId) {
        return categoryRepository.getCategoryById(categoryId);
    }

    public void insert(Category category) {
        categoryRepository.insert(category);
        actionFeedback.setValue("Category added");
    }

    public void update(Category category) {
        categoryRepository.update(category);
        actionFeedback.setValue("Category updated");
    }

    public void delete(Category category) {
        boolean hasAssociatedTasks = getTaskCountForCategory(category.getId()) > 0;

        if (hasAssociatedTasks) {
            actionFeedback.setValue("CONFIRM_DELETE_WITH_TASKS");
        } else {
            categoryRepository.delete(category, false);
            actionFeedback.setValue("Category deleted");
        }
    }

    public void deleteWithTasks(Category category) {
        categoryRepository.delete(category, true);
        actionFeedback.setValue("Category and associated tasks deleted");
    }

    public LiveData<String> getActionFeedback() {
        return actionFeedback;
    }

    public boolean canDeleteCategory(long categoryId) {
        return categoryRepository.canDeleteCategory(categoryId);
    }

    public int getTaskCountForCategory(long categoryId) {
        return categoryRepository.getTaskCountForCategory(categoryId);
    }
}