package com.roosoars.taskflow.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.roosoars.taskflow.db.Converters;

import java.util.Date;

/**
 * Entity class representing a task
 * Follows the Open/Closed Principle by allowing for extension through inheritance
 */
@Entity(tableName = "tasks",
        foreignKeys = @ForeignKey(
                entity = Category.class,
                parentColumns = "id",
                childColumns = "categoryId",
                onDelete = ForeignKey.SET_NULL
        ))
@TypeConverters(Converters.class)
public class Task {
    @PrimaryKey(autoGenerate = true)
    private long id;

    private String title;
    private String description;
    private Date dueDate;
    private int priority; // Stored as integer to simplify Room conversion
    private Long categoryId; // Can be null
    private boolean completed;
    private String type; // For identifying different task types (Regular, Project, etc.)

    // Default constructor
    public Task() {
        this.completed = false;
    }

    // Constructor with required fields
    public Task(String title, Date dueDate, int priority, Long categoryId, String type) {
        this.title = title;
        this.dueDate = dueDate;
        this.priority = priority;
        this.categoryId = categoryId;
        this.completed = false;
        this.type = type;
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public Priority getPriorityEnum() {
        return Priority.fromInt(priority);
    }

    public void setPriorityEnum(Priority priority) {
        this.priority = priority.toInt();
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }
}