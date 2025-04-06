package com.roosoars.taskflow.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.roosoars.taskflow.model.Task;
import com.roosoars.taskflow.model.TaskWithCategory;

import java.util.List;


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

    @Query("DELETE FROM tasks WHERE id IN (:taskIds)")
    void deleteTasks(List<Long> taskIds);

    @Query("UPDATE tasks SET categoryId = NULL WHERE categoryId = :categoryId")
    void clearCategoryForTasks(long categoryId);

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

    @Query("SELECT * FROM tasks WHERE categoryId = :categoryId")
    List<Task> getTasksByCategorySync(long categoryId);

    @Transaction
    @Query("SELECT * FROM tasks ORDER BY completed ASC, dueDate ASC")
    LiveData<List<TaskWithCategory>> getAllTasksWithCategoryByDate();

    @Transaction
    @Query("SELECT * FROM tasks ORDER BY completed ASC, priority ASC")
    LiveData<List<TaskWithCategory>> getAllTasksWithCategoryByPriority();

    @Transaction
    @Query("SELECT * FROM tasks ORDER BY completed ASC, categoryId ASC")
    LiveData<List<TaskWithCategory>> getAllTasksWithCategoryByCategory();

    @Query("SELECT * FROM tasks WHERE completed = 0 ORDER BY dueDate ASC")
    LiveData<List<Task>> getPendingTasksByDate();

    @Query("SELECT * FROM tasks WHERE completed = 0 ORDER BY priority ASC")
    LiveData<List<Task>> getPendingTasksByPriority();

    @Query("SELECT * FROM tasks WHERE completed = 0 ORDER BY categoryId ASC")
    LiveData<List<Task>> getPendingTasksByCategory();

    @Transaction
    @Query("SELECT * FROM tasks WHERE completed = 0 ORDER BY dueDate ASC")
    LiveData<List<TaskWithCategory>> getPendingTasksWithCategoryByDate();

    @Transaction
    @Query("SELECT * FROM tasks WHERE completed = 0 ORDER BY priority ASC")
    LiveData<List<TaskWithCategory>> getPendingTasksWithCategoryByPriority();

    @Transaction
    @Query("SELECT * FROM tasks WHERE completed = 0 ORDER BY categoryId ASC")
    LiveData<List<TaskWithCategory>> getPendingTasksWithCategoryByCategory();

    @Query("SELECT * FROM tasks WHERE completed = 1 ORDER BY dueDate DESC")
    LiveData<List<Task>> getCompletedTasksByDate();

    @Query("SELECT * FROM tasks WHERE completed = 1 ORDER BY priority ASC")
    LiveData<List<Task>> getCompletedTasksByPriority();

    @Query("SELECT * FROM tasks WHERE completed = 1 ORDER BY categoryId ASC")
    LiveData<List<Task>> getCompletedTasksByCategory();

    @Transaction
    @Query("SELECT * FROM tasks WHERE completed = 1 ORDER BY dueDate DESC")
    LiveData<List<TaskWithCategory>> getCompletedTasksWithCategoryByDate();

    @Transaction
    @Query("SELECT * FROM tasks WHERE completed = 1 ORDER BY priority ASC")
    LiveData<List<TaskWithCategory>> getCompletedTasksWithCategoryByPriority();

    @Transaction
    @Query("SELECT * FROM tasks WHERE completed = 1 ORDER BY categoryId ASC")
    LiveData<List<TaskWithCategory>> getCompletedTasksWithCategoryByCategory();

    @Query("SELECT * FROM tasks WHERE type = :type ORDER BY dueDate ASC")
    LiveData<List<Task>> getTasksByType(String type);
}