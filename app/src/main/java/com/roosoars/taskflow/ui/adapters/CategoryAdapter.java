package com.roosoars.taskflow.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.roosoars.taskflow.R;
import com.roosoars.taskflow.model.Category;

/**
 * Adapter for displaying categories in a RecyclerView
 * Follows the Adapter Pattern
 */
public class CategoryAdapter extends ListAdapter<Category, CategoryAdapter.CategoryViewHolder> {

    private final OnCategoryClickListener listener;
    private final Context context;

    // Interface for handling category item clicks
    public interface OnCategoryClickListener {
        void onCategoryClick(Category category);
        void onCategoryLongClick(Category category);
    }

    public CategoryAdapter(Context context, OnCategoryClickListener listener) {
        super(DIFF_CALLBACK);
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = getItem(position);

        // Set category name
        holder.textViewCategoryName.setText(category.getName());

        // Set category color
        holder.imageViewCategoryColor.setBackgroundColor(
                ContextCompat.getColor(context, category.getColor()));
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewCategoryName;
        private final ImageView imageViewCategoryColor;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewCategoryName = itemView.findViewById(R.id.text_view_category_name);
            imageViewCategoryColor = itemView.findViewById(R.id.image_view_category_color);

            // Set click listener for the category item
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onCategoryClick(getItem(position));
                }
            });

            // Set long click listener for the category item
            itemView.setOnLongClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onCategoryLongClick(getItem(position));
                    return true;
                }
                return false;
            });
        }
    }

    // DiffUtil callback for efficient RecyclerView updates
    private static final DiffUtil.ItemCallback<Category> DIFF_CALLBACK = new DiffUtil.ItemCallback<Category>() {
        @Override
        public boolean areItemsTheSame(@NonNull Category oldItem, @NonNull Category newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Category oldItem, @NonNull Category newItem) {
            return oldItem.getName().equals(newItem.getName()) &&
                    oldItem.getColor() == newItem.getColor();
        }
    };
}