package com.roosoars.taskflow.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.roosoars.taskflow.R;
import com.roosoars.taskflow.TaskFlowApplication;
import com.roosoars.taskflow.di.ViewModelFactory;
import com.roosoars.taskflow.model.Task;
import com.roosoars.taskflow.model.TaskWithCategory;
import com.roosoars.taskflow.ui.adapters.TaskAdapter;
import com.roosoars.taskflow.viewmodel.TaskViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import javax.inject.Inject;

/**
 * Fragment for displaying the list of tasks
 * Follows MVC architecture as the "View" component
 */
public class TaskListFragment extends Fragment implements TaskAdapter.OnTaskClickListener {

    @Inject
    ViewModelFactory viewModelFactory;

    private TaskViewModel taskViewModel;
    private TaskAdapter adapter;
    private Spinner spinnerSort;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inject dependencies
        ((TaskFlowApplication) requireActivity().getApplication())
                .getAppComponent().inject(this);

        // Enable options menu
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task_list, container, false);

        // Initialize ViewModel
        taskViewModel = new ViewModelProvider(requireActivity(), viewModelFactory)
                .get(TaskViewModel.class);

        // Set up RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_tasks);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

        adapter = new TaskAdapter(requireContext(), this);
        recyclerView.setAdapter(adapter);

        // Set up sort spinner
        spinnerSort = view.findViewById(R.id.spinner_sort);
        setupSortSpinner();

        // Observe tasks with categories
        taskViewModel.getAllTasksWithCategory().observe(getViewLifecycleOwner(), taskWithCategories -> {
            adapter.submitList(taskWithCategories);

            // Update empty view visibility
            View emptyView = view.findViewById(R.id.empty_view);
            if (taskWithCategories.isEmpty()) {
                emptyView.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            } else {
                emptyView.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
        });

        // Set up FAB for adding tasks
        FloatingActionButton fab = view.findViewById(R.id.fab_add_task);
        fab.setOnClickListener(v -> {
            Navigation.findNavController(view)
                    .navigate(R.id.action_taskListFragment_to_addTaskFragment);
        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_task_list, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_filter_completed:
                // Toggle between showing all tasks and only pending tasks
                if (item.isChecked()) {
                    item.setChecked(false);
                    observeAllTasks();
                } else {
                    item.setChecked(true);
                    observePendingTasks();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Setup sort spinner with options
    private void setupSortSpinner() {
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.sort_options,
                android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSort.setAdapter(spinnerAdapter);

        // Set initial selection based on ViewModel
        taskViewModel.getCurrentSortType().observe(getViewLifecycleOwner(), sortType -> {
            switch (sortType) {
                case "date":
                    spinnerSort.setSelection(0);
                    break;
                case "priority":
                    spinnerSort.setSelection(1);
                    break;
                case "category":
                    spinnerSort.setSelection(2);
                    break;
            }
        });

        // Listen for sort selection changes
        spinnerSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        taskViewModel.setSortStrategy("date");
                        break;
                    case 1:
                        taskViewModel.setSortStrategy("priority");
                        break;
                    case 2:
                        taskViewModel.setSortStrategy("category");
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    private void observeAllTasks() {
        taskViewModel.getAllTasksWithCategory().observe(getViewLifecycleOwner(), adapter::submitList);
    }

    private void observePendingTasks() {
        // Changing the displayed list to only pending tasks would require additional transformation
        // This is a simplified version that would be implemented in a real app
        taskViewModel.getPendingTasks().observe(getViewLifecycleOwner(), tasks -> {
            // In a real app, we would transform tasks to taskWithCategories here
            Toast.makeText(getContext(), "Showing pending tasks only", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onTaskClick(TaskWithCategory taskWithCategory) {
        // Navigate to edit task screen with the task ID
        Bundle args = new Bundle();
        args.putLong("taskId", taskWithCategory.getTask().getId());
        Navigation.findNavController(requireView())
                .navigate(R.id.action_taskListFragment_to_addTaskFragment, args);
    }

    @Override
    public void onTaskCheckedChange(Task task, boolean isChecked) {
        task.setCompleted(isChecked);
        if (isChecked) {
            taskViewModel.completeTask(task);
            Toast.makeText(getContext(), "Task completed!", Toast.LENGTH_SHORT).show();
        } else {
            taskViewModel.update(task);
        }
    }
}