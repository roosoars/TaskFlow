package com.roosoars.taskflow.factory;

import com.roosoars.taskflow.model.Priority;
import com.roosoars.taskflow.model.Task;

import java.util.Date;

/**
 * Factory for creating regular tasks
 * Implements the Factory Method Pattern
 */
public class RegularTaskFactory extends TaskFactory {

    private static final String TASK_TYPE = "regular";

    @Override
    public Task createTask(String title, String description, Date dueDate,
                           Priority priority, Long categoryId) {
        Task task = new Task();
        task.setTitle(title);
        task.setDescription(description);
        task.setDueDate(dueDate);
        task.setPriorityEnum(priority);
        task.setCategoryId(categoryId);
        task.setCompleted(false);
        task.setType(TASK_TYPE);

        return task;
    }
}