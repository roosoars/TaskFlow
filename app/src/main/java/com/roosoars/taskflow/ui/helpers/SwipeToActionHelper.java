package com.roosoars.taskflow.ui.helpers;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.roosoars.taskflow.R;
import com.roosoars.taskflow.ui.adapters.TaskAdapter;

/**
 * Helper class to implement swipe-to-delete and swipe-to-complete functionality
 * Enhances user experience with visual feedback
 */
public class SwipeToActionHelper extends ItemTouchHelper.SimpleCallback {

    private static final int SWIPE_THRESHOLD = 60;

    public static final int SWIPE_DIRECTION_LEFT = ItemTouchHelper.LEFT;
    public static final int SWIPE_DIRECTION_RIGHT = ItemTouchHelper.RIGHT;

    private final TaskAdapter adapter;
    private final ColorDrawable deleteBackground;
    private final ColorDrawable completeBackground;
    private final Drawable deleteIcon;
    private final Drawable completeIcon;
    private final int iconMargin;
    private final Paint clearPaint;
    private final Context context;

    public SwipeToActionHelper(Context context, TaskAdapter adapter) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.adapter = adapter;
        this.context = context;

        // Set up background colors
        deleteBackground = new ColorDrawable(ContextCompat.getColor(context, R.color.taskOverdue));
        completeBackground = new ColorDrawable(ContextCompat.getColor(context, R.color.taskComplete));

        // Set up icons
        deleteIcon = ContextCompat.getDrawable(context, R.drawable.ic_delete);
        completeIcon = ContextCompat.getDrawable(context, R.drawable.ic_task_check);

        if (deleteIcon != null) {
            deleteIcon.setTint(Color.WHITE);
        }

        if (completeIcon != null) {
            completeIcon.setTint(Color.WHITE);
        }

        iconMargin = context.getResources().getDimensionPixelSize(R.dimen.swipe_icon_margin);

        // For clean drawing
        clearPaint = new Paint();
        clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        // We don't support move action
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
        if (position != RecyclerView.NO_POSITION) {
            if (adapter instanceof TaskAdapter.OnTaskClickListener) {
                ((TaskAdapter.OnTaskClickListener) adapter).onTaskSwiped(
                        adapter.getCurrentList().get(position).getTask(),
                        direction);
            }
        }
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        View itemView = viewHolder.itemView;

        // Skip if item is being removed or added
        if (viewHolder.getAdapterPosition() == RecyclerView.NO_POSITION) {
            return;
        }

        // Ensure the canvas can be drawn on
        if (c.getWidth() == 0 || c.getHeight() == 0) {
            return;
        }

        // Save canvas state
        c.save();

        int itemHeight = itemView.getBottom() - itemView.getTop();
        int itemWidth = itemView.getRight() - itemView.getLeft();
        boolean isCanceled = dX == 0f && !isCurrentlyActive;

        if (isCanceled) {
            c.drawRect(itemView.getRight() + dX, itemView.getTop(), itemView.getRight(), itemView.getBottom(), clearPaint);
            c.drawRect(itemView.getLeft(), itemView.getTop(), itemView.getLeft() + dX, itemView.getBottom(), clearPaint);
            c.restore();
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, false);
            return;
        }

        // Swipe to delete (left)
        if (dX < 0) {
            // Draw red background
            deleteBackground.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
            deleteBackground.draw(c);

            // Calculate icon position
            if (deleteIcon != null) {
                int iconLeft = itemView.getRight() - iconMargin - deleteIcon.getIntrinsicWidth();
                int iconRight = itemView.getRight() - iconMargin;
                int iconTop = itemView.getTop() + (itemHeight - deleteIcon.getIntrinsicHeight()) / 2;
                int iconBottom = iconTop + deleteIcon.getIntrinsicHeight();

                deleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                deleteIcon.draw(c);
            }
        }
        // Swipe to complete (right)
        else if (dX > 0) {
            // Draw green background
            completeBackground.setBounds(itemView.getLeft(), itemView.getTop(), itemView.getLeft() + (int) dX, itemView.getBottom());
            completeBackground.draw(c);

            // Calculate icon position
            if (completeIcon != null) {
                int iconLeft = itemView.getLeft() + iconMargin;
                int iconRight = itemView.getLeft() + iconMargin + completeIcon.getIntrinsicWidth();
                int iconTop = itemView.getTop() + (itemHeight - completeIcon.getIntrinsicHeight()) / 2;
                int iconBottom = iconTop + completeIcon.getIntrinsicHeight();

                completeIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                completeIcon.draw(c);
            }
        }

        c.restore();

        // Draw the item on top
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }

    @Override
    public float getSwipeThreshold(@NonNull RecyclerView.ViewHolder viewHolder) {
        return SWIPE_THRESHOLD / (float) viewHolder.itemView.getWidth();
    }

    @Override
    public float getSwipeEscapeVelocity(float defaultValue) {
        return defaultValue * 10f;
    }

    @Override
    public float getSwipeVelocityThreshold(float defaultValue) {
        return defaultValue * 0.65f;
    }
}