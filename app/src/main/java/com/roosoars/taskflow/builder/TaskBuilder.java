package com.roosoars.taskflow.builder;

import com.roosoars.taskflow.model.Priority;
import com.roosoars.taskflow.model.Task;

import java.util.Date;


public class TaskBuilder {
    private String title;
    private String description;
    private Date dueDate;
    private Priority priority = Priority.MEDIUM;
    private Long categoryId;
    private boolean completed = false;
    private String type = "regular";


    public static TaskBuilder aTask(String title) {
        TaskBuilder builder = new TaskBuilder();
        builder.title = title;
        return builder;
    }


    public TaskBuilder withDescription(String description) {
        this.description = description;
        return this;
    }


    public TaskBuilder withDueDate(Date dueDate) {
        this.dueDate = dueDate;
        return this;
    }


    public TaskBuilder withPriority(Priority priority) {
        this.priority = priority;
        return this;
    }


    public TaskBuilder withCategory(Long categoryId) {
        this.categoryId = categoryId;
        return this;
    }


    public TaskBuilder isCompleted(boolean completed) {
        this.completed = completed;
        return this;
    }


    public TaskBuilder ofType(String type) {
        this.type = type;
        return this;
    }


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