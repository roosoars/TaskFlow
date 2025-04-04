package com.roosoars.taskflow.db;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.roosoars.taskflow.R;
import com.roosoars.taskflow.model.Category;
import com.roosoars.taskflow.model.Task;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Room database implementation
 * Implements the Singleton pattern to ensure a single database instance
 */
@Database(entities = {Task.class, Category.class}, version = 1, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {

    // DAOs
    public abstract TaskDao taskDao();
    public abstract CategoryDao categoryDao();

    // Singleton instance
    private static volatile AppDatabase INSTANCE;

    // Thread pool for database operations
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    // Get singleton instance
    public static AppDatabase getInstance(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "taskflow_database")
                            .addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    // Callback to populate database with initial data
    private static final RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            databaseWriteExecutor.execute(() -> {
                // Populate initial categories
                CategoryDao categoryDao = INSTANCE.categoryDao();

                Category workCategory = new Category("Work", R.color.pastelBlue);
                Category personalCategory = new Category("Personal", R.color.pastelPurple);
                Category healthCategory = new Category("Health", R.color.pastelGreen);
                Category financeCategory = new Category("Finance", R.color.pastelYellow);
                Category educationCategory = new Category("Education", R.color.pastelOrange);

                categoryDao.insert(workCategory);
                categoryDao.insert(personalCategory);
                categoryDao.insert(healthCategory);
                categoryDao.insert(financeCategory);
                categoryDao.insert(educationCategory);
            });
        }
    };
}