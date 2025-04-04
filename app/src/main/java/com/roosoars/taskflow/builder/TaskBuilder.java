package com.roosoars.taskflow.builder;

import com.roosoars.taskflow.model.Priority;
import com.roosoars.taskflow.model.Task;

import java.util.Date;

/**
 * Builder pattern implementation for constructing Task objects
 * Allows for step-by-step construction of complex Task objects
 */
public class TaskBuilder {
    private String title;
    private String description;
    private Date dueDate;
    private Priority priority = Priority.MEDIUM; // Default priority
    private Long categoryId;
    private boolean completed = false;
    private String type = "regular"; // Default type

    /**
     * Initializes builder with required task title
     * @param title Task title
     * @return Builder instance
     */
    public static TaskBuilder aTask(String title) {
        TaskBuilder builder = new TaskBuilder();
        builder.title = title;
        return builder;
    }

    /**
     * Sets the task description
     * @param description Task description
     * @return Builder instance
     */
    public TaskBuilder withDescription(String description) {
        this.description = description;
        return this;
    }

    /**
     * Sets the task due date
     * @param dueDate Task due date
     * @return Builder instance
     */
    public TaskBuilder withDueDate(Date dueDate) {
        this.dueDate = dueDate;
        return this;
    }

    /**
     * Sets the task priority
     * @param priority Task priority
     * @return Builder instance
     */
    public TaskBuilder withPriority(Priority priority) {
        this.priority = priority;
        return this;
    }

    /**
     * Sets the task category ID
     * @param categoryId Task category ID
     * @return Builder instance
     */
    public TaskBuilder withCategory(Long categoryId) {
        this.categoryId = categoryId;
        return this;
    }

    /**
     * Sets the task completion status
     * @param completed Task completion status
     * @return Builder instance
     */
    public TaskBuilder isCompleted(boolean completed) {
        this.completed = completed;
        return this;
    }

    /**
     * Sets the task type
     * @param type Task type
     * @return Builder instance
     */
    public TaskBuilder ofType(String type) {
        this.type = type;
        return this;
    }

    /**
     * Builds the Task object
     * @return Constructed Task object
     */
    public Task build() {
        Task task = new Task();
        task.setTitle(title);
        task.setDescription(description);
        task.setDueDate(dueDate);
        task.setPriorityEnum(priority);
        task.setCategoryId(categoryId);
        task.setCompleted(completed);
        task.setType(type);

        return task;
    }
}