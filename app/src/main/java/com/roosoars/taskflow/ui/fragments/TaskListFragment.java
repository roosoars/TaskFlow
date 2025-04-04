package com.roosoars.taskflow.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.roosoars.taskflow.R;
import com.roosoars.taskflow.TaskFlowApplication;
import com.roosoars.taskflow.di.ViewModelFactory;
import com.roosoars.taskflow.model.Task;
import com.roosoars.taskflow.model.TaskWithCategory;
import com.roosoars.taskflow.ui.adapters.TaskAdapter;
import com.roosoars.taskflow.ui.helpers.SwipeToActionHelper;
import com.roosoars.taskflow.viewmodel.TaskViewModel;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import javax.inject.Inject;

/**
 * Fragment for displaying the list of tasks with enhanced UI and animations
 * Follows MVC architecture as the "View" component
 */
public class TaskListFragment extends Fragment implements TaskAdapter.OnTaskClickListener {

    @Inject
    ViewModelFactory viewModelFactory;

    private TaskViewModel taskViewModel;
    private TaskAdapter adapter;
    private Spinner spinnerSort;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private View emptyView;
    private ChipGroup filterChipGroup;
    private ExtendedFloatingActionButton fabAddTask;

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

        // Initialize UI components
        recyclerView = view.findViewById(R.id.recycler_view_tasks);
        emptyView = view.findViewById(R.id.empty_view);
        spinnerSort = view.findViewById(R.id.spinner_sort);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        filterChipGroup = view.findViewById(R.id.filter_chip_group);
        fabAddTask = view.findViewById(R.id.fab_add_task);

        // Set up RecyclerView
        setupRecyclerView();

        // Set up SwipeRefreshLayout
        setupSwipeRefresh();

        // Set up filter chips
        setupFilterChips();

        // Set up sort spinner
        setupSortSpinner();

        // Observe tasks with categories
        observeAllTasks();

        // Set up FAB
        setupFab();

        return view;
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

        // Create adapter
        adapter = new TaskAdapter(requireContext(), this);
        recyclerView.setAdapter(adapter);

        // Add animation to the RecyclerView
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(
                requireContext(), R.anim.layout_fall_down);
        recyclerView.setLayoutAnimation(animation);

        // Set up swipe actions
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(
                new SwipeToActionHelper(requireContext(), adapter));
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void setupSwipeRefresh() {
        swipeRefreshLayout.setColorSchemeResources(
                R.color.colorPrimary,
                R.color.colorAccent,
                R.color.labelGreen
        );

        swipeRefreshLayout.setOnRefreshListener(() -> {
            // Simulate refresh
            recyclerView.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(
                    requireContext(), R.anim.layout_fall_down));
            recyclerView.scheduleLayoutAnimation();

            // Hide refresh indicator after a delay
            swipeRefreshLayout.postDelayed(
                    () -> swipeRefreshLayout.setRefreshing(false),
                    1000);
        });
    }

    private void setupFilterChips() {
        // Get references to individual chips
        Chip chipAll = filterChipGroup.findViewById(R.id.chip_all);
        Chip chipPending = filterChipGroup.findViewById(R.id.chip_pending);
        Chip chipCompleted = filterChipGroup.findViewById(R.id.chip_completed);

        // Set chip click listeners
        filterChipGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.chip_all) {
                observeAllTasks();
            } else if (checkedId == R.id.chip_pending) {
                observePendingTasks();
            } else if (checkedId == R.id.chip_completed) {
                observeCompletedTasks();
            }
        });
    }

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

    private void setupFab() {
        fabAddTask.setOnClickListener(v -> {
            // Animate the FAB
            fabAddTask.animate()
                    .scaleX(0.8f)
                    .scaleY(0.8f)
                    .setDuration(100)
                    .withEndAction(() -> {
                        fabAddTask.animate()
                                .scaleX(1f)
                                .scaleY(1f)
                                .setDuration(100)
                                .start();

                        // Navigate to add task screen
                        Navigation.findNavController(requireView())
                                .navigate(R.id.action_taskListFragment_to_addTaskFragment);
                    })
                    .start();
        });

        // Show/hide FAB based on scroll
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && fabAddTask.isExtended()) {
                    // Scrolling down, shrink FAB
                    fabAddTask.shrink();
                } else if (dy < 0 && !fabAddTask.isExtended()) {
                    // Scrolling up, extend FAB
                    fabAddTask.extend();
                }
            }
        });
    }

    private void observeAllTasks() {
        taskViewModel.getAllTasksWithCategory().observe(getViewLifecycleOwner(), taskWithCategories -> {
            adapter.submitList(taskWithCategories);
            updateEmptyViewVisibility(taskWithCategories.isEmpty());
        });
    }

    private void observePendingTasks() {
        taskViewModel.getPendingTasksWithCategory().observe(getViewLifecycleOwner(), taskWithCategories -> {
            adapter.submitList(taskWithCategories);
            updateEmptyViewVisibility(taskWithCategories.isEmpty());
        });
    }

    private void observeCompletedTasks() {
        taskViewModel.getCompletedTasksWithCategory().observe(getViewLifecycleOwner(), taskWithCategories -> {
            adapter.submitList(taskWithCategories);
            updateEmptyViewVisibility(taskWithCategories.isEmpty());
        });
    }

    private void updateEmptyViewVisibility(boolean isEmpty) {
        if (isEmpty) {
            emptyView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_filter_completed) {
            // Toggle between showing all tasks and only pending tasks
            if (item.isChecked()) {
                item.setChecked(false);
                observeAllTasks();
            } else {
                item.setChecked(true);
                observePendingTasks();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTaskClick(TaskWithCategory taskWithCategory) {
        // Animate the clicked item
        View view = recyclerView.findViewHolderForAdapterPosition(
                adapter.getCurrentList().indexOf(taskWithCategory)).itemView;

        view.animate()
                .scaleX(0.95f)
                .scaleY(0.95f)
                .setDuration(100)
                .withEndAction(() -> {
                    view.animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .setDuration(100)
                            .start();

                    // Navigate to edit task screen with the task ID
                    Bundle args = new Bundle();
                    args.putLong("taskId", taskWithCategory.getTask().getId());
                    Navigation.findNavController(requireView())
                            .navigate(R.id.action_taskListFragment_to_addTaskFragment, args);
                })
                .start();
    }

    @Override
    public void onTaskCheckedChange(Task task, boolean isChecked) {
        task.setCompleted(isChecked);
        if (isChecked) {
            taskViewModel.completeTask(task);

            // Show snackbar with undo option
            Snackbar snackbar = Snackbar.make(
                    requireView(),
                    "Task completed!",
                    Snackbar.LENGTH_LONG);

            snackbar.setAction("UNDO", v -> {
                task.setCompleted(false);
                taskViewModel.update(task);
            });

            snackbar.show();
        } else {
            taskViewModel.update(task);
        }
    }

    @Override
    public void onTaskSwiped(Task task, int direction) {
        if (direction == SwipeToActionHelper.SWIPE_DIRECTION_LEFT) {
            // Swipe left to delete
            Task deletedTask = new Task();
            deletedTask.setId(task.getId());
            deletedTask.setTitle(task.getTitle());
            deletedTask.setDescription(task.getDescription());
            deletedTask.setDueDate(task.getDueDate());
            deletedTask.setPriority(task.getPriority());
            deletedTask.setCategoryId(task.getCategoryId());
            deletedTask.setCompleted(task.isCompleted());
            deletedTask.setType(task.getType());

            taskViewModel.delete(task);

            // Show snackbar with undo option
            Snackbar snackbar = Snackbar.make(
                    requireView(),
                    "Task deleted!",
                    Snackbar.LENGTH_LONG);

            snackbar.setAction("UNDO", v -> {
                taskViewModel.insert(deletedTask);
            });

            snackbar.show();
        } else if (direction == SwipeToActionHelper.SWIPE_DIRECTION_RIGHT) {
            // Swipe right to complete/uncomplete
            boolean newStatus = !task.isCompleted();
            task.setCompleted(newStatus);

            if (newStatus) {
                taskViewModel.completeTask(task);
                Snackbar.make(requireView(), "Task completed!", Snackbar.LENGTH_SHORT).show();
            } else {
                taskViewModel.update(task);
                Snackbar.make(requireView(), "Task marked as pending!", Snackbar.LENGTH_SHORT).show();
            }
        }
    }
}