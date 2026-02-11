package com.example.final_project_android;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import android.widget.ImageButton;

/**
 * Fragment showing the specific details of a single inspection.
 * Reached by clicking a row in the history list.
 */
public class review_details extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the details layout
        // Inflate the layout and store it in a variable to find internal views
        View view = inflater.inflate(R.layout.fragment_review_details, container, false);

        // Initialize the back button and set it to navigate to the previous screen
        ImageButton btnReturn = view.findViewById(R.id.btn_return);
        btnReturn.setOnClickListener(v -> Navigation.findNavController(v).popBackStack());

        return view;
    }
}