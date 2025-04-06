package com.roosoars.taskflow;

import android.app.Application;

import com.roosoars.taskflow.di.AppComponent;
import com.roosoars.taskflow.di.DaggerAppComponent;


public class TaskFlowApplication extends Application {

    private AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        appComponent = DaggerAppComponent.builder()
                .application(this)
                .build();

        appComponent.inject(this);
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }
}