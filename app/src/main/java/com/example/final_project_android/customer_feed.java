package com.example.final_project_android;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import androidx.appcompat.widget.SearchView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.navigation.Navigation;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

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
        InspectionReportAdapter adapter = new InspectionReportAdapter(businessId -> {
            // Navigate to the reviews page using the ID from nav_graph
            Bundle bundle = new Bundle();

            bundle.putString("filterType", "business_id");
            bundle.putString("filterValue", businessId);

            Navigation.findNavController(view).navigate(R.id.action_customer_feed2_to_restaurant_reviews2, bundle);
        });

        // Attach the adapter to the RecyclerView
        rvRestaurants.setAdapter(adapter);

        // Obtaining a reference to the DB (inspections)
        DatabaseReference inspectionsRef = FirebaseDatabase.getInstance().getReference("inspections");

        //Adding a listener for retrieving data
        inspectionsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Inspection_Report_class> inspectionList = new ArrayList<>();

                // check all inspections
                for (DataSnapshot ds : snapshot.getChildren()) {
                    // Automatic conversion of JSON from the cloud to Java object
                    Inspection_Report_class report = ds.getValue(Inspection_Report_class.class);

                    if (report != null) {
                        inspectionList.add(report);
                    }
                }

                //Update the adapter with the list from Firebase
                adapter.setInspections(inspectionList);

                //Default Sort: Ensure the list is sorted by Date (Newest First) initially
                adapter.sortByDate();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Error handling
                Toast.makeText(getContext(), "Error fetching data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        //search logic
        SearchView searchView = view.findViewById(R.id.Customer_search);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) { return false; }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filter(newText);
                return true;
            }
        });

        // Activate the "Sort by Grade" button with Toggle functionality
        Button btnSortGrade = view.findViewById(R.id.btn_sort_grade);

        // Array to hold the state: [false] = Sorted by Date (Default), [true] = Sorted by Grade
        final boolean[] isSortedByGrade = {false};

        if (btnSortGrade != null) {
            btnSortGrade.setOnClickListener(v -> {
                if (!isSortedByGrade[0]) {
                    // Switch to Grade Sort using the adapter method
                    adapter.sortByGrade();
                    isSortedByGrade[0] = true;

                    // Change text and background color to indicate active state
                    btnSortGrade.setText("Date");
                    btnSortGrade.setBackgroundTintList(ColorStateList.valueOf(android.graphics.Color.parseColor("#0D47A1")));
                } else {
                    // Revert to Date Sort using the adapter method
                    adapter.sortByDate();
                    isSortedByGrade[0] = false;

                    // Revert text and background color to original
                    btnSortGrade.setText("Grade");
                    // Retrieve original color from resources
                    int originalColor = view.getContext().getColor(R.color.blue);
                    btnSortGrade.setBackgroundTintList(ColorStateList.valueOf(originalColor));
                }
            });
        }

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