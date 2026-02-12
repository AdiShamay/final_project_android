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

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(String restaurantName);
    }

    private final OnItemClickListener listener;

    //two lists : one for the source and one for the filtered view
    private List<Restaurant_class> allRestaurants = new ArrayList<>();
    private List<Restaurant_class> filteredList = new ArrayList<>();

    public RestaurantAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    // Function to update the list, keeping only restaurants with valid reviews (Grade + Date)
    public void setRestaurants(List<Restaurant_class> restaurants) {
        List<Restaurant_class> ratedRestaurants = new ArrayList<>();

        for (Restaurant_class res : restaurants) {
            // Check that BOTH Grade and Date exist and are not empty
            boolean hasGrade = res.getHealth_score() != null && !res.getHealth_score().trim().isEmpty();
            boolean hasDate = res.getDate() != null && !res.getDate().trim().isEmpty();

            if (hasGrade && hasDate) {
                ratedRestaurants.add(res);
            }
        }

        this.allRestaurants = new ArrayList<>(ratedRestaurants);
        this.filteredList = new ArrayList<>(ratedRestaurants);
        notifyDataSetChanged();
    }

    // search by name and addres or grade logic
    public void filter(String query) {
        //Clearing the currently displayed list
        filteredList.clear();

        // If the search is empty  all restaurants from the source are returned.
        if (query.isEmpty()) {
            filteredList.addAll(allRestaurants);
        } else {
            String pattern = query.toLowerCase().trim();

            for (Restaurant_class res : allRestaurants) {
                //Checking for a match between the parameters
                boolean matchesName = res.getRes_name().toLowerCase().contains(pattern);
                boolean matchesAddress = res.getAddress().toLowerCase().contains(pattern);

                if (matchesName || matchesAddress ) {
                    filteredList.add(res);
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

        Restaurant_class restaurant = filteredList.get(position);

        holder.tvName.setText(restaurant.getRes_name());
        holder.tvAddress.setText(restaurant.getAddress());

        // Bind Health Score (Grade) with color logic
        String grade = restaurant.getHealth_score();
        holder.tvGrade.setText("Grade: " + grade);

        // Set color based on grade
        if (grade.equals("A")) {
            holder.tvGrade.setTextColor(android.graphics.Color.parseColor("#4CAF50")); // Green
        } else if (grade.equals("B")) {
            holder.tvGrade.setTextColor(android.graphics.Color.parseColor("#FFC107")); // Orange
        } else {
            holder.tvGrade.setTextColor(android.graphics.Color.RED); // Red
        }

        // Bind Inspection Date
        holder.tvDate.setText("Last Inspection: " + restaurant.getDate());

        holder.itemView.setOnClickListener(v -> {
            listener.onItemClick(restaurant.getRes_name());
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

    public void sortByGrade() {
        Collections.sort(allRestaurants, (r1, r2) -> {
            String s1 = (r1.getHealth_score() != null) ? r1.getHealth_score() : "";
            String s2 = (r2.getHealth_score() != null) ? r2.getHealth_score() : "";

            // 2. Handle empty scores
            if (s1.isEmpty() && s2.isEmpty()) return 0;
            if (s1.isEmpty()) return 1;
            if (s2.isEmpty()) return -1;

            //Primary Sort: Health Grade (A -> B -> C)
            int gradeCompare = s1.compareTo(s2);

            // 4. Sub-case Tie-breaker: If grades are the same, sort by Date (Newest first)
            if (gradeCompare == 0) {
                String date1 = (r1.getDate() != null) ? r1.getDate() : "";
                String date2 = (r2.getDate() != null) ? r2.getDate() : "";
                return date2.compareTo(date1); // Descending date
            }

            return gradeCompare;
        });
        notifyDataSetChanged();
    }
}