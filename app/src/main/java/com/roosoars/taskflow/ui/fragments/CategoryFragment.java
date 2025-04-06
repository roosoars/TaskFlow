package com.roosoars.taskflow.ui.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.roosoars.taskflow.R;
import com.roosoars.taskflow.TaskFlowApplication;
import com.roosoars.taskflow.di.ViewModelFactory;
import com.roosoars.taskflow.model.Category;
import com.roosoars.taskflow.ui.adapters.CategoryAdapter;
import com.roosoars.taskflow.viewmodel.CategoryViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import javax.inject.Inject;


public class CategoryFragment extends Fragment implements CategoryAdapter.OnCategoryClickListener {

    @Inject
    ViewModelFactory viewModelFactory;

    private CategoryViewModel categoryViewModel;
    private CategoryAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((TaskFlowApplication) requireActivity().getApplication())
                .getAppComponent().inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category, container, false);

        categoryViewModel = new ViewModelProvider(requireActivity(), viewModelFactory)
                .get(CategoryViewModel.class);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_categories);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

        adapter = new CategoryAdapter(requireContext(), this);
        recyclerView.setAdapter(adapter);

        categoryViewModel.getAllCategories().observe(getViewLifecycleOwner(), categories -> {
            adapter.submitList(categories);

            View emptyView = view.findViewById(R.id.empty_view);
            if (categories.isEmpty()) {
                emptyView.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            } else {
                emptyView.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
        });

        categoryViewModel.getActionFeedback().observe(getViewLifecycleOwner(), feedback -> {
            if (feedback != null) {
                if (feedback.equals("CONFIRM_DELETE_WITH_TASKS")) {
                } else {
                    Snackbar.make(view, feedback, Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        FloatingActionButton fab = view.findViewById(R.id.fab_add_category);
        fab.setOnClickListener(v -> showAddCategoryDialog(null));

        return view;
    }

    @Override
    public void onCategoryClick(Category category) {
        showAddCategoryDialog(category);
    }

    @Override
    public void onCategoryLongClick(Category category) {
        showDeleteCategoryDialog(category);
    }

    private void showAddCategoryDialog(@Nullable Category category) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_category, null);
        builder.setView(dialogView);

        final EditText editTextCategoryName = dialogView.findViewById(R.id.edit_text_category_name);

        if (category != null) {
            editTextCategoryName.setText(category.getName());
            builder.setTitle(R.string.edit);
        } else {
            builder.setTitle(R.string.new_category);
        }

        builder.setPositiveButton(R.string.save, (dialog, which) -> {
            String categoryName = editTextCategoryName.getText().toString().trim();

            if (categoryName.isEmpty()) {
                Toast.makeText(getContext(), R.string.error_empty_category, Toast.LENGTH_SHORT).show();
                return;
            }

            int[] colors = new int[] {
                    R.color.labelRed, R.color.labelGreen, R.color.labelYellow,
                    R.color.labelPurple, R.color.labelOrange
            };
            int randomColor = colors[(int) (Math.random() * colors.length)];

            if (category != null) {
                category.setName(categoryName);
                categoryViewModel.update(category);
            } else {
                Category newCategory = new Category(categoryName, randomColor);
                categoryViewModel.insert(newCategory);
            }
        });

        builder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }

    private void showDeleteCategoryDialog(Category category) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(R.string.delete);

        int taskCount = categoryViewModel.getTaskCountForCategory(category.getId());

        if (taskCount > 0) {
            String message = "Delete " + category.getName() + "?\n\n" +
                    "This category has " + taskCount + " tasks associated with it.";
            builder.setMessage(message);

            builder.setNeutralButton("Keep Tasks", (dialog, which) -> {
                categoryViewModel.delete(category);
            });

            builder.setPositiveButton("Delete All", (dialog, which) -> {
                categoryViewModel.deleteWithTasks(category);
            });

            builder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss());
        } else {
            builder.setMessage(getString(R.string.delete) + " " + category.getName() + "?");

            builder.setPositiveButton(R.string.delete, (dialog, which) -> {
                categoryViewModel.delete(category);
            });

            builder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss());
        }

        builder.create().show();
    }
}