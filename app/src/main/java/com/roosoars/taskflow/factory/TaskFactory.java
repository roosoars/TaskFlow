package com.roosoars.taskflow.factory;

import com.roosoars.taskflow.model.Priority;
import com.roosoars.taskflow.model.Task;

import java.util.Date;


public abstract class TaskFactory {


    public abstract Task createTask(String title, String description, Date dueDate,
                                    Priority priority, Long categoryId);

    public Task createSimpleTask(String title, Date dueDate, Priority priority) {
        return createTask(title, null, dueDate, priority, null);
    }
}