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
 * Fragment displaying list of notifications.
 * Navigates to details on click.
 */
public class notifications extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);

        RecyclerView rvNotifications = view.findViewById(R.id.rv_notifications_list);
        rvNotifications.setLayoutManager(new LinearLayoutManager(getContext()));

        // Create adapter with NavGraph click listener
        NotificationsAdapter adapter = new NotificationsAdapter(() -> {
            // Navigate to notification details using the NavGraph action
            Navigation.findNavController(view).navigate(R.id.action_notifications2_to_notification_details2);
        });

        rvNotifications.setAdapter(adapter);

        // Initialize the professional back button
        ImageButton btnReturn = view.findViewById(R.id.btn_return);
        btnReturn.setOnClickListener(v -> {
            // Navigate back to the Dashboard
            Navigation.findNavController(v).popBackStack();
        });

        return view;
    }
}