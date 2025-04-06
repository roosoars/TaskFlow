package com.roosoars.taskflow.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.roosoars.taskflow.builder.TaskBuilder;
import com.roosoars.taskflow.db.TaskDao;
import com.roosoars.taskflow.factory.ProjectTaskFactory;
import com.roosoars.taskflow.factory.RegularTaskFactory;
import com.roosoars.taskflow.factory.TaskFactory;
import com.roosoars.taskflow.model.Priority;
import com.roosoars.taskflow.model.Task;
import com.roosoars.taskflow.model.TaskWithCategory;
import com.roosoars.taskflow.observer.TaskObserver;
import com.roosoars.taskflow.repository.TaskRepository;
import com.roosoars.taskflow.strategy.SortByCategoryStrategy;
import com.roosoars.taskflow.strategy.SortByDateStrategy;
import com.roosoars.taskflow.strategy.SortByPriorityStrategy;
import com.roosoars.taskflow.strategy.SortStrategy;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

public class TaskViewModel extends ViewModel {

    private final TaskRepository taskRepository;
    private final TaskDao taskDao;
    private final MutableLiveData<String> currentSortType = new MutableLiveData<>("date");
    private final MutableLiveData<List<Task>> selectedTasks = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> isMultiSelectMode = new MutableLiveData<>(false);

    private final TaskFactory regularTaskFactory;
    private final TaskFactory projectTaskFactory;

    @Inject
    public TaskViewModel(TaskRepository taskRepository, TaskDao taskDao) {
        this.taskRepository = taskRepository;
        this.taskDao = taskDao;

        this.regularTaskFactory = new RegularTaskFactory();
        this.projectTaskFactory = new ProjectTaskFactory();

        setSortStrategy("date");
    }

    public void setSortStrategy(String strategyType) {
        SortStrategy strategy;

        switch (strategyType) {
            case "priority":
                strategy = new SortByPriorityStrategy(taskDao);
                break;
            case "category":
                strategy = new SortByCategoryStrategy(taskDao);
                break;
            case "date":
            default:
                strategy = new SortByDateStrategy(taskDao);
                strategyType = "date";
                break;
        }

        taskRepository.setSortStrategy(strategy);
        taskRepository.setCurrentSortType(strategyType);
        currentSortType.setValue(strategyType);
    }

    public LiveData<String> getCurrentSortType() {
        return currentSortType;
    }

    public LiveData<List<TaskWithCategory>> getAllTasksWithCategory() {
        return taskRepository.getAllTasksWithCategory();
    }

    public LiveData<List<Task>> getPendingTasks() {
        return taskRepository.getPendingTasks();
    }

    public LiveData<List<TaskWithCategory>> getPendingTasksWithCategory() {
        return taskRepository.getPendingTasksWithCategory();
    }

    public LiveData<List<TaskWithCategory>> getCompletedTasksWithCategory() {
        return taskRepository.getCompletedTasksWithCategory();
    }

    public LiveData<List<Task>> getCompletedTasks() {
        return taskRepository.getCompletedTasks();
    }

    public LiveData<List<Task>> getTasksByCategory(long categoryId) {
        return taskRepository.getTasksByCategory(categoryId);
    }

    public LiveData<Task> getTaskById(long taskId) {
        return taskRepository.getTaskById(taskId);
    }

    public LiveData<List<Task>> getTasksByType(String type) {
        return taskRepository.getTasksByType(type);
    }

    public void insert(String title, String description, Date dueDate,
                       Priority priority, Long categoryId, String taskType) {
        Task task;

        if ("project".equals(taskType)) {
            task = projectTaskFactory.createTask(title, description, dueDate, priority, categoryId);
        } else {
            task = regularTaskFactory.createTask(title, description, dueDate, priority, categoryId);
        }

        taskRepository.insert(task);
    }

    public void insert(Task task) {
        taskRepository.insert(task);
    }

    public void insertWithBuilder(String title, String description, Date dueDate,
                                  Priority priority, Long categoryId, String taskType,
                                  boolean completed) {
        Task task = TaskBuilder.aTask(title)
                .withDescription(description)
                .withDueDate(dueDate)
                .withPriority(priority)
                .withCategory(categoryId)
                .ofType(taskType)
                .isCompleted(completed)
                .build();

        taskRepository.insert(task);
    }

    public void update(Task task) {
        taskRepository.update(task);
    }

    public void delete(Task task) {
        taskRepository.delete(task);
    }

    public void deleteSelectedTasks() {
        List<Task> tasksToDelete = selectedTasks.getValue();
        if (tasksToDelete != null && !tasksToDelete.isEmpty()) {
            List<Long> taskIds = new ArrayList<>();
            for (Task task : tasksToDelete) {
                taskIds.add(task.getId());
            }
            taskRepository.deleteTasks(taskIds, tasksToDelete);
            clearSelectedTasks();
        }
    }

    public void completeTask(Task task) {
        taskRepository.completeTask(task);
    }

    public void toggleTaskCompletion(Task task) {
        taskRepository.toggleTaskCompletion(task);
    }

    public TaskObserver getTaskObserver() {
        return taskRepository.getTaskObserver();
    }

    public LiveData<Integer> getOverdueTasksCount() {
        return taskRepository.getOverdueTasksCount();
    }


    public LiveData<List<Task>> getSelectedTasks() {
        return selectedTasks;
    }

    public boolean isTaskSelected(Task task) {
        List<Task> currentSelectedTasks = selectedTasks.getValue();
        if (currentSelectedTasks != null) {
            for (Task selectedTask : currentSelectedTasks) {
                if (selectedTask.getId() == task.getId()) {
                    return true;
                }
            }
        }
        return false;
    }

    public void toggleTaskSelection(Task task) {
        List<Task> currentSelectedTasks = selectedTasks.getValue();
        if (currentSelectedTasks == null) {
            currentSelectedTasks = new ArrayList<>();
        } else {
            currentSelectedTasks = new ArrayList<>(currentSelectedTasks);
        }

        boolean isRemoved = false;
        for (int i = 0; i < currentSelectedTasks.size(); i++) {
            if (currentSelectedTasks.get(i).getId() == task.getId()) {
                currentSelectedTasks.remove(i);
                isRemoved = true;
                break;
            }
        }

        if (!isRemoved) {
            currentSelectedTasks.add(task);
        }

        boolean inMultiSelectMode = !currentSelectedTasks.isEmpty();
        isMultiSelectMode.setValue(inMultiSelectMode);

        selectedTasks.setValue(currentSelectedTasks);
    }

    public void clearSelectedTasks() {
        selectedTasks.setValue(new ArrayList<>());
        isMultiSelectMode.setValue(false);
    }

    public LiveData<Boolean> isInMultiSelectMode() {
        return isMultiSelectMode;
    }
}