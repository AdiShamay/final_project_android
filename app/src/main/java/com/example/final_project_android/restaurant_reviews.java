package com.example.final_project_android;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Fragment showing the inspection history list.
 * Clicking a row navigates to the detailed review page.
 */
public class restaurant_reviews extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_restaurant_reviews, container, false);

        // Find the RecyclerView in the layout
        RecyclerView rvReviews = view.findViewById(R.id.rv_reviews_history);

        // Set the LayoutManager to arrange items linearly
        rvReviews.setLayoutManager(new LinearLayoutManager(getContext()));

        // Create the adapter and define the click behavior
        ReviewsAdapter adapter = new ReviewsAdapter(() -> {

            // Navigate to the detailed review page when a row is clicked
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new review_details())
                    .addToBackStack(null) // Allow user to navigate back
                    .commit();
        });

        // Attach the adapter to the RecyclerView
        rvReviews.setAdapter(adapter);
        return view;
    }
}