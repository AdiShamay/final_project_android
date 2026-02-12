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

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullUnmarked;

import java.util.ArrayList;
import java.util.Collection;
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
        RestaurantAdapter adapter = new RestaurantAdapter(restaurantName -> {
            // Navigate to the reviews page using the ID from nav_graph
            Navigation.findNavController(view).navigate(R.id.action_customer_feed2_to_restaurant_reviews2);
        });

        // Attach the adapter to the RecyclerView
        rvRestaurants.setAdapter(adapter);

        // Obtaining a reference to the DB (restaurants)
        DatabaseReference restaurantsRef = FirebaseDatabase.getInstance().getReference("restaurants");

        //Adding a listener for retrieving data
        restaurantsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Restaurant_class> restaurantList = new ArrayList<>();
                // check all restaurants
                for (DataSnapshot ds : snapshot.getChildren()) {
                    // Automatic conversion of JSON from the cloud to Java object
                    Restaurant_class restaurant = ds.getValue(Restaurant_class.class);

                    if (restaurant != null) {
                        restaurantList.add(restaurant);
                    }
                }
                //Default Sort: Sort by Date (Newest First)
                Collections.sort(restaurantList, (r1, r2) -> {
                    String date1 = (r1.getDate() != null) ? r1.getDate() : "";
                    String date2 = (r2.getDate() != null) ? r2.getDate() : "";

                    int dateCompare = date2.compareTo(date1); // Descending order

                    // Tie-breaker sub-case: If dates are the same, sort alphabetically by name
                    if (dateCompare == 0) {
                        String name1 = (r1.getRes_name() != null) ? r1.getRes_name() : "";
                        String name2 = (r2.getRes_name() != null) ? r2.getRes_name() : "";
                        return name1.compareTo(name2);
                    }
                    return dateCompare;
                });

                //Update the adapter with the sorted list
                adapter.setRestaurants(restaurantList);

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