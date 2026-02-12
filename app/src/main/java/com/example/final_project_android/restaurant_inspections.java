package com.example.final_project_android;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.navigation.Navigation;
import android.widget.ImageButton;

/**
 * Fragment showing the inspection history list.
 * Clicking a row navigates to the detailed review page.
 */
public class restaurant_inspections extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_restaurant_inspections, container, false);

        // Find the RecyclerView in the layout
        RecyclerView rvReviews = view.findViewById(R.id.rv_reviews_history);

        // Set the LayoutManager to arrange items linearly
        rvReviews.setLayoutManager(new LinearLayoutManager(getContext()));

        // Create the adapter and define the click behavior
        InspectionsListAdapter adapter = new InspectionsListAdapter(() -> {

            // Navigate to review details using the action defined in your NavGraph
            Navigation.findNavController(view).navigate(R.id.action_restaurant_reviews2_to_review_details2);
        });

        // Attach the adapter to the RecyclerView
        rvReviews.setAdapter(adapter);

        // Initialize the back button and set it to pop the fragment back stack
        ImageButton btnReturn = view.findViewById(R.id.btn_return);
        btnReturn.setOnClickListener(v -> Navigation.findNavController(v).popBackStack());

        return view;
    }
}