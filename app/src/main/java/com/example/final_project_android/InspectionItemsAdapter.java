package com.example.final_project_android;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

/**
 * Adapter for the inspection checklist form.
 * Handles real-time point calculation and comment saving.
 */
public class InspectionItemsAdapter extends RecyclerView.Adapter<InspectionItemsAdapter.ViewHolder> {

    private List<new_inspection_item> items;
    private OnPointsChangedListener listener;

    // Interface to notify the Fragment when points are updated
    public interface OnPointsChangedListener { void onPointsChanged(); }

    public InspectionItemsAdapter(List<new_inspection_item> items, OnPointsChangedListener listener) {
        this.items = items;
        this.listener = listener;
    }

    /**
     * Iterates through the list to calculate the total penalty points.
     */
    public int calculateTotalPoints() {
        int total = 0;
        for (new_inspection_item item : items) { total += item.getCurrentPoints(); }
        return total;
    }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Updated to use your correct XML filename to prevent NullPointerException
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_new_inspection_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        new_inspection_item item = items.get(position);
        holder.tvCategory.setText(item.getCategory());
        holder.tvDescription.setText(item.getDescription());

        // Handling Points Input
        if (holder.pointsWatcher != null) holder.etPoints.removeTextChangedListener(holder.pointsWatcher);

        holder.etPoints.setText(item.getCurrentPoints() == 0 ? "" : String.valueOf(item.getCurrentPoints()));

        holder.pointsWatcher = new TextWatcher() {
            public void afterTextChanged(Editable s) {
                try {
                    String input = s.toString();
                    if (input.isEmpty()) {
                        item.setCurrentPoints(0);
                        if (listener != null) listener.onPointsChanged();
                        return;
                    }
                    int val = Integer.parseInt(input);
                    if (val < 0 || val > 10) {
                        Toast.makeText(holder.itemView.getContext(),
                                "Please enter a grade between 0-10", Toast.LENGTH_SHORT).show();
                        item.setCurrentPoints(0);
                        holder.etPoints.setText("");
                    } else {
                        item.setCurrentPoints(val);
                    }
                    if(listener!=null)
                        listener.onPointsChanged();
                }
                catch (Exception e) { item.setCurrentPoints(0); }
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        };
        holder.etPoints.addTextChangedListener(holder.pointsWatcher);

        // Handling Comments Input
        holder.etComments.setText(item.getComments());
        holder.etComments.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) { item.setComments(s.toString()); }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });
    }

    @Override public int getItemCount() { return items.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategory, tvDescription;
        EditText etPoints, etComments;
        TextWatcher pointsWatcher;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategory = itemView.findViewById(R.id.tv_row_category);
            tvDescription = itemView.findViewById(R.id.tv_row_description);
            etPoints = itemView.findViewById(R.id.et_row_points);
            etComments = itemView.findViewById(R.id.et_row_comments);
        }
    }
}