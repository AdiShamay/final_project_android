package com.example.final_project_android;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

// Adapter for the filtered inspection history list
public class FilteredInspectionsAdapter extends RecyclerView.Adapter<FilteredInspectionsAdapter.ViewHolder> {

    private List<Inspection_Report_class> inspectionsList = new ArrayList<>();

    // Updated interface to pass the unique report ID upon clicking
    public interface OnReviewClickListener { void onReviewClick(String reportId); }
    private final OnReviewClickListener listener;

    public FilteredInspectionsAdapter(OnReviewClickListener listener) {
        this.listener = listener;
    }

    // Replace current list with new filtered data
    public void setInspections(List<Inspection_Report_class> newList) {
        this.inspectionsList = newList;
        notifyDataSetChanged();
    }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflating the custom card layout for each row
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_restaurant_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Inspection_Report_class inspection = inspectionsList.get(position);

        // Setting values from the data object to the view holder
        holder.tvName.setText(inspection.getRestaurant_name());
        holder.tvAddress.setText(inspection.getRestaurant_address());
        holder.tvDate.setText(inspection.getDate());

        // Grade text and dynamic color assignment
        String grade = inspection.getFinal_grade();
        holder.tvGrade.setText(grade);

        // Apply color based on the inspection grade
        if ("A".equals(grade)) {
            holder.tvGrade.setTextColor(Color.parseColor("#4CAF50"));
        } else if ("B".equals(grade)) {
            holder.tvGrade.setTextColor(Color.parseColor("#FFC107"));
        } else {
            holder.tvGrade.setTextColor(Color.RED);
        }

        // Trigger navigation and pass the specific report ID
        holder.itemView.setOnClickListener(v -> listener.onReviewClick(inspection.getReport_id()));
    }

    @Override
    public int getItemCount() {
        return inspectionsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvAddress, tvDate, tvGrade;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Linking UI components to their XML IDs
            tvName = itemView.findViewById(R.id.tv_restaurant_name);
            tvAddress = itemView.findViewById(R.id.tv_restaurant_address);
            tvDate = itemView.findViewById(R.id.tv_inspection_date);
            tvGrade = itemView.findViewById(R.id.tv_sanitation_grade);
        }
    }
}