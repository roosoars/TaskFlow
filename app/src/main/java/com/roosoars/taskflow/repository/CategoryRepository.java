package com.roosoars.taskflow.repository;

import androidx.lifecycle.LiveData;

import com.roosoars.taskflow.db.AppDatabase;
import com.roosoars.taskflow.db.CategoryDao;
import com.roosoars.taskflow.db.TaskDao;
import com.roosoars.taskflow.model.Category;
import com.roosoars.taskflow.model.Task;
import com.roosoars.taskflow.observer.TaskObserver;

import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class CategoryRepository {

    private final CategoryDao categoryDao;
    private final TaskDao taskDao;
    private final TaskObserver taskObserver;

    @Inject
    public CategoryRepository(AppDatabase database, TaskObserver taskObserver) {
        this.categoryDao = database.categoryDao();
        this.taskDao = database.taskDao();
        this.taskObserver = taskObserver;
    }

    public LiveData<List<Category>> getAllCategories() {
        return categoryDao.getAllCategories();
    }

    public LiveData<Category> getCategoryById(long categoryId) {
        return categoryDao.getCategoryById(categoryId);
    }

    public void insert(Category category) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            long id = categoryDao.insert(category);
            category.setId(id);
        });
    }

    public void update(Category category) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            categoryDao.update(category);
        });
    }

    public void delete(Category category, boolean deleteTasks) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            if (deleteTasks) {
                List<Task> tasksToDelete = getTasksWithCategorySync(category.getId());
                if (tasksToDelete != null && !tasksToDelete.isEmpty()) {
                    for (Task task : tasksToDelete) {
                        taskDao.delete(task);
                        taskObserver.notifyTaskDeleted(task);
                    }
                }
            } else {
                taskDao.clearCategoryForTasks(category.getId());
            }

            categoryDao.delete(category);
        });
    }

    private List<Task> getTasksWithCategorySync(long categoryId) {
        try {
            return AppDatabase.databaseWriteExecutor.submit(() ->
                    taskDao.getTasksByCategorySync(categoryId)).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

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

    public int getTaskCountForCategory(long categoryId) {
        try {
            return AppDatabase.databaseWriteExecutor.submit(() ->
                    categoryDao.getTaskCountForCategory(categoryId)).get();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}