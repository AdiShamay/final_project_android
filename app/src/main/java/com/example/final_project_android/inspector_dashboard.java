package com.example.final_project_android;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import android.widget.ImageButton;

/**
 * Main Dashboard for the Sanitation Inspector.
 * Provides access to Schedule, History, New Reports, and Profile.
 */
public class inspector_dashboard extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the dashboard layout
        View view = inflater.inflate(R.layout.fragment_inspector_dashboard, container, false);

        // Header and Logout logic
        TextView tvGreeting = view.findViewById(R.id.tv_inspector_greeting);
        tvGreeting.setText("Hello, Inspector Cohen");

        Button btnLogout = view.findViewById(R.id.btn_inspector_logout);
        btnLogout.setOnClickListener(v -> {
            Navigation.findNavController(v)
                    .navigate(R.id.action_inspector_dashboard2_to_home2);
        });

        // Start New Inspection
        Button btnNew = view.findViewById(R.id.btn_start_new_inspection);
        btnNew.setOnClickListener(v -> {
            Navigation.findNavController(v)
                    .navigate(R.id.action_inspector_dashboard2_to_new_inspection_form2);
        });

        // View Schedule
        Button btnSchedule = view.findViewById(R.id.btn_view_schedule);
        btnSchedule.setOnClickListener(v -> {
            Navigation.findNavController(v)
                    .navigate(R.id.action_inspector_dashboard2_to_inspector_schedule2);
        });

        // Past History
        Button btnHistory = view.findViewById(R.id.btn_inspector_history);
        btnHistory.setOnClickListener(v -> {
            Navigation.findNavController(v)
                    .navigate(R.id.action_inspector_dashboard2_to_restaurant_reviews2);
        });

        // Messages
        Button btnMessages = view.findViewById(R.id.btn_inspector_messages);
        btnMessages.setOnClickListener(v -> {
            Navigation.findNavController(v)
                    .navigate(R.id.action_inspector_dashboard2_to_notifications2);
        });

        // Edit Profile
        Button btnEdit = view.findViewById(R.id.btn_inspector_edit_profile);
        btnEdit.setOnClickListener(v -> {
            Navigation.findNavController(v)
                    .navigate(R.id.action_inspector_dashboard2_to_edit_inspector_profile2);
        });

        Button btnViewAll = view.findViewById(R.id.btn_view_all_reviews);
        btnViewAll.setOnClickListener(v -> {
            Navigation.findNavController(v)
                    .navigate(R.id.action_inspector_dashboard2_to_customer_feed2);
        });

        return view;
    }
}