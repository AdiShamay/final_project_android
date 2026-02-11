package com.example.final_project_android;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import android.widget.ImageButton;

/**
 * Fragment for editing restaurant details.
 */
public class edit_restaurant_profile extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_restaurant_profile, container, false);

        Button btnSave = view.findViewById(R.id.btn_save_changes);

        // Handle the Save button click
        btnSave.setOnClickListener(v -> {
            // Save logic to MongoDB will go here
            Toast.makeText(getActivity(), "Changes Saved!", Toast.LENGTH_SHORT).show();

            // Navigate back to Dashboard using the NavGraph
            Navigation.findNavController(v).popBackStack();
        });

        // Initialize the professional back button
        ImageButton btnReturn = view.findViewById(R.id.btn_return);
        btnReturn.setOnClickListener(v -> {
            // Navigate back to Dashboard without saving changes
            Navigation.findNavController(v).popBackStack();
        });

        return view;
    }
}