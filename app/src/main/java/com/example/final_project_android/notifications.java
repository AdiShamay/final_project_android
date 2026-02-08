package com.example.final_project_android;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

        // Create adapter with click listener
        NotificationsAdapter adapter = new NotificationsAdapter(() -> {
            // Navigate to detailed view
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new notification_details())
                    .addToBackStack(null)
                    .commit();
        });

        rvNotifications.setAdapter(adapter);

        return view;
    }
}