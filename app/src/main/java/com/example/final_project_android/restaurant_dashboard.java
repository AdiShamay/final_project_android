package com.example.final_project_android;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

/**
 * Main Dashboard logic linking to management pages.
 */
public class restaurant_dashboard extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_restaurant_dashboard, container, false);

        Button btnLogout = view.findViewById(R.id.btn_logout);
        Button btnRequestNew = view.findViewById(R.id.btn_request_inspection);
        Button btnHistory = view.findViewById(R.id.btn_view_my_history);
        Button btnEdit = view.findViewById(R.id.btn_edit_details);
        Button btnNotif = view.findViewById(R.id.btn_notifications);

        // Logout and return to home screen
        btnLogout.setOnClickListener(v -> {
            Navigation.findNavController(v)
                    .navigate(R.id.action_restaurant_dashboard2_to_home2);
        });

        // Request Inspection
        btnRequestNew.setOnClickListener(v -> {
            Navigation.findNavController(v)
                    .navigate(R.id.action_restaurant_dashboard2_to_inspection_request2);
        });

        // View History
        btnHistory.setOnClickListener(v -> {
            Navigation.findNavController(v)
                    .navigate(R.id.action_restaurant_dashboard2_to_restaurant_reviews2);
        });

        // Edit Profile
        btnEdit.setOnClickListener(v -> {
            Navigation.findNavController(v)
                    .navigate(R.id.action_restaurant_dashboard2_to_edit_restaurant_profile2);
        });

        // Notifications
        btnNotif.setOnClickListener(v -> {
            Navigation.findNavController(v)
                    .navigate(R.id.action_restaurant_dashboard2_to_notifications2);
        });

        return view;
    }
}