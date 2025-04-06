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
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import java.util.List;

import javax.inject.Inject;


public class TaskListFragment extends Fragment implements TaskAdapter.OnTaskClickListener {

    @Inject
    ViewModelFactory viewModelFactory;

    private TaskViewModel taskViewModel;
    private TaskAdapter adapter;
    private Spinner spinnerSort;
    private RecyclerView recyclerView;
    private View emptyView;
    private ChipGroup filterChipGroup;
    private ExtendedFloatingActionButton fabAddTask;

    private ActionMode actionMode;
    private boolean isMultiSelectActive = false;

    private final ActionMode.Callback actionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.menu_multi_select, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            if (item.getItemId() == R.id.action_delete_selected) {
                deleteSelectedTasks();
                return true;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            actionMode = null;
            taskViewModel.clearSelectedTasks();
            isMultiSelectActive = false;
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((TaskFlowApplication) requireActivity().getApplication())
                .getAppComponent().inject(this);

        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task_list, container, false);

        taskViewModel = new ViewModelProvider(requireActivity(), viewModelFactory)
                .get(TaskViewModel.class);

        recyclerView = view.findViewById(R.id.recycler_view_tasks);
        emptyView = view.findViewById(R.id.empty_view);
        spinnerSort = view.findViewById(R.id.spinner_sort);
        filterChipGroup = view.findViewById(R.id.filter_chip_group);
        fabAddTask = view.findViewById(R.id.fab_add_task);

        setupRecyclerView();

        setupFilterChips();

        setupSortSpinner();

        setupFab();

        setupMultiSelectObservation();

        return view;
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

        adapter = new TaskAdapter(requireContext(), this);
        recyclerView.setAdapter(adapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(
                new SwipeToActionHelper(requireContext(), adapter));
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void setupFilterChips() {
        Chip chipAll = filterChipGroup.findViewById(R.id.chip_all);
        Chip chipPending = filterChipGroup.findViewById(R.id.chip_pending);
        Chip chipCompleted = filterChipGroup.findViewById(R.id.chip_completed);

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

            int selectedChipId = filterChipGroup.getCheckedChipId();
            if (selectedChipId == R.id.chip_all) {
                observeAllTasks();
            } else if (selectedChipId == R.id.chip_pending) {
                observePendingTasks();
            } else if (selectedChipId == R.id.chip_completed) {
                observeCompletedTasks();
            }
        });

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
            }
        });
    }

    private void setupFab() {
        fabAddTask.setOnClickListener(v -> {
            Navigation.findNavController(requireView())
                    .navigate(R.id.action_taskListFragment_to_addTaskFragment);
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && fabAddTask.isExtended()) {
                    fabAddTask.shrink();
                } else if (dy < 0 && !fabAddTask.isExtended()) {
                    fabAddTask.extend();
                }
            }
        });
    }

    private void setupMultiSelectObservation() {
        taskViewModel.isInMultiSelectMode().observe(getViewLifecycleOwner(), isMultiSelectMode -> {
            if (isMultiSelectMode && actionMode == null) {
                actionMode = ((AppCompatActivity) requireActivity()).startSupportActionMode(actionModeCallback);
            } else if (!isMultiSelectMode && actionMode != null) {
                actionMode.finish();
            }

            if (actionMode != null) {
                List<Task> selectedTasks = taskViewModel.getSelectedTasks().getValue();
                int count = selectedTasks != null ? selectedTasks.size() : 0;
                actionMode.setTitle(count + " selected");
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

    private void deleteSelectedTasks() {
        taskViewModel.deleteSelectedTasks();
        Snackbar.make(requireView(), "Tarefa Deletada", Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_filter_completed) {
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
        Task task = taskWithCategory.getTask();

        if (taskViewModel.isInMultiSelectMode().getValue() != null
                && taskViewModel.isInMultiSelectMode().getValue()) {
            taskViewModel.toggleTaskSelection(task);
        } else {
            Bundle args = new Bundle();
            args.putLong("taskId", task.getId());
            Navigation.findNavController(requireView())
                    .navigate(R.id.action_taskListFragment_to_addTaskFragment, args);
        }
    }

    @Override
    public void onTaskLongClick(TaskWithCategory taskWithCategory) {
        Task task = taskWithCategory.getTask();

        if (taskViewModel.isInMultiSelectMode().getValue() == null
                || !taskViewModel.isInMultiSelectMode().getValue()) {
            taskViewModel.toggleTaskSelection(task);
        }
    }

    @Override
    public void onTaskCheckedChange(Task task, boolean isChecked) {
        task.setCompleted(isChecked);
        taskViewModel.update(task);
    }

    @Override
    public void onTaskSwiped(Task task, int direction) {
        if (direction == SwipeToActionHelper.SWIPE_DIRECTION_LEFT) {
            taskViewModel.delete(task);
            Snackbar.make(requireView(), "Tarefa Deletada", Snackbar.LENGTH_SHORT).show();
        } else if (direction == SwipeToActionHelper.SWIPE_DIRECTION_RIGHT) {
            taskViewModel.toggleTaskCompletion(task);
        }
    }

    @Override
    public boolean isTaskSelected(Task task) {
        return taskViewModel.isTaskSelected(task);
    }
}