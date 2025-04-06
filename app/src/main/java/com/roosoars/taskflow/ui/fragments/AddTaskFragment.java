package com.roosoars.taskflow.ui.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.roosoars.taskflow.R;
import com.roosoars.taskflow.TaskFlowApplication;
import com.roosoars.taskflow.di.ViewModelFactory;
import com.roosoars.taskflow.model.Category;
import com.roosoars.taskflow.model.Priority;
import com.roosoars.taskflow.model.Task;
import com.roosoars.taskflow.viewmodel.CategoryViewModel;
import com.roosoars.taskflow.viewmodel.TaskViewModel;
import com.google.android.material.button.MaterialButtonToggleGroup;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;


public class AddTaskFragment extends Fragment {

    @Inject
    ViewModelFactory viewModelFactory;

    private TaskViewModel taskViewModel;
    private CategoryViewModel categoryViewModel;

    private EditText editTextTitle;
    private EditText editTextDescription;
    private TextView textViewDueDate;
    private Spinner spinnerPriority;
    private Spinner spinnerCategory;
    private RadioGroup radioGroupTaskType;
    private MaterialButtonToggleGroup toggleGroup;
    private Button buttonSave;

    private List<Category> categories = new ArrayList<>();
    private long taskId = -1;
    private Task currentTask;
    private Date selectedDate;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());

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
        View view = inflater.inflate(R.layout.fragment_add_task, container, false);

        taskViewModel = new ViewModelProvider(requireActivity(), viewModelFactory)
                .get(TaskViewModel.class);

        categoryViewModel = new ViewModelProvider(requireActivity(), viewModelFactory)
                .get(CategoryViewModel.class);

        editTextTitle = view.findViewById(R.id.edit_text_task_title);
        editTextDescription = view.findViewById(R.id.edit_text_task_description);
        textViewDueDate = view.findViewById(R.id.text_view_due_date);
        spinnerPriority = view.findViewById(R.id.spinner_priority);
        spinnerCategory = view.findViewById(R.id.spinner_category);
        radioGroupTaskType = view.findViewById(R.id.radio_group_task_type);
        buttonSave = view.findViewById(R.id.button_save);

        setupDatePicker();

        setupPrioritySpinner();
        setupCategorySpinner();

        if (getArguments() != null) {
            taskId = getArguments().getLong("taskId", -1);

            if (taskId != -1) {
                loadTaskData();
            }
        }

        buttonSave.setOnClickListener(v -> saveTask());

        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        if (taskId != -1) {
            inflater.inflate(R.menu.menu_add_task, menu);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_delete_task) {
            deleteTask();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupDatePicker() {
        selectedDate = new Date();
        textViewDueDate.setText(dateFormat.format(selectedDate));

        textViewDueDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            if (selectedDate != null) {
                calendar.setTime(selectedDate);
            }

            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    requireContext(),
                    (view, selectedYear, selectedMonth, selectedDayOfMonth) -> {
                        Calendar selectedCalendar = Calendar.getInstance();
                        selectedCalendar.set(selectedYear, selectedMonth, selectedDayOfMonth);
                        selectedDate = selectedCalendar.getTime();
                        textViewDueDate.setText(dateFormat.format(selectedDate));
                    },
                    year, month, day);

            datePickerDialog.show();
        });
    }

    private void setupPrioritySpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.priorities,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPriority.setAdapter(adapter);
    }

    private void setupCategorySpinner() {
        categoryViewModel.getAllCategories().observe(getViewLifecycleOwner(), categoryList -> {
            categories = categoryList;

            List<String> categoryNames = new ArrayList<>();
            for (Category category : categories) {
                categoryNames.add(category.getName());
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    categoryNames);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerCategory.setAdapter(adapter);

            if (currentTask != null && currentTask.getCategoryId() != null) {
                for (int i = 0; i < categories.size(); i++) {
                    if (categories.get(i).getId() == currentTask.getCategoryId()) {
                        spinnerCategory.setSelection(i);
                        break;
                    }
                }
            }
        });
    }

    private void loadTaskData() {
        taskViewModel.getTaskById(taskId).observe(getViewLifecycleOwner(), task -> {
            if (task != null) {
                currentTask = task;

                editTextTitle.setText(task.getTitle());
                editTextDescription.setText(task.getDescription());

                if (task.getDueDate() != null) {
                    selectedDate = task.getDueDate();
                    textViewDueDate.setText(dateFormat.format(selectedDate));
                }

                spinnerPriority.setSelection(task.getPriority());

                if ("project".equals(task.getType())) {
                    radioGroupTaskType.check(R.id.radio_project_task);
                } else {
                    radioGroupTaskType.check(R.id.radio_regular_task);
                }

                buttonSave.setText(R.string.update);
            }
        });
    }

    private void saveTask() {
        String title = editTextTitle.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();

        if (title.isEmpty()) {
            editTextTitle.setError(getString(R.string.error_empty_title));
            return;
        }

        Priority priority;
        switch (spinnerPriority.getSelectedItemPosition()) {
            case 0:
                priority = Priority.HIGH;
                break;
            case 1:
                priority = Priority.MEDIUM;
                break;
            case 2:
                priority = Priority.LOW;
                break;
            default:
                priority = Priority.MEDIUM;
                break;
        }

        Long categoryId = null;
        if (spinnerCategory.getSelectedItemPosition() != AdapterView.INVALID_POSITION
                && !categories.isEmpty()) {
            categoryId = categories.get(spinnerCategory.getSelectedItemPosition()).getId();
        }

        String taskType = "regular";
        int selectedTaskTypeId = radioGroupTaskType.getCheckedRadioButtonId();
        if (selectedTaskTypeId == R.id.radio_project_task) {
            taskType = "project";
        }

        if (taskId == -1) {
            taskViewModel.insertWithBuilder(
                    title, description, selectedDate, priority, categoryId, taskType, false);
            Toast.makeText(getContext(), getString(R.string.task_saved), Toast.LENGTH_SHORT).show();
        } else {
            currentTask.setTitle(title);
            currentTask.setDescription(description);
            currentTask.setDueDate(selectedDate);
            currentTask.setPriorityEnum(priority);
            currentTask.setCategoryId(categoryId);
            currentTask.setType(taskType);

            taskViewModel.update(currentTask);
            Toast.makeText(getContext(), getString(R.string.task_saved), Toast.LENGTH_SHORT).show();
        }

        Navigation.findNavController(requireView()).navigateUp();
    }

    private void deleteTask() {
        if (currentTask != null) {
            taskViewModel.delete(currentTask);
            Toast.makeText(getContext(), getString(R.string.task_deleted), Toast.LENGTH_SHORT).show();
            Navigation.findNavController(requireView()).navigateUp();
        }
    }
}