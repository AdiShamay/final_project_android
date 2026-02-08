package com.example.final_project_android;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;

/**
 * Fragment to fill out a new sanitation inspection report.
 */
public class new_inspection_form extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_inspection_form, container, false);

        // Setup the dropdown (Spinner) with dummy data
        Spinner spinner = view.findViewById(R.id.spinner_restaurants);
        // Create an array of restaurants
        String[] restaurants = new String[]{"Select Restaurant...", "Pizza Place", "Sushi TLV", "Burger King", "Cafe Hillel"};
        // Create an adapter for the spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, restaurants);
        spinner.setAdapter(adapter);

        // Handle Submit Button
        Button btnSubmit = view.findViewById(R.id.btn_submit_inspection);
        TextView tvTotal = view.findViewById(R.id.tv_total_score);

        btnSubmit.setOnClickListener(v -> {
            // Here you would gather all scores, calculate sum, and save to DB

            // Simulation:
            tvTotal.setText("95"); // Pretend we calculated it
            Toast.makeText(getActivity(), "Inspection Report Submitted!", Toast.LENGTH_SHORT).show();

            // Return to dashboard
            getParentFragmentManager().popBackStack();
        });

        return view;
    }
}