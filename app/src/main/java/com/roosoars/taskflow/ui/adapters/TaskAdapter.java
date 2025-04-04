package com.roosoars.taskflow.ui.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.roosoars.taskflow.R;
import com.roosoars.taskflow.model.Task;
import com.roosoars.taskflow.model.TaskWithCategory;
import com.roosoars.taskflow.ui.decorators.TaskItemDecorator;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Adapter for displaying tasks in a RecyclerView
 * Follows the Adapter Pattern
 */
public class TaskAdapter extends ListAdapter<TaskWithCategory, TaskAdapter.TaskViewHolder> {

    private final OnTaskClickListener listener;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
    private final Context context;

    // Interface for handling task item clicks
    public interface OnTaskClickListener {
        void onTaskClick(TaskWithCategory task);
        void onTaskCheckedChange(Task task, boolean isChecked);
    }

    public TaskAdapter(Context context, OnTaskClickListener listener) {
        super(DIFF_CALLBACK);
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        TaskWithCategory currentItem = getItem(position);
        Task task = currentItem.getTask();

        // Apply Decorator pattern for UI enhancements based on task properties
        TaskItemDecorator decorator = new TaskItemDecorator(task);

        // Set task title
        holder.textViewTitle.setText(task.getTitle());

        // Apply strike-through if completed
        if (task.isCompleted()) {
            holder.textViewTitle.setPaintFlags(holder.textViewTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            holder.textViewTitle.setPaintFlags(holder.textViewTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }

        // Set due date
        if (task.getDueDate() != null) {
            holder.textViewDueDate.setText(dateFormat.format(task.getDueDate()));
            holder.textViewDueDate.setVisibility(View.VISIBLE);

            // Highlight overdue tasks
            if (decorator.isOverdue()) {
                holder.textViewDueDate.setTextColor(ContextCompat.getColor(context, R.color.priorityHigh));
            } else {
                holder.textViewDueDate.setTextColor(ContextCompat.getColor(context, R.color.darkGray));
            }
        } else {
            holder.textViewDueDate.setVisibility(View.GONE);
        }

        // Set category
        if (currentItem.getCategory() != null) {
            holder.textViewCategory.setText(currentItem.getCategory().getName());
            holder.textViewCategory.setVisibility(View.VISIBLE);

            // Set category color indicator
            holder.imageViewCategoryColor.setBackgroundColor(
                    ContextCompat.getColor(context, currentItem.getCategory().getColor()));
            holder.imageViewCategoryColor.setVisibility(View.VISIBLE);
        } else {
            holder.textViewCategory.setVisibility(View.GONE);
            holder.imageViewCategoryColor.setVisibility(View.GONE);
        }

        // Set priority color
        int priorityColor;
        switch (task.getPriorityEnum()) {
            case HIGH:
                priorityColor = R.color.priorityHigh;
                break;
            case MEDIUM:
                priorityColor = R.color.priorityMedium;
                break;
            case LOW:
                priorityColor = R.color.priorityLow;
                break;
            default:
                priorityColor = R.color.lightGray;
                break;
        }

        holder.viewPriorityIndicator.setBackgroundColor(ContextCompat.getColor(context, priorityColor));

        // Set completion state
        holder.checkBoxCompleted.setChecked(task.isCompleted());

        // Apply card background based on task state (Decorator pattern)
        int cardBackgroundColor = decorator.getCardBackgroundColor(context);
        holder.cardView.setCardBackgroundColor(cardBackgroundColor);
    }

    class TaskViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewTitle;
        private final TextView textViewDueDate;
        private final TextView textViewCategory;
        private final ImageView imageViewCategoryColor;
        private final View viewPriorityIndicator;
        private final CheckBox checkBoxCompleted;
        private final CardView cardView;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewTitle = itemView.findViewById(R.id.text_view_task_title);
            textViewDueDate = itemView.findViewById(R.id.text_view_due_date);
            textViewCategory = itemView.findViewById(R.id.text_view_category);
            imageViewCategoryColor = itemView.findViewById(R.id.image_view_category_color);
            viewPriorityIndicator = itemView.findViewById(R.id.view_priority_indicator);
            checkBoxCompleted = itemView.findViewById(R.id.checkbox_completed);
            cardView = itemView.findViewById(R.id.card_view_task);

            // Set click listener for the entire item
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onTaskClick(getItem(position));
                }
            });

            // Set click listener for the checkbox
            checkBoxCompleted.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    TaskWithCategory taskWithCategory = getItem(position);
                    Task task = taskWithCategory.getTask();
                    listener.onTaskCheckedChange(task, checkBoxCompleted.isChecked());
                }
            });
        }
    }

    // DiffUtil callback for efficient RecyclerView updates
    private static final DiffUtil.ItemCallback<TaskWithCategory> DIFF_CALLBACK = new DiffUtil.ItemCallback<TaskWithCategory>() {
        @Override
        public boolean areItemsTheSame(@NonNull TaskWithCategory oldItem, @NonNull TaskWithCategory newItem) {
            return oldItem.getTask().getId() == newItem.getTask().getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull TaskWithCategory oldItem, @NonNull TaskWithCategory newItem) {
            Task oldTask = oldItem.getTask();
            Task newTask = newItem.getTask();

            return oldTask.getTitle().equals(newTask.getTitle())
                    && oldTask.isCompleted() == newTask.isCompleted()
                    && oldTask.getPriority() == newTask.getPriority()
                    && (oldTask.getCategoryId() == null ? newTask.getCategoryId() == null :
                    oldTask.getCategoryId().equals(newTask.getCategoryId()))
                    && (oldTask.getDueDate() == null ? newTask.getDueDate() == null :
                    oldTask.getDueDate().equals(newTask.getDueDate()));
        }
    };
}