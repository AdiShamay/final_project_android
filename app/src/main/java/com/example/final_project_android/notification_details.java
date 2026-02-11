package com.example.final_project_android;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import android.widget.ImageButton;

/**
 * Displays the full content of a notification.
 */
public class notification_details extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate layout and store in a variable
        View view = inflater.inflate(R.layout.fragment_notification_details, container, false);

        // Initialize the professional back button
        ImageButton btnReturn = view.findViewById(R.id.btn_return);
        btnReturn.setOnClickListener(v -> {
            // Navigate back to the notifications list
            Navigation.findNavController(v).popBackStack();
        });

        return view;
    }
}