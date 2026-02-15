package com.example.final_project_android;

import android.app.TimePickerDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Adapter class for managing available inspection requests in a RecyclerView.
 * This class handles data binding and user interactions for claimable requests.
 */
public class AvailableRequestsAdapter extends RecyclerView.Adapter<AvailableRequestsAdapter.ViewHolder> {

    private List<Inspection_Request_class> requestList;
    private List<Inspection_Request_class> fullList; // Copy for search
    private OnRequestAcceptedListener listener;

    // Interface for handling request acceptance events.

    public interface OnRequestAcceptedListener {
        void onAccepted(Inspection_Request_class request, String time);
    }

    public AvailableRequestsAdapter(List<Inspection_Request_class> requestList, OnRequestAcceptedListener listener) {
        this.requestList = requestList;
        this.fullList = new ArrayList<>(requestList);
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflates the custom layout for each available request item
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_available_request, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Inspection_Request_class request = requestList.get(position);

        // Populate view components with data from the request object
        holder.tvName.setText(request.getRes_name());
        holder.tvAddress.setText(request.getAddress());
        holder.tvDate.setText("Date: " + request.getRequested_date());

        // Opens a TimePickerDialog for the inspector to choose an arrival time
        holder.etTime.setOnClickListener(v -> {
            Calendar mcurrentTime = Calendar.getInstance();
            int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
            int minute = mcurrentTime.get(Calendar.MINUTE);
            TimePickerDialog mTimePicker = new TimePickerDialog(v.getContext(), (timePicker, selectedHour, selectedMinute) -> {
                holder.etTime.setText(String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute));
            }, hour, minute, true);
            mTimePicker.setTitle("Select Inspection Time");
            mTimePicker.show();
        });

        // Validates input and triggers the acceptance listener
        holder.btnAccept.setOnClickListener(v -> {
            String time = holder.etTime.getText().toString();
            if (!time.isEmpty()) {
                listener.onAccepted(request, time);
            } else {
                Toast.makeText(v.getContext(), "Please select an hour", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

    // Call this from Fragment when Firebase data changes
    public void updateList(List<Inspection_Request_class> newList) {
        this.requestList.clear();
        this.requestList.addAll(newList);

        this.fullList.clear();
        this.fullList.addAll(newList);
        notifyDataSetChanged();
    }

    // Search Logic
    public void filter(String query) {
        requestList.clear();
        if (query.isEmpty()) {
            requestList.addAll(fullList);
        } else {
            String pattern = query.toLowerCase().trim();
            for (Inspection_Request_class item : fullList) {
                if ((item.getRes_name() != null && item.getRes_name().toLowerCase().contains(pattern)) ||
                        (item.getAddress() != null && item.getAddress().toLowerCase().contains(pattern)) ||
                        (item.getRequested_date() != null && item.getRequested_date().contains(pattern))) {
                    requestList.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    /**
     * ViewHolder class providing references to the UI components for each list item.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvAddress, tvDate;
        EditText etTime;
        Button btnAccept;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_res_name);
            tvAddress = itemView.findViewById(R.id.tv_res_address);
            tvDate = itemView.findViewById(R.id.tv_requested_date);
            etTime = itemView.findViewById(R.id.et_inspection_time);
            btnAccept = itemView.findViewById(R.id.btn_accept_request);
        }
    }
}