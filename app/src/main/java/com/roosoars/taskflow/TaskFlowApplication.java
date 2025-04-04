package com.roosoars.taskflow;

import android.app.Application;

import com.roosoars.taskflow.di.DaggerAppComponent;

/**
 * Custom Application class for initializing Dagger
 */
public class TaskFlowApplication extends Application {

    private DaggerAppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        appComponent = (DaggerAppComponent) DaggerAppComponent.builder()
                .application(this)
                .build();

        appComponent.inject(this);
    }

    public DaggerAppComponent getAppComponent() {
        return appComponent;
    }
}