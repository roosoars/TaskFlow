package com.roosoars.taskflow.strategy;

import androidx.lifecycle.LiveData;

import com.roosoars.taskflow.model.Task;

import java.util.List;


public interface SortStrategy {

    LiveData<List<Task>> getSortedTasks();

    String getStrategyName();
}