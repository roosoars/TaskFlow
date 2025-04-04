package com.roosoars.taskflow.strategy;

import androidx.lifecycle.LiveData;

import com.roosoars.taskflow.model.Task;

import java.util.List;

/**
 * Strategy pattern interface for task sorting strategies
 */
public interface SortStrategy {
    /**
     * Return tasks sorted according to the specific strategy
     * @return LiveData containing the sorted list of tasks
     */
    LiveData<List<Task>> getSortedTasks();

    /**
     * Gets the strategy name for display purposes
     * @return Strategy name
     */
    String getStrategyName();
}