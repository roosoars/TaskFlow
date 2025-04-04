package com.roosoars.taskflow.factory;

import com.roosoars.taskflow.model.Priority;
import com.roosoars.taskflow.model.Task;

import java.util.Date;

/**
 * Abstract Factory for creating different types of tasks
 * Implements the Factory Method Pattern
 */
public abstract class TaskFactory {

    /**
     * Factory method to create a task
     * @param title Task title
     * @param description Task description (optional)
     * @param dueDate Task due date
     * @param priority Task priority
     * @param categoryId Task category ID
     * @return A new Task instance
     */
    public abstract Task createTask(String title, String description, Date dueDate,
                                    Priority priority, Long categoryId);

    /**
     * Creates a basic task with minimal information
     * @param title Task title
     * @param dueDate Task due date
     * @param priority Task priority
     * @return A new Task instance
     */
    public Task createSimpleTask(String title, Date dueDate, Priority priority) {
        return createTask(title, null, dueDate, priority, null);
    }
}