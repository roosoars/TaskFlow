package com.roosoars.taskflow.ui.adapters;

import android.content.Context;
import android.content.res.Resources;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TaskAdapter extends ListAdapter<TaskWithCategory, TaskAdapter.TaskViewHolder> {

    private final OnTaskClickListener listener;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
    private final Context context;

    public interface OnTaskClickListener {
        void onTaskClick(TaskWithCategory task);
        void onTaskLongClick(TaskWithCategory task);
        void onTaskCheckedChange(Task task, boolean isChecked);
        void onTaskSwiped(Task task, int direction);
        boolean isTaskSelected(Task task);
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

        holder.textViewTitle.setText(task.getTitle());

        if (task.isCompleted()) {
            holder.textViewTitle.setPaintFlags(holder.textViewTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.cardView.setAlpha(0.8f);
        } else {
            holder.textViewTitle.setPaintFlags(holder.textViewTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            holder.cardView.setAlpha(1.0f);
        }

        if (task.getDueDate() != null) {
            Date now = new Date();
            String formattedDate = dateFormat.format(task.getDueDate());

            if (task.getDueDate().before(now) && !task.isCompleted()) {
                holder.textViewDueDate.setText("Overdue: " + formattedDate);
                holder.textViewDueDate.setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_light));
            } else if (isDueSoon(task) && !task.isCompleted()) {
                holder.textViewDueDate.setText("Due soon: " + formattedDate);
                holder.textViewDueDate.setTextColor(ContextCompat.getColor(context, android.R.color.holo_orange_light));
            } else {
                holder.textViewDueDate.setText(formattedDate);
                holder.textViewDueDate.setTextColor(ContextCompat.getColor(context, android.R.color.darker_gray));
            }

            holder.textViewDueDate.setVisibility(View.VISIBLE);
        } else {
            holder.textViewDueDate.setVisibility(View.GONE);
        }

        if (currentItem.getCategory() != null) {
            holder.textViewCategory.setText(currentItem.getCategory().getName());
            holder.textViewCategory.setVisibility(View.VISIBLE);

            int categoryColor;
            try {
                categoryColor = ContextCompat.getColor(context, currentItem.getCategory().getColor());
            } catch (Resources.NotFoundException e) {
                categoryColor = ContextCompat.getColor(context, android.R.color.holo_blue_light);
            }

            holder.categoryColorView.setBackgroundColor(categoryColor);
            holder.categoryColorView.setVisibility(View.VISIBLE);
        } else {
            holder.textViewCategory.setVisibility(View.GONE);
            holder.categoryColorView.setVisibility(View.GONE);
        }

        updateColorBar(holder, task);

        holder.checkBoxCompleted.setChecked(task.isCompleted());

        boolean isSelected = listener.isTaskSelected(task);
        holder.cardView.setActivated(isSelected);
        if (isSelected) {
            holder.cardView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimaryLight));
        } else {
            holder.cardView.setBackgroundColor(ContextCompat.getColor(context, android.R.color.white));
        }
    }

    private void updateColorBar(TaskViewHolder holder, Task task) {
        View priorityColorBar = holder.itemView.findViewById(R.id.priority_color_bar);
        if (priorityColorBar != null) {
            int barColor;

            if (task.isCompleted()) {
                barColor = ContextCompat.getColor(context, android.R.color.darker_gray);
            }
            else if (task.getDueDate() != null) {
                if (isTaskDueToday(task)) {
                    barColor = ContextCompat.getColor(context, android.R.color.holo_red_light);
                } else if (isTaskDueTomorrow(task)) {
                    barColor = ContextCompat.getColor(context, android.R.color.holo_orange_light);
                } else {
                    barColor = ContextCompat.getColor(context, R.color.colorPrimary);
                }
            } else {
                barColor = ContextCompat.getColor(context, R.color.colorPrimary);
            }

            priorityColorBar.setBackgroundColor(barColor);
        }
    }

    private boolean isTaskDueToday(Task task) {
        if (task.getDueDate() == null) return false;

        Calendar taskDate = Calendar.getInstance();
        taskDate.setTime(task.getDueDate());

        Calendar today = Calendar.getInstance();

        return taskDate.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                taskDate.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR);
    }

    private boolean isTaskDueTomorrow(Task task) {
        if (task.getDueDate() == null) return false;

        Calendar taskDate = Calendar.getInstance();
        taskDate.setTime(task.getDueDate());

        Calendar tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DAY_OF_YEAR, 1);

        return taskDate.get(Calendar.YEAR) == tomorrow.get(Calendar.YEAR) &&
                taskDate.get(Calendar.DAY_OF_YEAR) == tomorrow.get(Calendar.DAY_OF_YEAR);
    }

    private boolean isDueSoon(Task task) {
        if (task.getDueDate() == null) {
            return false;
        }

        Date now = new Date();
        long diffInMillis = task.getDueDate().getTime() - now.getTime();
        long diffInHours = diffInMillis / (60 * 60 * 1000);

        return diffInHours >= 0 && diffInHours <= 24;
    }

    class TaskViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewTitle;
        private final TextView textViewDueDate;
        private final TextView textViewCategory;
        private final View categoryColorView;
        private final CheckBox checkBoxCompleted;
        private final CardView cardView;
        private final View priorityColorBar;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewTitle = itemView.findViewById(R.id.text_view_task_title);
            textViewDueDate = itemView.findViewById(R.id.text_view_due_date);
            textViewCategory = itemView.findViewById(R.id.text_view_category);
            categoryColorView = itemView.findViewById(R.id.image_view_category_color);
            checkBoxCompleted = itemView.findViewById(R.id.checkbox_completed);
            cardView = itemView.findViewById(R.id.card_view_task);
            priorityColorBar = itemView.findViewById(R.id.priority_color_bar);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onTaskClick(getItem(position));
                }
            });

            itemView.setOnLongClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onTaskLongClick(getItem(position));
                    return true;
                }
                return false;
            });

            checkBoxCompleted.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    TaskWithCategory taskWithCategory = getItem(position);
                    Task task = taskWithCategory.getTask();

                    boolean isCompleted = checkBoxCompleted.isChecked();

                    task.setCompleted(isCompleted);

                    if (isCompleted) {
                        textViewTitle.setPaintFlags(textViewTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        cardView.animate()
                                .alpha(0.8f)
                                .setDuration(300)
                                .start();
                    } else {
                        textViewTitle.setPaintFlags(textViewTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                        cardView.animate()
                                .alpha(1.0f)
                                .setDuration(300)
                                .start();
                    }

                    updateColorBar(this, task);

                    if (task.getDueDate() != null) {
                        Date now = new Date();
                        String formattedDate = dateFormat.format(task.getDueDate());

                        if (isCompleted) {
                            textViewDueDate.setText(formattedDate);
                            textViewDueDate.setTextColor(ContextCompat.getColor(context, android.R.color.darker_gray));
                        } else if (task.getDueDate().before(now)) {
                            textViewDueDate.setText("Overdue: " + formattedDate);
                            textViewDueDate.setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_light));
                        } else if (isDueSoon(task)) {
                            textViewDueDate.setText("Due soon: " + formattedDate);
                            textViewDueDate.setTextColor(ContextCompat.getColor(context, android.R.color.holo_orange_light));
                        }
                    }

                    listener.onTaskCheckedChange(task, isCompleted);
                }
            });
        }
    }

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