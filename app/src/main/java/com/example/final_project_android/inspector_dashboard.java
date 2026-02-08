package com.example.final_project_android;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.fragment.app.Fragment;

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
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new home())
                    .commit();
        });

        // Start New Inspection (Updated Link)
        Button btnNew = view.findViewById(R.id.btn_start_new_inspection);
        btnNew.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new new_inspection_form())
                    .addToBackStack(null)
                    .commit();
        });

        // View Schedule (Updated Link)
        Button btnSchedule = view.findViewById(R.id.btn_view_schedule);
        btnSchedule.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new inspector_schedule())
                    .addToBackStack(null)
                    .commit();
        });

        // Past History
        Button btnHistory = view.findViewById(R.id.btn_inspector_history);
        btnHistory.setOnClickListener(v -> {
            // Reusing the restaurant reviews fragment to show history
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new restaurant_reviews())
                    .addToBackStack(null)
                    .commit();
        });

        // Messages
        Button btnMessages = view.findViewById(R.id.btn_inspector_messages);
        btnMessages.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new notifications())
                    .addToBackStack(null)
                    .commit();
        });

        // Edit Profile
        Button btnEdit = view.findViewById(R.id.btn_inspector_edit_profile);
        btnEdit.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new edit_inspector_profile())
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }
}