package com.example.final_project_android;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Adapter for the Inspection History list.
 * Binds inspection data to the row layout.
 */
public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ViewHolder> {

    public interface OnReviewClickListener {
        void onReviewClick();
    }

    private final OnReviewClickListener listener;

    public ReviewsAdapter(OnReviewClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Connect to the row XML layout
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_review_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Dummy data
        holder.tvDate.setText("12/0" + (position + 1) + "/2026");
        holder.tvInspector.setText("Inspector: Dana Cohen");

        // Logic to alternate grades just for visuals
        String grade = (position % 2 == 0) ? "A" : "B";
        holder.tvGrade.setText(grade);

        holder.itemView.setOnClickListener(v -> listener.onReviewClick());
    }

    @Override
    public int getItemCount() {
        return 5; // Number of dummy reviews to show
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvInspector, tvGrade;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tv_row_date);
            tvInspector = itemView.findViewById(R.id.tv_row_inspector);
            tvGrade = itemView.findViewById(R.id.tv_row_grade);
        }
    }
}