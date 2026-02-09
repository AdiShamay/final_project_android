package com.example.final_project_android;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import androidx.fragment.app.Fragment;

/**
 * Fragment for registering a new restaurant.
 */
public class restaurant_register extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the registration layout
        View view = inflater.inflate(R.layout.fragment_restaurant_register, container, false);

        // Find the finish button
        Button btnFinish = view.findViewById(R.id.btn_finish_rest_reg);

        // Handle the submission action
        btnFinish.setOnClickListener(v -> {
            MainActivity mainActivity = (MainActivity) getActivity();
            if (mainActivity != null) {
                // It now only happens in MainActivity if the registration actually works.
                mainActivity.register_restaurant();
            }
        });
        return view;
        }
}