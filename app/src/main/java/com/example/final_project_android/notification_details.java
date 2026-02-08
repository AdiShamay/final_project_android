package com.example.final_project_android;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;

/**
 * Displays the full content of a notification.
 */
public class notification_details extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate layout
        return inflater.inflate(R.layout.fragment_notification_details, container, false);
    }
}