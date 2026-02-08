package com.example.final_project_android;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import androidx.fragment.app.Fragment;

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
        btnFinish.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            MainActivity mainActivity =(MainActivity) getActivity();
            mainActivity.register_inspector();
            // Show a success message
            Toast.makeText(getActivity(), "Inspector application submitted!", Toast.LENGTH_SHORT).show();
        }
    });
        return view;
    }
}