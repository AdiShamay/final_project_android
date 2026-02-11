package com.example.final_project_android;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

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
        Button btnInspector = view.findViewById(R.id.btn_inspector);
        Button btnCustomer = view.findViewById(R.id.btn_customer);

        // Navigate to Restaurant Login using the specific NavGraph action ID
        btnRestaurant.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_home2_to_restaurant_login)
        );

        // Navigate to Inspector Login using the specific NavGraph action ID
        btnInspector.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_home2_to_inspector_login)
        );

        // Navigate to Customer Feed using the specific NavGraph action ID
        btnCustomer.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_home2_to_customer_feed2)
        );

        return view;
    }
    
}