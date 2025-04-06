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


@Database(entities = {Task.class, Category.class}, version = 1, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {

    public abstract TaskDao taskDao();
    public abstract CategoryDao categoryDao();

    private static volatile AppDatabase INSTANCE;

    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

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

    private static final RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            databaseWriteExecutor.execute(() -> {
                CategoryDao categoryDao = INSTANCE.categoryDao();

                Category workCategory = new Category("Work", R.color.colorPrimary);
                Category personalCategory = new Category("Personal", R.color.labelPurple);
                Category healthCategory = new Category("Health", R.color.labelGreen);
                Category financeCategory = new Category("Finance", R.color.labelYellow);
                Category educationCategory = new Category("Education", R.color.labelOrange);

                categoryDao.insert(workCategory);
                categoryDao.insert(personalCategory);
                categoryDao.insert(healthCategory);
                categoryDao.insert(financeCategory);
                categoryDao.insert(educationCategory);
            });
        }
    };
}