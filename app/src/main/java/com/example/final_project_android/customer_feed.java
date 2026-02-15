package com.example.final_project_android;

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
import java.util.Collections;
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
        // CHANGED: Now receiving reportId instead of restaurantName
        RestaurantAdapter adapter = new RestaurantAdapter(businessId -> {
            // Navigate to the reviews page using the ID from nav_graph
            Bundle bundle = new Bundle();

            bundle.putString("filterType", "business_id");
            bundle.putString("filterValue", businessId);

            Navigation.findNavController(view).navigate(R.id.action_customer_feed2_to_restaurant_reviews2, bundle);
        });

        // Attach the adapter to the RecyclerView
        rvRestaurants.setAdapter(adapter);

        // Obtaining a reference to the DB (CHANGED: now pointing to "inspections")
        DatabaseReference inspectionsRef = FirebaseDatabase.getInstance().getReference("inspections");

        //Adding a listener for retrieving data
        inspectionsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // CHANGED: Using Inspection_Report_class list
                List<Inspection_Report_class> inspectionList = new ArrayList<>();

                // check all inspections
                for (DataSnapshot ds : snapshot.getChildren()) {
                    // Automatic conversion of JSON from the cloud to Java object
                    // CHANGED: Converting to Inspection_Report_class
                    Inspection_Report_class report = ds.getValue(Inspection_Report_class.class);

                    if (report != null) {
                        inspectionList.add(report);
                    }
                }

                //Default Sort: Sort by Date (Newest First)
                Collections.sort(inspectionList, (r1, r2) -> {
                    String date1 = (r1.getDate() != null) ? r1.getDate() : "";
                    String date2 = (r2.getDate() != null) ? r2.getDate() : "";

                    int dateCompare = date2.compareTo(date1); // Descending order

                    // Tie-breaker sub-case: If dates are the same, sort alphabetically by name
                    if (dateCompare == 0) {
                        // CHANGED: Using getRestaurant_name()
                        String name1 = (r1.getRestaurant_name() != null) ? r1.getRestaurant_name() : "";
                        String name2 = (r2.getRestaurant_name() != null) ? r2.getRestaurant_name() : "";
                        return name1.compareTo(name2);
                    }
                    return dateCompare;
                });

                //Update the adapter with the sorted list
                // CHANGED: setRestaurants now accepts Inspection_Report_class list
                adapter.setRestaurants(inspectionList); // Note: Make sure Adapter method name matches (setInspections or setRestaurants)

                //Activate the "Sort by Grade" button
                Button btnSortGrade = view.findViewById(R.id.btn_sort_grade);
                if (btnSortGrade != null) {
                    btnSortGrade.setOnClickListener(v -> {
                        // This calls the logic we implemented in the Adapter earlier
                        adapter.sortByGrade();
                    });
                }
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