package com.roosoars.taskflow.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.roosoars.taskflow.db.AppDatabase;
import com.roosoars.taskflow.db.TaskDao;
import com.roosoars.taskflow.model.Task;
import com.roosoars.taskflow.model.TaskWithCategory;
import com.roosoars.taskflow.observer.TaskObserver;
import com.roosoars.taskflow.strategy.SortStrategy;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;


@Singleton
public class TaskRepository {

    private final TaskDao taskDao;
    private final TaskObserver taskObserver;
    private SortStrategy sortStrategy;
    private static final String SORT_TYPE_DATE = "date";
    private static final String SORT_TYPE_PRIORITY = "priority";
    private static final String SORT_TYPE_CATEGORY = "category";
    private String currentSortType = SORT_TYPE_DATE;

    @Inject
    public TaskRepository(AppDatabase database, TaskObserver taskObserver) {
        this.taskDao = database.taskDao();
        this.taskObserver = taskObserver;
    }

    public void setSortStrategy(SortStrategy sortStrategy) {
        this.sortStrategy = sortStrategy;
    }

    public String getCurrentSortType() {
        return currentSortType;
    }

    public void setCurrentSortType(String sortType) {
        this.currentSortType = sortType;
    }

    public LiveData<List<TaskWithCategory>> getAllTasksWithCategory() {
        switch (currentSortType) {
            case SORT_TYPE_PRIORITY:
                return taskDao.getAllTasksWithCategoryByPriority();
            case SORT_TYPE_CATEGORY:
                return taskDao.getAllTasksWithCategoryByCategory();
            case SORT_TYPE_DATE:
            default:
                return taskDao.getAllTasksWithCategoryByDate();
        }
    }

    public LiveData<Task> getTaskById(long taskId) {
        return taskDao.getTaskById(taskId);
    }

    public LiveData<List<Task>> getTasksByCategory(long categoryId) {
        return taskDao.getTasksByCategory(categoryId);
    }

    public LiveData<List<Task>> getPendingTasks() {
        switch (currentSortType) {
            case SORT_TYPE_PRIORITY:
                return taskDao.getPendingTasksByPriority();
            case SORT_TYPE_CATEGORY:
                return taskDao.getPendingTasksByCategory();
            case SORT_TYPE_DATE:
            default:
                return taskDao.getPendingTasksByDate();
        }
    }

    public LiveData<List<TaskWithCategory>> getPendingTasksWithCategory() {
        switch (currentSortType) {
            case SORT_TYPE_PRIORITY:
                return taskDao.getPendingTasksWithCategoryByPriority();
            case SORT_TYPE_CATEGORY:
                return taskDao.getPendingTasksWithCategoryByCategory();
            case SORT_TYPE_DATE:
            default:
                return taskDao.getPendingTasksWithCategoryByDate();
        }
    }

    public LiveData<List<Task>> getCompletedTasks() {
        switch (currentSortType) {
            case SORT_TYPE_PRIORITY:
                return taskDao.getCompletedTasksByPriority();
            case SORT_TYPE_CATEGORY:
                return taskDao.getCompletedTasksByCategory();
            case SORT_TYPE_DATE:
            default:
                return taskDao.getCompletedTasksByDate();
        }
    }

    public LiveData<List<TaskWithCategory>> getCompletedTasksWithCategory() {
        switch (currentSortType) {
            case SORT_TYPE_PRIORITY:
                return taskDao.getCompletedTasksWithCategoryByPriority();
            case SORT_TYPE_CATEGORY:
                return taskDao.getCompletedTasksWithCategoryByCategory();
            case SORT_TYPE_DATE:
            default:
                return taskDao.getCompletedTasksWithCategoryByDate();
        }
    }

    public LiveData<List<Task>> getTasksByType(String type) {
        return taskDao.getTasksByType(type);
    }

    public void insert(Task task) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            long id = taskDao.insert(task);
            task.setId(id);
            taskObserver.notifyTaskAdded(task);
        });
    }

    public void update(Task task) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            taskDao.update(task);
            taskObserver.notifyTaskUpdated(task);
        });
    }

    public void delete(Task task) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            taskDao.delete(task);
            taskObserver.notifyTaskDeleted(task);
        });
    }

    public void deleteTasks(List<Long> taskIds, List<Task> tasks) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            taskDao.deleteTasks(taskIds);

            for (Task task : tasks) {
                taskObserver.notifyTaskDeleted(task);
            }
        });
    }

    public void completeTask(Task task) {
        task.setCompleted(true);
        AppDatabase.databaseWriteExecutor.execute(() -> {
            taskDao.update(task);
            taskObserver.notifyTaskCompleted(task);
        });
    }

    public void toggleTaskCompletion(Task task) {
        boolean newCompletionState = !task.isCompleted();
        task.setCompleted(newCompletionState);
        AppDatabase.databaseWriteExecutor.execute(() -> {
            taskDao.update(task);
            if (newCompletionState) {
                taskObserver.notifyTaskCompleted(task);
            } else {
                taskObserver.notifyTaskUpdated(task);
            }
        });
    }

    public TaskObserver getTaskObserver() {
        return taskObserver;
    }

    public LiveData<Integer> getOverdueTasksCount() {
        return Transformations.map(getPendingTasks(), tasks -> {
            int count = 0;
            Date now = new Date();

            for (Task task : tasks) {
                if (task.getDueDate() != null && task.getDueDate().before(now)) {
                    count++;
                }
            }

            return count;
        });
    }
}