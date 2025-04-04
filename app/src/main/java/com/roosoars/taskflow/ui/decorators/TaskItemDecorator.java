package com.roosoars.taskflow.ui.decorators;

import android.content.Context;
import android.graphics.Color;

import androidx.core.content.ContextCompat;

import com.roosoars.taskflow.R;
import com.roosoars.taskflow.model.Task;

import java.util.Date;

/**
 * Decorator for Task items to enhance visual appearance
 * Implements the Decorator Pattern
 */
public class TaskItemDecorator {

    private final Task task;

    public TaskItemDecorator(Task task) {
        this.task = task;
    }

    /**
     * Determine if the task is overdue
     * @return true if the task is overdue and not completed
     */
    public boolean isOverdue() {
        if (task.isCompleted() || task.getDueDate() == null) {
            return false;
        }

        return task.getDueDate().before(new Date());
    }

    /**
     * Determine if the task is due soon (within 24 hours)
     * @return true if the task is due soon and not completed
     */
    public boolean isDueSoon() {
        if (task.isCompleted() || task.getDueDate() == null) {
            return false;
        }

        long currentTime = new Date().getTime();
        long dueTime = task.getDueDate().getTime();
        long timeDiff = dueTime - currentTime;

        // Within 24 hours but not overdue
        return timeDiff > 0 && timeDiff <= 24 * 60 * 60 * 1000;
    }

    /**
     * Get the background color for the task card based on task state
     * @param context the context to get resources
     * @return color resource for the card background
     */
    public int getCardBackgroundColor(Context context) {
        // If task is completed, use a muted background
        if (task.isCompleted()) {
            return ContextCompat.getColor(context, R.color.lightGray);
        }

        // If task is overdue, use a light red tint
        if (isOverdue()) {
            return Color.argb(20, 255, 0, 0);
        }

        // If task is due soon, use a light yellow tint
        if (isDueSoon()) {
            return Color.argb(20, 255, 255, 0);
        }

        // For project tasks, use a special background
        if ("project".equals(task.getType())) {
            return Color.argb(20, 0, 0, 255);
        }

        // Default white background
        return ContextCompat.getColor(context, R.color.white);
    }

    /**
     * Get emoji indicator based on task state
     * @return emoji string to prepend to title
     */
    public String getTaskIndicator() {
        if (task.isCompleted()) {
            return "✅ ";
        }

        if (isOverdue()) {
            return "⚠️ ";
        }

        if (isDueSoon()) {
            return "⏰ ";
        }

        return "";
    }
}