package com.roosoars.taskflow.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.roosoars.taskflow.model.Category;

import java.util.List;


@Dao
public interface CategoryDao {
    @Insert
    long insert(Category category);

    @Update
    void update(Category category);

    @Delete
    void delete(Category category);

    @Query("SELECT * FROM categories WHERE id = :id")
    LiveData<Category> getCategoryById(long id);

    @Query("SELECT * FROM categories ORDER BY name ASC")
    LiveData<List<Category>> getAllCategories();

    @Query("SELECT COUNT(*) FROM tasks WHERE categoryId = :categoryId")
    int getTaskCountForCategory(long categoryId);
}