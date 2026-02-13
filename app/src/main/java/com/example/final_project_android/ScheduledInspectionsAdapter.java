package com.example.final_project_android;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

// Adapter for managing the list of scheduled inspections and handling user actions
public class ScheduledInspectionsAdapter extends RecyclerView.Adapter<ScheduledInspectionsAdapter.ViewHolder> {

    private List<Inspection_Request_class> requestList;
    private OnItemActionListener listener;

    // Interface for handling click events on the list items
    public interface OnItemActionListener {
        void onCancelClick(Inspection_Request_class request);
        void onRescheduleClick(Inspection_Request_class request);
    }

    public ScheduledInspectionsAdapter(List<Inspection_Request_class> requestList, OnItemActionListener listener) {
        this.requestList = requestList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflates the layout for a single inspection item
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_scheduled_inspection, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Inspection_Request_class request = requestList.get(position);

        // Bind data to UI elements
        holder.tvName.setText(request.getRes_name());
        holder.tvAddress.setText(request.getAddress());
        holder.tvDate.setText(request.getRequested_date());
        holder.tvTime.setText(request.getInspection_time());

        // Set click listeners for buttons
        holder.btnCancel.setOnClickListener(v -> listener.onCancelClick(request));
        holder.btnReschedule.setOnClickListener(v -> listener.onRescheduleClick(request));
    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

    // ViewHolder class to cache view references
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvAddress, tvDate, tvTime;
        Button btnCancel, btnReschedule;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_res_name);
            tvAddress = itemView.findViewById(R.id.tv_res_address);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvTime = itemView.findViewById(R.id.tv_time);
            btnCancel = itemView.findViewById(R.id.btn_cancel);
            btnReschedule = itemView.findViewById(R.id.btn_reschedule);
        }
    }
}