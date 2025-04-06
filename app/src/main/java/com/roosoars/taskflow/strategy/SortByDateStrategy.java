package com.roosoars.taskflow.strategy;

import androidx.lifecycle.LiveData;

import com.roosoars.taskflow.db.TaskDao;
import com.roosoars.taskflow.model.Task;

import java.util.List;

public class SortByDateStrategy implements SortStrategy {

    private final TaskDao taskDao;

    public SortByDateStrategy(TaskDao taskDao) {
        this.taskDao = taskDao;
    }

    @Override
    public LiveData<List<Task>> getSortedTasks() {
        return taskDao.getAllTasksByDate();
    }

    @Override
    public String getStrategyName() {
        return "Date";
    }
}