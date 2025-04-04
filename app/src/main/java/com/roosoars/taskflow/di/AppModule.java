package com.roosoars.taskflow.di;

import android.app.Application;

import androidx.room.Room;

import com.roosoars.taskflow.db.AppDatabase;
import com.roosoars.taskflow.db.CategoryDao;
import com.roosoars.taskflow.db.TaskDao;
import com.roosoars.taskflow.observer.TaskObserver;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Dagger module for providing application-wide dependencies
 * Implements Dependency Injection pattern
 */
@Module
public class AppModule {

    @Provides
    @Singleton
    AppDatabase provideAppDatabase(Application application) {
        return AppDatabase.getInstance(application);
    }

    @Provides
    @Singleton
    TaskDao provideTaskDao(AppDatabase database) {
        return database.taskDao();
    }

    @Provides
    @Singleton
    CategoryDao provideCategoryDao(AppDatabase database) {
        return database.categoryDao();
    }

    @Provides
    @Singleton
    TaskObserver provideTaskObserver() {
        return new TaskObserver();
    }
}