package com.roosoars.taskflow.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.taskflow.model.Task;
import com.example.taskflow.model.TaskWithCategory;

import java.util.List;

/**
 * Data Access Object for Task entities
 * Follows Interface Segregation Principle by providing specific methods for Task operations
 */
@Dao
public interface TaskDao {
    @Insert
    long insert(Task task);

    @Update
    void update(Task task);

    @Delete
    void delete(Task task);

    @Query("DELETE FROM tasks")
    void deleteAllTasks();

    @Query("SELECT * FROM tasks WHERE id = :id")
    LiveData<Task> getTaskById(long id);

    @Query("SELECT * FROM tasks ORDER BY dueDate ASC")
    LiveData<List<Task>> getAllTasksByDate();

    @Query("SELECT * FROM tasks ORDER BY priority ASC")
    LiveData<List<Task>> getAllTasksByPriority();

    @Query("SELECT * FROM tasks ORDER BY categoryId ASC")
    LiveData<List<Task>> getAllTasksByCategory();

    @Query("SELECT * FROM tasks WHERE categoryId = :categoryId ORDER BY dueDate ASC")
    LiveData<List<Task>> getTasksByCategory(long categoryId);

    @Transaction
    @Query("SELECT * FROM tasks ORDER BY dueDate ASC")
    LiveData<List<TaskWithCategory>> getAllTasksWithCategory();

    @Query("SELECT * FROM tasks WHERE completed = 0 ORDER BY dueDate ASC")
    LiveData<List<Task>> getPendingTasks();

    @Query("SELECT * FROM tasks WHERE completed = 1 ORDER BY dueDate DESC")
    LiveData<List<Task>> getCompletedTasks();

    @Query("SELECT * FROM tasks WHERE type = :type ORDER BY dueDate ASC")
    LiveData<List<Task>> getTasksByType(String type);
}