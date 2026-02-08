package com.example.final_project_android;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Adapter for notifications list with click listener.
 */
public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.ViewHolder> {

    // Interface for click events
    public interface OnNotificationClickListener {
        void onNotificationClick();
    }

    private final OnNotificationClickListener listener;

    // Constructor receiving the listener
    public NotificationsAdapter(OnNotificationClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Dummy Data
        holder.tvDate.setText("10/02/2026");

        if (position == 0) {
            holder.tvTitle.setText("New Inspection Request Update");
            holder.tvBody.setText("Your request has been approved pending date selection.");
            holder.tvTitle.setTypeface(null, Typeface.BOLD);
            holder.ivIcon.setColorFilter(0xFF2196F3);
        } else {
            holder.tvTitle.setText("System Welcome");
            holder.tvBody.setText("Welcome to the new sanitation system.");
            holder.tvTitle.setTypeface(null, Typeface.NORMAL);
            holder.ivIcon.setColorFilter(0xFF757575);
        }

        // Handle Click
        holder.itemView.setOnClickListener(v -> {
            listener.onNotificationClick();
        });
    }

    @Override
    public int getItemCount() {
        return 5;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvBody, tvDate;
        ImageView ivIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_notification_title);
            tvBody = itemView.findViewById(R.id.tv_notification_body);
            tvDate = itemView.findViewById(R.id.tv_notification_date);
            ivIcon = itemView.findViewById(R.id.iv_notification_icon);
        }
    }
}