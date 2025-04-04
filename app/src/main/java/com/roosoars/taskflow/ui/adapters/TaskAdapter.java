package com.roosoars.taskflow.ui.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.roosoars.taskflow.R;
import com.roosoars.taskflow.model.Priority;
import com.roosoars.taskflow.model.Task;
import com.roosoars.taskflow.model.TaskWithCategory;
import com.roosoars.taskflow.ui.decorators.TaskItemDecorator;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Adapter for displaying tasks in a RecyclerView with enhanced UI
 * Follows the Adapter Pattern
 */
public class TaskAdapter extends ListAdapter<TaskWithCategory, TaskAdapter.TaskViewHolder> {

    private final OnTaskClickListener listener;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
    private final Context context;

    // Interface para manejo de clicks en tareas
    public interface OnTaskClickListener {
        void onTaskClick(TaskWithCategory task);
        void onTaskCheckedChange(Task task, boolean isChecked);
        void onTaskSwiped(Task task, int direction);
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
            holder.cardView.setAlpha(0.8f); // Dim completed tasks
        } else {
            holder.textViewTitle.setPaintFlags(holder.textViewTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            holder.cardView.setAlpha(1.0f);
        }

        // Set due date with color coding
        if (task.getDueDate() != null) {
            Date now = new Date();
            String formattedDate = dateFormat.format(task.getDueDate());

            if (task.getDueDate().before(now) && !task.isCompleted()) {
                // Overdue task
                holder.textViewDueDate.setText("Overdue: " + formattedDate);
                holder.textViewDueDate.setTextColor(ContextCompat.getColor(context, R.color.taskOverdue));
            } else if (decorator.isDueSoon() && !task.isCompleted()) {
                // Due soon
                holder.textViewDueDate.setText("Due soon: " + formattedDate);
                holder.textViewDueDate.setTextColor(ContextCompat.getColor(context, R.color.taskDueSoon));
            } else {
                // Normal due date
                holder.textViewDueDate.setText(formattedDate);
                holder.textViewDueDate.setTextColor(ContextCompat.getColor(context, R.color.mediumGray));
            }

            holder.textViewDueDate.setVisibility(View.VISIBLE);
        } else {
            holder.textViewDueDate.setVisibility(View.GONE);
        }

        // Set category with colored dot
        if (currentItem.getCategory() != null) {
            holder.textViewCategory.setText(currentItem.getCategory().getName());
            holder.textViewCategory.setVisibility(View.VISIBLE);

            // Get category color
            int categoryColor = ContextCompat.getColor(context, currentItem.getCategory().getColor());

            // Apply color to category indicator
            holder.categoryColorView.setBackgroundColor(categoryColor);
            holder.categoryColorView.setVisibility(View.VISIBLE);

            // Set label color
            View labelView = holder.itemView.findViewById(R.id.label_category);
            labelView.setBackgroundTintList(ContextCompat.getColorStateList(context, currentItem.getCategory().getColor()));
        } else {
            holder.textViewCategory.setVisibility(View.GONE);
            holder.categoryColorView.setVisibility(View.GONE);
        }

        // Set priority label color
        View priorityLabel = holder.itemView.findViewById(R.id.label_priority);
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
                priorityColor = R.color.priorityMedium;
                break;
        }

        priorityLabel.setBackgroundTintList(ContextCompat.getColorStateList(context, priorityColor));

        // Set completion state
        holder.checkBoxCompleted.setChecked(task.isCompleted());

        // Apply card background based on task state (Decorator pattern)
        int cardBackgroundColor = decorator.getCardBackgroundColor(context);
        holder.cardView.setCardBackgroundColor(cardBackgroundColor);

        // Apply subtle animation
        applyCardAnimation(holder.cardView, position);
    }

    // Add subtle animation to cards
    private void applyCardAnimation(CardView cardView, int position) {
        cardView.setAlpha(0f);
        cardView.setTranslationY(50f);
        cardView.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(300)
                .setStartDelay(position * 50)
                .start();
    }

    class TaskViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewTitle;
        private final TextView textViewDueDate;
        private final TextView textViewCategory;
        private final View categoryColorView;
        private final CheckBox checkBoxCompleted;
        private final CardView cardView;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewTitle = itemView.findViewById(R.id.text_view_task_title);
            textViewDueDate = itemView.findViewById(R.id.text_view_due_date);
            textViewCategory = itemView.findViewById(R.id.text_view_category);
            categoryColorView = itemView.findViewById(R.id.image_view_category_color);
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

                    // Animate completion
                    if (checkBoxCompleted.isChecked()) {
                        cardView.animate()
                                .alpha(0.8f)
                                .setDuration(300)
                                .start();
                    } else {
                        cardView.animate()
                                .alpha(1.0f)
                                .setDuration(300)
                                .start();
                    }

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