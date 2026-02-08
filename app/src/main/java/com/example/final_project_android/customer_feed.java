package com.example.final_project_android;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

            // Navigate to the reviews history page when a card is clicked
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new restaurant_reviews())
                    .addToBackStack(null) // Allow user to navigate back
                    .commit();
        });

        // Attach the adapter to the RecyclerView
        rvRestaurants.setAdapter(adapter);

        return view;
    }
}