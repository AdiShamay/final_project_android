package com.example.final_project_android;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class inspection_details extends Fragment {

    // UI Components
    private TextView tvResName, tvAddress, tvDate, tvGrade, tvScore;
    private LinearLayout llItemsContainer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inspection_details, container, false);

        // Initialize UI References
        tvResName = view.findViewById(R.id.tv_detail_res_name);
        tvAddress = view.findViewById(R.id.tv_detail_address);
        tvDate = view.findViewById(R.id.tv_detail_date);
        tvGrade = view.findViewById(R.id.tv_detail_grade);
        tvScore = view.findViewById(R.id.tv_detail_score);
        llItemsContainer = view.findViewById(R.id.ll_items_container);

        ImageButton btnReturn = view.findViewById(R.id.btn_return);
        btnReturn.setOnClickListener(v -> Navigation.findNavController(v).popBackStack());

        // Get Report ID from Bundle
        if (getArguments() != null) {
            String reportId = getArguments().getString("report_id");
            if (reportId != null) {
                loadReportDetails(reportId);
            }
        }

        return view;
    }

    private void loadReportDetails(String reportId) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("inspections").child(reportId);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Inspection_Report_class report = snapshot.getValue(Inspection_Report_class.class);

                if (report != null) {
                    // Populate Header Data
                    tvResName.setText(report.getRestaurant_name());
                    tvAddress.setText(report.getRestaurant_address());
                    tvDate.setText(report.getDate());
                    tvScore.setText(report.getTotal_score() + " Pts");

                    // Set Grade with Color
                    String grade = report.getFinal_grade();
                    tvGrade.setText(grade);
                    if ("A".equals(grade)) {
                        tvGrade.setTextColor(Color.parseColor("#4CAF50")); // Green
                    } else if ("B".equals(grade)) {
                        tvGrade.setTextColor(Color.parseColor("#FFC107")); // Orange
                    } else {
                        tvGrade.setTextColor(Color.RED); // Red
                    }

                    // Populate List Dynamically
                    if (report.getInspection_items_list() != null) {
                        populateItemsList(report.getInspection_items_list());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error loading report", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Dynamically adds inspection items to the linear layout container
    private void populateItemsList(java.util.List<new_inspection_item> items) {
        // Clear previous views to prevent duplication
        llItemsContainer.removeAllViews();

        LayoutInflater inflater = LayoutInflater.from(getContext());

        for (new_inspection_item item : items) {
            // Inflate the XML layout for a single row
            View rowView = inflater.inflate(R.layout.item_detail_row, llItemsContainer, false);

            // Bind UI components
            TextView tvCategory = rowView.findViewById(R.id.tv_item_category);
            TextView tvDesc = rowView.findViewById(R.id.tv_item_description);
            TextView tvComments = rowView.findViewById(R.id.tv_item_comments);
            TextView tvPoints = rowView.findViewById(R.id.tv_item_points);

            // Set the category and description text using correct getters
            tvCategory.setText(item.getCategory());
            tvDesc.setText(item.getDescription());

            // Handle inspector comments visibility
            String notes = item.getComments();
            if (notes != null && !notes.trim().isEmpty()) {
                tvComments.setText("Note: " + notes);
                tvComments.setVisibility(View.VISIBLE);
            } else {
                tvComments.setVisibility(View.GONE);
            }

            // Set violation points text
            int points = item.getCurrentPoints();
            tvPoints.setText(points + " Violation Pts");

            // Apply text color based on points value
            if (points > 0) {
                tvPoints.setTextColor(Color.RED);
            } else {
                tvPoints.setTextColor(Color.parseColor("#4CAF50"));
            }

            // Add the row to the main container
            llItemsContainer.addView(rowView);

            // Add a divider line between items
            View divider = new View(getContext());
            divider.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 2));
            divider.setBackgroundColor(Color.parseColor("#EEEEEE"));
            llItemsContainer.addView(divider);
        }
    }
}