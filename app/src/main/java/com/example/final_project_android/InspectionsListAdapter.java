package com.example.final_project_android;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Adapter for the Inspection History list.
 * Displays summary data (Date, Inspector, Grade) for past reports.
 */
public class InspectionsListAdapter extends RecyclerView.Adapter<InspectionsListAdapter.ViewHolder> {

    // Listener for navigating to details screen
    public interface OnReviewClickListener { void onReviewClick(); }
    private final OnReviewClickListener listener;

    public InspectionsListAdapter(OnReviewClickListener listener) {
        this.listener = listener;
    }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // IMPORTANT: Use a dedicated history layout file to match the IDs below
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_inspection_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Temporary dummy data for display
        holder.tvDate.setText("2026-02-12");
        holder.tvGrade.setText(position % 2 == 0 ? "A" : "B");

        holder.itemView.setOnClickListener(v -> listener.onReviewClick());
    }

    @Override public int getItemCount() { return 5; }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvInspector, tvGrade;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // These IDs must exist inside item_history_row.xml
            tvDate = itemView.findViewById(R.id.tv_row_date);
            tvGrade = itemView.findViewById(R.id.tv_row_grade);
        }
    }
}