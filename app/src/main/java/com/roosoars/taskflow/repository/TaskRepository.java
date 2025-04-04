package com.roosoars.taskflow.repository;

import androidx.lifecycle.LiveData;

import com.roosoars.taskflow.db.AppDatabase;
import com.roosoars.taskflow.db.TaskDao;
import com.roosoars.taskflow.model.Task;
import com.roosoars.taskflow.model.TaskWithCategory;
import com.roosoars.taskflow.observer.TaskObserver;
import com.roosoars.taskflow.strategy.SortStrategy;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Repository for handling task data operations
 * Follows the Repository Pattern to abstract data sources
 * Applies the Dependency Inversion Principle by depending on abstractions
 */
@Singleton
public class TaskRepository {

    private final TaskDao taskDao;
    private final TaskObserver taskObserver;
    private SortStrategy sortStrategy;

    @Inject
    public TaskRepository(AppDatabase database, TaskObserver taskObserver) {
        this.taskDao = database.taskDao();
        this.taskObserver = taskObserver;
    }

    // Set the sort strategy (Strategy pattern)
    public void setSortStrategy(SortStrategy sortStrategy) {
        this.sortStrategy = sortStrategy;
    }

    // Get all tasks using the current sort strategy
    public LiveData<List<Task>> getAllTasks() {
        return sortStrategy != null ? sortStrategy.getSortedTasks() : taskDao.getAllTasksByDate();
    }

    // Get all tasks with their categories
    public LiveData<List<TaskWithCategory>> getAllTasksWithCategory() {
        return taskDao.getAllTasksWithCategory();
    }

    // Get task by ID
    public LiveData<Task> getTaskById(long taskId) {
        return taskDao.getTaskById(taskId);
    }

    // Get tasks by category
    public LiveData<List<Task>> getTasksByCategory(long categoryId) {
        return taskDao.getTasksByCategory(categoryId);
    }

    // Get pending tasks
    public LiveData<List<Task>> getPendingTasks() {
        return taskDao.getPendingTasks();
    }

    // Get completed tasks
    public LiveData<List<Task>> getCompletedTasks() {
        return taskDao.getCompletedTasks();
    }

    // Get tasks by type
    public LiveData<List<Task>> getTasksByType(String type) {
        return taskDao.getTasksByType(type);
    }

    // Insert a task
    public void insert(Task task) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            long id = taskDao.insert(task);
            task.setId(id);
            taskObserver.notifyTaskAdded(task);
        });
    }

    // Update a task
    public void update(Task task) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            taskDao.update(task);
            taskObserver.notifyTaskUpdated(task);
        });
    }

    // Delete a task
    public void delete(Task task) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            taskDao.delete(task);
            taskObserver.notifyTaskDeleted(task);
        });
    }

    // Complete a task
    public void completeTask(Task task) {
        task.setCompleted(true);
        AppDatabase.databaseWriteExecutor.execute(() -> {
            taskDao.update(task);
            taskObserver.notifyTaskCompleted(task);
        });
    }

    // Get the task observer
    public TaskObserver getTaskObserver() {
        return taskObserver;
    }
}