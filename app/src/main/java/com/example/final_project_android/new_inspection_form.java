package com.example.final_project_android;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import android.widget.ImageButton;

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

        // Initialize EditTexts for NYC violation categories
        EditText etScore1 = view.findViewById(R.id.et_score_kitchen_hygiene);
        EditText etScore2 = view.findViewById(R.id.et_score_food_temp);
        EditText etScore3 = view.findViewById(R.id.et_score_personal_hygiene);
        EditText etScore4 = view.findViewById(R.id.et_score_pest_control);
        EditText etScore5 = view.findViewById(R.id.et_score_maintenance);

        // Submit button
        btnSubmit.setOnClickListener(v -> {
            try {
                // 1. Extract violation points from EditText fields
                // In NYC grading, higher points mean more violations
                int p1 = Integer.parseInt(etScore1.getText().toString());
                int p2 = Integer.parseInt(etScore2.getText().toString());
                int p3 = Integer.parseInt(etScore3.getText().toString());
                int p4 = Integer.parseInt(etScore4.getText().toString());
                int p5 = Integer.parseInt(etScore5.getText().toString());

                // 2. Calculate the sum of violation points
                int totalPoints = p1 + p2 + p3 + p4 + p5;

                // 3. Determine the final grade based on NYC Department of Health thresholds
                // 0-13 points: A
                // 14-27 points: B
                // 28+ points: C
                String grade;
                int color;

                if (totalPoints <= 13) {
                    grade = "A";
                    color = android.graphics.Color.parseColor("#4CAF50"); // Green for good standing
                } else if (totalPoints <= 27) {
                    grade = "B";
                    color = android.graphics.Color.parseColor("#FFC107"); // Yellow/Orange for improvements needed
                } else {
                    grade = "C";
                    color = android.graphics.Color.parseColor("#D32F2F"); // Red for serious violations
                }

                // 4. Update the UI with the calculated grade and total points
                tvTotal.setText("Grade: " + grade + " (" + totalPoints + " pts)");
                tvTotal.setTextColor(color);

                // 5. Provide visual feedback and navigate back to the dashboard
                Toast.makeText(getActivity(), "Inspection Submitted: Grade " + grade, Toast.LENGTH_LONG).show();

                androidx.navigation.Navigation.findNavController(v).popBackStack();

            } catch (NumberFormatException e) {
                // Handle cases where fields are empty or contain invalid numbers
                Toast.makeText(getActivity(), "Please enter points for all categories", Toast.LENGTH_SHORT).show();
            }
        });

        // Return button
        ImageButton btnReturn = view.findViewById(R.id.btn_return);
        btnReturn.setOnClickListener(v -> {
            Navigation.findNavController(v).popBackStack();
        });

        return view;
    }
}