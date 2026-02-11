package com.example.final_project_android;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

/**
 * Fragment for editing Inspector details.
 */
public class edit_inspector_profile extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_inspector_profile, container, false);

        // Find views
        Button btnSave = view.findViewById(R.id.btn_insp_save);
        ImageButton btnReturn = view.findViewById(R.id.btn_return);

        // Handle the Save button click
        btnSave.setOnClickListener(v -> {
            // Save logic to MongoDB will go here
            Toast.makeText(getActivity(), "Inspector Profile Updated!", Toast.LENGTH_SHORT).show();

            // Navigate back to Inspector Dashboard using NavGraph
            Navigation.findNavController(v).popBackStack();
        });

        // Handle the professional back button (Back Arrow)
        btnReturn.setOnClickListener(v -> {
            // Navigate back to Dashboard without saving changes
            Navigation.findNavController(v).popBackStack();
        });

        return view;
    }
}