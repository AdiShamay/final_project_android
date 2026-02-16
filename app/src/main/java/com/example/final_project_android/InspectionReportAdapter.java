package com.example.final_project_android;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Renamed to InspectionReportAdapter as it handles inspection reports
public class InspectionReportAdapter extends RecyclerView.Adapter<InspectionReportAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(String businessId, String restaurantName);
    }

    private final OnItemClickListener listener;

    //two lists : one for the source and one for the filtered view
    // Renamed lists to reflect that they hold inspection reports
    private List<Inspection_Report_class> allInspections = new ArrayList<>();
    private List<Inspection_Report_class> filteredList = new ArrayList<>();

    public InspectionReportAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    // Function to update the list, keeping only reports with valid data (Grade + Date)
    // Renamed method to setInspections
    public void setInspections(List<Inspection_Report_class> inspections) {
        List<Inspection_Report_class> ratedInspections = new ArrayList<>();

        for (Inspection_Report_class report : inspections) {
            // Check that BOTH Grade and Date exist and are not empty
            boolean hasGrade = report.getFinal_grade() != null && !report.getFinal_grade().trim().isEmpty();
            boolean hasDate = report.getDate() != null && !report.getDate().trim().isEmpty();

            if (hasGrade && hasDate) {
                ratedInspections.add(report);
            }
        }

        this.allInspections = new ArrayList<>(ratedInspections);
        this.filteredList = new ArrayList<>(ratedInspections);
        notifyDataSetChanged();
    }

    // search by name or addres logic
    public void filter(String query) {
        //Clearing the currently displayed list
        filteredList.clear();

        // If the search is empty all inspections from the source are returned.
        if (query.isEmpty()) {
            filteredList.addAll(allInspections);
        } else {
            String pattern = query.toLowerCase().trim();

            for (Inspection_Report_class report : allInspections) {
                //Checking for a match between the parameters
                boolean matchesName = report.getRestaurant_name().toLowerCase().contains(pattern);
                boolean matchesAddress = report.getRestaurant_address().toLowerCase().contains(pattern);

                if (matchesName || matchesAddress ) {
                    filteredList.add(report);
                }
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_restaurant_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Inspection_Report_class report = filteredList.get(position);

        holder.tvName.setText(report.getRestaurant_name());
        holder.tvAddress.setText(report.getRestaurant_address());

        // Bind Health Score (Grade) with color logic
        String grade = report.getFinal_grade();
        holder.tvGrade.setText(grade);

        // Set color based on grade
        if (grade.equals("A")) {
            holder.tvGrade.setTextColor(android.graphics.Color.parseColor("#4CAF50")); // Green
        } else if (grade.equals("B")) {
            holder.tvGrade.setTextColor(android.graphics.Color.parseColor("#FFC107")); // Orange
        } else {
            holder.tvGrade.setTextColor(android.graphics.Color.RED); // Red
        }

        // Bind Inspection Date
        holder.tvDate.setText("Inspection Date: " + report.getDate());

        holder.itemView.setOnClickListener(v -> {
            listener.onItemClick(report.getBusiness_id(), report.getRestaurant_name());
        });
    }
    //get the size of the list
    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvAddress, tvGrade, tvDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_restaurant_name);
            tvAddress = itemView.findViewById(R.id.tv_restaurant_address);
            tvGrade = itemView.findViewById(R.id.tv_sanitation_grade);
            tvDate = itemView.findViewById(R.id.tv_inspection_date);
        }
    }

    // Sorts the displayed list by Health Grade (A -> B -> C)
    // If grades are equal, it uses the Date as a secondary sort (Newest first)
    public void sortByGrade() {
        java.util.Comparator<Inspection_Report_class> gradeComparator = (r1, r2) -> {
            String s1 = (r1.getFinal_grade() != null) ? r1.getFinal_grade() : "";
            String s2 = (r2.getFinal_grade() != null) ? r2.getFinal_grade() : "";

            // Handle empty or null grades (push to bottom)
            if (s1.isEmpty() && s2.isEmpty()) return 0;
            if (s1.isEmpty()) return 1;
            if (s2.isEmpty()) return -1;

            // Compare grades alphabetically
            int gradeCompare = s1.compareTo(s2);

            // Tie-breaker: If grades are identical, sort by Date descending
            if (gradeCompare == 0) {
                String date1 = (r1.getDate() != null) ? r1.getDate() : "";
                String date2 = (r2.getDate() != null) ? r2.getDate() : "";
                return date2.compareTo(date1);
            }

            return gradeCompare;
        };

        // Apply sorting to the list currently shown to the user
        Collections.sort(filteredList, gradeComparator);

        // Apply sorting to the source list to maintain order when search filter is cleared
        Collections.sort(allInspections, gradeComparator);

        // Refresh the RecyclerView to show the new order
        notifyDataSetChanged();
    }

    // Sorts the displayed list by Date (Newest -> Oldest)
    public void sortByDate() {
        java.util.Comparator<Inspection_Report_class> dateComparator = (r1, r2) -> {
            String date1 = (r1.getDate() != null) ? r1.getDate() : "";
            String date2 = (r2.getDate() != null) ? r2.getDate() : "";
            // Descending order (Newest date first)
            return date2.compareTo(date1);
        };

        // Apply sorting to the list currently shown to the user
        Collections.sort(filteredList, dateComparator);

        // Apply sorting to the source list
        Collections.sort(allInspections, dateComparator);

        // Refresh the RecyclerView to show the new order
        notifyDataSetChanged();
    }
}