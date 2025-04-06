package com.roosoars.taskflow.ui;

import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.roosoars.taskflow.R;
import com.roosoars.taskflow.TaskFlowApplication;
import com.roosoars.taskflow.di.ViewModelFactory;
import com.roosoars.taskflow.observer.TaskObserver;
import com.roosoars.taskflow.viewmodel.TaskViewModel;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import javax.inject.Inject;


public class MainActivity extends AppCompatActivity {

    @Inject
    ViewModelFactory viewModelFactory;

    @Inject
    TaskObserver taskObserver;

    private TaskViewModel taskViewModel;
    private NavController navController;
    private BottomNavigationView bottomNavigationView;
    private BadgeDrawable taskBadge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ((TaskFlowApplication) getApplication()).getAppComponent().inject(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        getWindow().setDecorFitsSystemWindows(false);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.taskListFragment, R.id.categoryFragment
        ).build();

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        taskViewModel = new ViewModelProvider(this, viewModelFactory).get(TaskViewModel.class);

        taskBadge = bottomNavigationView.getOrCreateBadge(R.id.taskListFragment);
        taskBadge.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        taskBadge.setVisible(false);

        taskViewModel.getOverdueTasksCount().observe(this, count -> {
            if (count > 0) {
                taskBadge.setNumber(count);
                taskBadge.setVisible(true);
            } else {
                taskBadge.setVisible(false);
            }
        });

        getLifecycle().addObserver(taskObserver);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return NavigationUI.onNavDestinationSelected(item, navController)
                || super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        return navController.navigateUp() || super.onSupportNavigateUp();
    }
}