package com.example.final_project_android;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.navigation.Navigation;

/**
 * Fragment that displays the list of restaurants using the RestaurantAdapter.
 * Handles the navigation to the specific restaurant's review history.
 */
public class customer_feed extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_customer_feed, container, false);

        // Find the RecyclerView in the layout
        RecyclerView rvRestaurants = view.findViewById(R.id.rv_restaurants);

        // Set the LayoutManager to arrange items linearly
        rvRestaurants.setLayoutManager(new LinearLayoutManager(getContext()));

        // Create the adapter and define the click behavior
        RestaurantAdapter adapter = new RestaurantAdapter(restaurantName -> {
            // Navigate to the reviews page using the ID from nav_graph
            Navigation.findNavController(view).navigate(R.id.action_customer_feed2_to_restaurant_reviews2);
        });

        // Attach the adapter to the RecyclerView
        rvRestaurants.setAdapter(adapter);

        //the Return button
        // Use ImageButton with the consistent ID btn_return
        ImageButton btnReturn = view.findViewById(R.id.btn_return);
        btnReturn.setOnClickListener(v -> {
            // Navigate back to the previous screen in the stack (Home)
            Navigation.findNavController(v).popBackStack();
        });
        return view;
    }
}