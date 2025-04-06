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


public class TaskObserver implements LifecycleObserver {

    private static final String TAG = "TaskObserver";

    public interface TaskChangeListener {
        void onTaskAdded(Task task);
        void onTaskUpdated(Task task);
        void onTaskDeleted(Task task);
        void onTaskCompleted(Task task);
    }

    private final List<TaskChangeListener> listeners = new ArrayList<>();
    private final List<Task> tasks = new ArrayList<>();

    public void addListener(TaskChangeListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeListener(TaskChangeListener listener) {
        listeners.remove(listener);
    }

    public void notifyTaskAdded(Task task) {
        tasks.add(task);
        for (TaskChangeListener listener : listeners) {
            listener.onTaskAdded(task);
        }
        Log.d(TAG, "Tarefa Adicionada: " + task.getTitle());
    }

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
        Log.d(TAG, "Tarefa Atualizada: " + task.getTitle());
    }

    public void notifyTaskDeleted(Task task) {
        tasks.removeIf(t -> t.getId() == task.getId());
        for (TaskChangeListener listener : listeners) {
            listener.onTaskDeleted(task);
        }
        Log.d(TAG, "Tarefa Deletada: " + task.getTitle());
    }

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
        Log.d(TAG, "Tarefa Completa: " + task.getTitle());
    }

    public int getUpcomingTasksCount() {
        Date now = new Date();
        int count = 0;

        for (Task task : tasks) {
            if (!task.isCompleted() && task.getDueDate() != null) {
                long diffInMillis = task.getDueDate().getTime() - now.getTime();
                long diffInHours = diffInMillis / (60 * 60 * 1000);

                if (diffInHours >= 0 && diffInHours <= 24) {
                    count++;
                }
            }
        }

        return count;
    }

    public Observer<List<Task>> createTaskListObserver() {
        return taskList -> {
            tasks.clear();
            if (taskList != null) {
                tasks.addAll(taskList);
            }
        };
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStart(LifecycleOwner owner) {
        Log.d(TAG, "TaskObserver started observing");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onStop(LifecycleOwner owner) {
        Log.d(TAG, "TaskObserver stopped observing");
    }
}