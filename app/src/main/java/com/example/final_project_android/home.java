package com.example.final_project_android;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.fragment.app.Fragment;

/**
 * Main fragment that directs users to Restaurant, Inspector, or Customer pages.
 */
public class home extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Find the buttons in the layout
        Button btnRestaurant = view.findViewById(R.id.btn_restaurant);
        Button btnInspector = view.findViewById(R.id.btn_inspector); // Changed from btn_critic
        Button btnCustomer = view.findViewById(R.id.btn_customer);

        // Set listener to navigate to Restaurant Login
        btnRestaurant.setOnClickListener(v -> navigateTo(new restaurant_login()));

        // Set listener to navigate to Inspector Login
        btnInspector.setOnClickListener(v -> navigateTo(new inspector_login()));

        // Set listener to navigate to Customer Feed (Grades view)
        btnCustomer.setOnClickListener(v -> navigateTo(new customer_feed()));

        return view;
    }

    // Helper method to switch fragments and add to back stack
    private void navigateTo(Fragment fragment) {
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }
}