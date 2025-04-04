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

import javax.inject.Inject;

/**
 * Fragment for managing categories
 * Follows MVC architecture as the "View" component
 */
public class CategoryFragment extends Fragment implements CategoryAdapter.OnCategoryClickListener {

    @Inject
    ViewModelFactory viewModelFactory;

    private CategoryViewModel categoryViewModel;
    private CategoryAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inject dependencies
        ((TaskFlowApplication) requireActivity().getApplication())
                .getAppComponent().inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category, container, false);

        // Initialize ViewModel
        categoryViewModel = new ViewModelProvider(requireActivity(), viewModelFactory)
                .get(CategoryViewModel.class);

        // Set up RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_categories);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

        adapter = new CategoryAdapter(requireContext(), this);
        recyclerView.setAdapter(adapter);

        // Observe categories
        categoryViewModel.getAllCategories().observe(getViewLifecycleOwner(), categories -> {
            adapter.submitList(categories);

            // Update empty view visibility
            View emptyView = view.findViewById(R.id.empty_view);
            if (categories.isEmpty()) {
                emptyView.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            } else {
                emptyView.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
        });

        // Set up FAB for adding categories
        FloatingActionButton fab = view.findViewById(R.id.fab_add_category);
        fab.setOnClickListener(v -> showAddCategoryDialog(null));

        return view;
    }

    @Override
    public void onCategoryClick(Category category) {
        // Show dialog to edit category
        showAddCategoryDialog(category);
    }

    @Override
    public void onCategoryLongClick(Category category) {
        // Show dialog to delete category
        showDeleteCategoryDialog(category);
    }

    // Show dialog to add or edit a category
    private void showAddCategoryDialog(@Nullable Category category) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_category, null);
        builder.setView(dialogView);

        final EditText editTextCategoryName = dialogView.findViewById(R.id.edit_text_category_name);

        // If editing an existing category, fill in the current name
        if (category != null) {
            editTextCategoryName.setText(category.getName());
            builder.setTitle(R.string.edit);
        } else {
            builder.setTitle(R.string.new_category);
        }

        builder.setPositiveButton(R.string.save, (dialog, which) -> {
            String categoryName = editTextCategoryName.getText().toString().trim();

            // Validate category name
            if (categoryName.isEmpty()) {
                Toast.makeText(getContext(), R.string.error_empty_category, Toast.LENGTH_SHORT).show();
                return;
            }

            // Randomly select a color from the pastel colors
            int[] colors = new int[] {
                    R.color.labelRed, R.color.labelGreen, R.color.labelYellow,
                    R.color.labelPurple, R.color.labelOrange
            };
            int randomColor = colors[(int) (Math.random() * colors.length)];

            if (category != null) {
                // Update existing category
                category.setName(categoryName);
                categoryViewModel.update(category);
                Toast.makeText(getContext(), R.string.category_saved, Toast.LENGTH_SHORT).show();
            } else {
                // Create new category
                Category newCategory = new Category(categoryName, randomColor);
                categoryViewModel.insert(newCategory);
                Toast.makeText(getContext(), R.string.category_saved, Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }

    // Show dialog to confirm category deletion
    private void showDeleteCategoryDialog(Category category) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(R.string.delete);
        builder.setMessage(getString(R.string.delete) + " " + category.getName() + "?");

        builder.setPositiveButton(R.string.delete, (dialog, which) -> {
            // Check if the category can be safely deleted
            if (categoryViewModel.canDeleteCategory(category.getId())) {
                categoryViewModel.delete(category);
                Toast.makeText(getContext(), R.string.category_deleted, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Cannot delete category with associated tasks", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }
}