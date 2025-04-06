package com.roosoars.taskflow.ui.decorators;

import android.content.Context;
import android.graphics.Color;

import androidx.core.content.ContextCompat;

import com.roosoars.taskflow.R;
import com.roosoars.taskflow.model.Task;

import java.util.Date;

public class TaskItemDecorator {

    private final Task task;

    public TaskItemDecorator(Task task) {
        this.task = task;
    }


    public boolean isOverdue() {
        if (task.isCompleted() || task.getDueDate() == null) {
            return false;
        }

        return task.getDueDate().before(new Date());
    }


    public boolean isDueSoon() {
        if (task.isCompleted() || task.getDueDate() == null) {
            return false;
        }

        long currentTime = new Date().getTime();
        long dueTime = task.getDueDate().getTime();
        long timeDiff = dueTime - currentTime;

        return timeDiff > 0 && timeDiff <= 24 * 60 * 60 * 1000;
    }

    public int getCardBackgroundColor(Context context) {
        if (task.isCompleted()) {
            return ContextCompat.getColor(context, R.color.lightGray);
        }

        if (isOverdue()) {
            return Color.argb(20, 255, 0, 0);
        }

        if (isDueSoon()) {
            return Color.argb(20, 255, 255, 0);
        }

        if ("project".equals(task.getType())) {
            return Color.argb(20, 0, 0, 255);
        }

        return ContextCompat.getColor(context, R.color.white);
    }

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