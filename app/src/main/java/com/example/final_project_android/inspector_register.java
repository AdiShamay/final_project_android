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
 * Handles the registration form for new Inspectors.
 * Returns to the login screen upon completion.
 */
public class inspector_register extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the registration layout
        View view = inflater.inflate(R.layout.fragment_inspector_register, container, false);

        // Find the "Complete Registration" button
        Button btnFinish = view.findViewById(R.id.btn_finish_inspector_reg);
        btnFinish.setOnClickListener(v -> {
            MainActivity mainActivity = (MainActivity) getActivity();
            if (mainActivity != null) {
                // It now only happens in MainActivity if the registration actually works.
                mainActivity.register_inspector();
            }
        });

        // Initialize the back button to return to the Inspector Login screen
        ImageButton btnReturn = view.findViewById(R.id.btn_return);
        btnReturn.setOnClickListener(v -> Navigation.findNavController(v).popBackStack());

        return view;
    }
}