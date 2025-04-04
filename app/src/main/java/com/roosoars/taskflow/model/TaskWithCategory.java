package com.roosoars.taskflow.model;

import androidx.room.Embedded;
import androidx.room.Relation;

/**
 * Entity relationship class to retrieve a Task with its Category
 * Demonstrates Single Responsibility by focusing only on the relationship
 */
public class TaskWithCategory {
    @Embedded
    private Task task;

    @Relation(
            parentColumn = "categoryId",
            entityColumn = "id"
    )
    private Category category;

    public TaskWithCategory(Task task, Category category) {
        this.task = task;
        this.category = category;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}