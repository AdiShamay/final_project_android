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

    // function to update the list from the Fragment after pulling from Firebase
    public void setRestaurants(List<Restaurant_class> restaurants) {
        this.allRestaurants = new ArrayList<>(restaurants);
        this.filteredList = new ArrayList<>(restaurants);
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
        holder.tvGrade.setText("Grade: ");

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
        TextView tvName, tvAddress, tvGrade;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_restaurant_name);
            tvAddress = itemView.findViewById(R.id.tv_restaurant_address);
            tvGrade = itemView.findViewById(R.id.tv_sanitation_grade);
        }
    }
}