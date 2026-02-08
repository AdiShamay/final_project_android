package com.example.final_project_android;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Adapter for the Customer Feed list.
 * It binds the restaurant data to the card view.
 */
public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.ViewHolder> {

    // Interface to handle clicks
    public interface OnItemClickListener {
        void onItemClick(String restaurantName);
    }

    private final OnItemClickListener listener;

    public RestaurantAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Here we connect to the XML of the single card
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_restaurant_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Dummy data for testing (in the future, this will come from MongoDB)
        holder.tvName.setText("Restaurant " + (position + 1));
        holder.tvAddress.setText("Herzl St " + (position + 10) + ", Tel Aviv");
        holder.tvGrade.setText("Grade: A");

        // When a user clicks on the card
        holder.itemView.setOnClickListener(v -> {
            listener.onItemClick("Restaurant " + (position + 1));
        });
    }

    @Override
    public int getItemCount() {
        return 10; // Number of dummy items to show
    }

    // Holds the view elements
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