package com.example.final_project_android;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.navigation.Navigation;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class filtered_inspections extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_filtered_inspections, container, false);

        // Reference the target name TextView for dynamic title updates
        TextView tvTargetName = view.findViewById(R.id.tv_target_name);

        // Setup the RecyclerView with the updated adapter and navigation logic
        RecyclerView rvReviews = view.findViewById(R.id.rv_reviews_history);
        rvReviews.setLayoutManager(new LinearLayoutManager(getContext()));

        // Receiver for the clicked report ID to perform navigation
        FilteredInspectionsAdapter adapter = new FilteredInspectionsAdapter(reportId -> {
            Bundle bundle = new Bundle();
            bundle.putString("report_id", reportId);

            // Navigate to the inspection details screen
            Navigation.findNavController(view).navigate(R.id.action_restaurant_reviews2_to_review_details2, bundle);
        });
        rvReviews.setAdapter(adapter);

        // Process arguments received from the previous fragment
        if (getArguments() != null) {
            String filterType = getArguments().getString("filterType");
            String filterValue = getArguments().getString("filterValue");

            if ("inspector_id".equals(filterType)) {
                String inspectorName = getArguments().getString("inspector_name");
                if (inspectorName != null) tvTargetName.setText(inspectorName);
            }

            if ("business_id".equals(filterType)) {
                String resName = getArguments().getString("restaurant_name");
                if (resName != null) tvTargetName.setText(resName);
            }

            if (filterValue != null) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("inspections");
                Query query;

                // Build query based on inspector ID or business ID provided in the bundle
                if ("inspector_id".equals(filterType)) {
                    // Filter reviews based on the unique inspector ID (TZ)
                    query = ref.orderByChild("inspector_id").equalTo(filterValue);
                } else if ("business_id".equals(filterType)) {
                    // Filter reviews based on the unique business license number
                    query = ref.orderByChild("business_id").equalTo(filterValue);
                } else {
                    query = ref;
                }

                // Attach listener to fetch and sort data from Firebase
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<Inspection_Report_class> inspectionsList = new ArrayList<>();

                        if (snapshot.exists()) {
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                Inspection_Report_class report = ds.getValue(Inspection_Report_class.class);
                                if (report != null) {
                                    inspectionsList.add(report);
                                }
                            }

                            // Sort the retrieved data to ensure chronological order (newest first)
                            Collections.sort(inspectionsList, (r1, r2) -> {
                                String d1 = r1.getDate() != null ? r1.getDate() : "";
                                String d2 = r2.getDate() != null ? r2.getDate() : "";
                                return d2.compareTo(d1);
                            });
                        }
                        // Refresh the adapter with the processed list
                        adapter.setInspections(inspectionsList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

        // Return button functionality using the navigation back stack
        ImageButton btnReturn = view.findViewById(R.id.btn_return);
        btnReturn.setOnClickListener(v -> Navigation.findNavController(v).popBackStack());

        return view;
    }
}