package com.example.final_project_android;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import androidx.annotation.NonNull;

/**
 * Main Dashboard logic linking to management pages.
 */
public class restaurant_dashboard extends Fragment {

    private String currentBusinessId = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_restaurant_dashboard, container, false);

        // Initialize UI Elements for dynamic data
        TextView tvGreeting = view.findViewById(R.id.tv_greeting);
        TextView tvGrade = view.findViewById(R.id.tv_current_grade);
        TextView tvDate = view.findViewById(R.id.tv_grade_date);

        // Retrieve the email of the currently logged-in user
        String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("restaurants");

        // Query the database to find the specific restaurant associated with this email
        ref.orderByChild("email").equalTo(userEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Iterate through the results to find the matching restaurant
                    for (DataSnapshot child : snapshot.getChildren()) {
                        Restaurant_class restaurant = child.getValue(Restaurant_class.class);

                        if (restaurant != null) {
                            // Store the business ID for the history button logic
                            currentBusinessId = restaurant.getBusiness_id();

                            // Update the greeting text with the restaurant's name
                            tvGreeting.setText("Hello, " + restaurant.getRes_name());

                            // Update the grade TextView with color coding based on the score
                            String grade = restaurant.getHealth_score();
                            if (grade == null || grade.isEmpty()) {
                                tvGrade.setText("-");
                                tvGrade.setTextColor(android.graphics.Color.GRAY);
                            } else {
                                tvGrade.setText(grade);
                                if (grade.equals("A"))
                                    tvGrade.setTextColor(android.graphics.Color.parseColor("#4CAF50"));
                                else if (grade.equals("B"))
                                    tvGrade.setTextColor(android.graphics.Color.parseColor("#FFC107"));
                                else
                                    tvGrade.setTextColor(android.graphics.Color.RED);
                            }

                            // Update the last inspection date text
                            String date = restaurant.getDate();
                            if (date == null || date.isEmpty()) {
                                tvDate.setText("No Inspection Yet");
                            } else {
                                tvDate.setText("Last Inspection: " + date);
                            }
                        }
                    }
                } else {
                    // Handle scenario where no restaurant matches the email
                    tvGreeting.setText("Restaurant not found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database errors
                tvGreeting.setText("Error loading data");
            }
        });

        Button btnLogout = view.findViewById(R.id.btn_logout);
        Button btnRequestNew = view.findViewById(R.id.btn_request_inspection);
        Button btnHistory = view.findViewById(R.id.btn_view_my_history);
        Button btnEdit = view.findViewById(R.id.btn_restaurant_edit_profile);

        // Logout and return to home screen
        btnLogout.setOnClickListener(v -> {
            Navigation.findNavController(v)
                    .navigate(R.id.action_restaurant_dashboard2_to_home2);
        });

        // Request Inspection
        btnRequestNew.setOnClickListener(v -> {
            Navigation.findNavController(v)
                    .navigate(R.id.action_restaurant_dashboard2_to_inspection_request2);
        });

        // View History
        btnHistory.setOnClickListener(v -> {
            if (currentBusinessId != null && !currentBusinessId.isEmpty()) {
                Bundle bundle = new Bundle();
                // Pass "business_id" as type so the adapter knows how to filter
                bundle.putString("filterType", "business_id");
                // Pass the actual license number we fetched earlier
                bundle.putString("filterValue", currentBusinessId);

                Navigation.findNavController(v)
                        .navigate(R.id.action_restaurant_dashboard2_to_restaurant_reviews2, bundle);
            } else {
                // Handle case where data hasn't loaded yet
                android.widget.Toast.makeText(getContext(), "Loading data, please wait...", android.widget.Toast.LENGTH_SHORT).show();
            }
        });

        // Edit Profile
        btnEdit.setOnClickListener(v -> {
            Navigation.findNavController(v)
                    .navigate(R.id.action_restaurant_dashboard2_to_edit_restaurant_profile2);
        });

        return view;
    }
}