package com.roosoars.taskflow.di;

import android.app.Application;

import com.roosoars.taskflow.TaskFlowApplication;
import com.roosoars.taskflow.ui.MainActivity;
import com.roosoars.taskflow.ui.fragments.AddTaskFragment;
import com.roosoars.taskflow.ui.fragments.CategoryFragment;
import com.roosoars.taskflow.ui.fragments.TaskListFragment;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;

/**
 * Dagger component for dependency injection in the application
 * Implements Dependency Injection pattern
 */
@Singleton
@Component(modules = {AppModule.class, ViewModelModule.class})
public interface AppComponent {

    void inject(TaskFlowApplication application);

    void inject(MainActivity activity);

    void inject(TaskListFragment fragment);

    void inject(AddTaskFragment fragment);

    void inject(CategoryFragment fragment);

    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder application(Application application);

        AppComponent build();
    }
}