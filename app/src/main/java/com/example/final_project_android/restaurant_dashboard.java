package com.example.final_project_android;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.fragment.app.Fragment;

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

        // Logout
        btnLogout.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new home())
                    .commit();
        });

        // Request Inspection (Opens the new page)
        btnRequestNew.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new inspection_request())
                    .addToBackStack(null)
                    .commit();
        });

        // History (Reuses the existing list page - LOGIC APPROVED)
        btnHistory.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new restaurant_reviews())
                    .addToBackStack(null)
                    .commit();
        });

        // Edit Profile (Opens the new page)
        btnEdit.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new edit_restaurant_profile())
                    .addToBackStack(null)
                    .commit();
        });

        // Find the notifications button
        Button btnNotif = view.findViewById(R.id.btn_notifications);

        // Set Click Listener to open the new Notifications Fragment
        btnNotif.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new notifications())
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }
}