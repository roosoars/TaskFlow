package com.roosoars.taskflow.observer;

import android.util.Log;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.OnLifecycleEvent;

import com.roosoars.taskflow.model.Task;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Observer pattern implementation for task changes
 * Implements the Observer Pattern
 */
public class TaskObserver implements LifecycleObserver {

    private static final String TAG = "TaskObserver";

    // Interface for task change callbacks
    public interface TaskChangeListener {
        void onTaskAdded(Task task);
        void onTaskUpdated(Task task);
        void onTaskDeleted(Task task);
        void onTaskCompleted(Task task);
    }

    private final List<TaskChangeListener> listeners = new ArrayList<>();
    private final List<Task> tasks = new ArrayList<>();

    // Add a listener
    public void addListener(TaskChangeListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    // Remove a listener
    public void removeListener(TaskChangeListener listener) {
        listeners.remove(listener);
    }

    // Notify about task addition
    public void notifyTaskAdded(Task task) {
        tasks.add(task);
        for (TaskChangeListener listener : listeners) {
            listener.onTaskAdded(task);
        }
        Log.d(TAG, "Task added: " + task.getTitle());
    }

    // Notify about task update
    public void notifyTaskUpdated(Task task) {
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getId() == task.getId()) {
                tasks.set(i, task);
                break;
            }
        }
        for (TaskChangeListener listener : listeners) {
            listener.onTaskUpdated(task);
        }
        Log.d(TAG, "Task updated: " + task.getTitle());
    }

    // Notify about task deletion
    public void notifyTaskDeleted(Task task) {
        tasks.removeIf(t -> t.getId() == task.getId());
        for (TaskChangeListener listener : listeners) {
            listener.onTaskDeleted(task);
        }
        Log.d(TAG, "Task deleted: " + task.getTitle());
    }

    // Notify about task completion
    public void notifyTaskCompleted(Task task) {
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getId() == task.getId()) {
                tasks.get(i).setCompleted(true);
                break;
            }
        }
        for (TaskChangeListener listener : listeners) {
            listener.onTaskCompleted(task);
        }
        Log.d(TAG, "Task completed: " + task.getTitle());
    }

    // Count of upcoming tasks due soon
    public int getUpcomingTasksCount() {
        Date now = new Date();
        int count = 0;

        for (Task task : tasks) {
            if (!task.isCompleted() && task.getDueDate() != null) {
                // Check if due within next 24 hours
                long diffInMillis = task.getDueDate().getTime() - now.getTime();
                long diffInHours = diffInMillis / (60 * 60 * 1000);

                if (diffInHours >= 0 && diffInHours <= 24) {
                    count++;
                }
            }
        }

        return count;
    }

    // LiveData Observer to update when tasks change
    public Observer<List<Task>> createTaskListObserver() {
        return taskList -> {
            tasks.clear();
            if (taskList != null) {
                tasks.addAll(taskList);
            }
        };
    }

    // Lifecycle hooks to register/unregister
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStart(LifecycleOwner owner) {
        Log.d(TAG, "TaskObserver started observing");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onStop(LifecycleOwner owner) {
        Log.d(TAG, "TaskObserver stopped observing");
    }
}