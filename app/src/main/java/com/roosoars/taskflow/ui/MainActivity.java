package com.roosoars.taskflow.ui;

import android.os.Bundle;
import android.view.MenuItem;

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

/**
 * Main activity hosting the fragments
 * Follow Single Responsibility Principle by focusing only on navigation
 */
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
        // Inject dependencies
        ((TaskFlowApplication) getApplication()).getAppComponent().inject(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set up navigation
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        // Configure app bar with navigation
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.taskListFragment, R.id.categoryFragment
        ).build();

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        // Initialize ViewModel
        taskViewModel = new ViewModelProvider(this, viewModelFactory).get(TaskViewModel.class);

        // Set up badge for pending tasks
        taskBadge = bottomNavigationView.getOrCreateBadge(R.id.taskListFragment);
        taskBadge.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        taskBadge.setVisible(false);

        // Observe pending tasks count
        taskViewModel.getOverdueTasksCount().observe(this, count -> {
            if (count > 0) {
                taskBadge.setNumber(count);
                taskBadge.setVisible(true);
            } else {
                taskBadge.setVisible(false);
            }
        });

        // Add lifecycle awareness to task observer
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