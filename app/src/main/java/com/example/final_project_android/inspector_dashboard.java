package com.example.final_project_android;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.cardview.widget.CardView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Main Dashboard for the Sanitation Inspector.
 * Handles fetching inspector details, finding the next upcoming inspection,
 * and navigation to various system modules.
 */
public class inspector_dashboard extends Fragment {

    // UI Components
    private TextView tvGreeting;
    private TextView tvNextResName, tvNextTime, tvNextAddress, tvNoInspection;
    private CardView cardNextInspection;
    private Button btnStartReportCard;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inspector_dashboard, container, false);

        // Initialize UI Elements
        tvGreeting = view.findViewById(R.id.tv_inspector_greeting);
        tvNextResName = view.findViewById(R.id.tv_next_restaurant_name);
        tvNextTime = view.findViewById(R.id.tv_next_time);
        tvNextAddress = view.findViewById(R.id.tv_next_address);
        tvNoInspection = view.findViewById(R.id.tv_no_inspection);
        cardNextInspection = view.findViewById(R.id.card_next_inspection);
        btnStartReportCard = view.findViewById(R.id.btn_start_report_card);

        // Load Inspector Data (Name + Next Inspection) from Firebase
        loadInspectorData();

        // Navigation Buttons Logic

        Button btnLogout = view.findViewById(R.id.btn_inspector_logout);
        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Navigation.findNavController(v).navigate(R.id.action_inspector_dashboard2_to_home2);
        });

        Button btnSchedule = view.findViewById(R.id.btn_view_schedule);
        btnSchedule.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_inspector_dashboard2_to_inspector_schedule2);
        });

        Button btnHistory = view.findViewById(R.id.btn_inspector_history);
        btnHistory.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_inspector_dashboard2_to_restaurant_reviews2);
        });

        Button btnMessages = view.findViewById(R.id.btn_inspector_messages);
        btnMessages.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_inspector_dashboard2_to_notifications2);
        });

        Button btnEdit = view.findViewById(R.id.btn_inspector_edit_profile);
        btnEdit.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_inspector_dashboard2_to_edit_inspector_profile2);
        });

        Button btnViewAll = view.findViewById(R.id.btn_view_all_reviews);
        btnViewAll.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_inspector_dashboard2_to_customer_feed2);
        });

        return view;
    }

    /**
     * Fetches the current inspector's details using their Auth Email.
     * Updates the greeting and triggers the search for the next inspection.
     */
    private void loadInspectorData() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) return;
        String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("inspectors");

        // Query by "email"
        ref.orderByChild("email").equalTo(userEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot child : snapshot.getChildren()) {
                        Inspector_class inspector = child.getValue(Inspector_class.class);
                        if (inspector != null) {
                            // Update Greeting with Full Name
                            tvGreeting.setText("Hello, " + inspector.getFull_name());

                            // Find the closest upcoming inspection using the Inspector's ID
                            findNextInspection(inspector.getID());
                        }
                    }
                } else {
                    tvGreeting.setText("Hello, Inspector");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error loading profile", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Searches for the nearest future inspection request for the given inspector ID.
     */
    private void findNextInspection(String inspectorId) {
        DatabaseReference requestsRef = FirebaseDatabase.getInstance().getReference("inspection_requests");

        requestsRef.orderByChild("inspector_id").equalTo(inspectorId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Inspection_Request_class closestRequest = null;
                Date closestDate = null;

                SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

                try {
                    Date now = new Date();
                    // Normalize 'now' to start of day for date comparison
                    Date todayZero = dateFormat.parse(dateFormat.format(now));

                    for (DataSnapshot child : snapshot.getChildren()) {
                        Inspection_Request_class req = child.getValue(Inspection_Request_class.class);

                        if (req != null && req.getRequested_date() != null) {
                            // Parse date and time from request
                            String timeStr = (req.getInspection_time() != null) ? req.getInspection_time() : "00:00";
                            Date reqFullDate = dateTimeFormat.parse(req.getRequested_date() + " " + timeStr);
                            Date reqDayOnly = dateFormat.parse(req.getRequested_date());

                            // Logic: Date must be today or in the future
                            if (reqFullDate != null && reqDayOnly != null && !reqDayOnly.before(todayZero)) {

                                // Find the request with the minimal time difference from now
                                if (closestDate == null || reqFullDate.before(closestDate)) {
                                    closestDate = reqFullDate;
                                    closestRequest = req;
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                updateDashboardUI(closestRequest);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    /**
     * Updates the Dashboard UI based on whether an upcoming inspection was found.
     */
    private void updateDashboardUI(Inspection_Request_class request) {
        if (request != null) {
            // Show Card, Hide "No Inspection" message
            cardNextInspection.setVisibility(View.VISIBLE);
            tvNoInspection.setVisibility(View.GONE);

            tvNextResName.setText(request.getRes_name());
            tvNextAddress.setText(request.getAddress());
            tvNextTime.setText(request.getRequested_date() + ", " + request.getInspection_time());

            // Set Action for the "Start" button inside the card
            btnStartReportCard.setOnClickListener(v -> {
                // Pass relevant data to the new inspection form
                Bundle bundle = new Bundle();
                bundle.putString("restaurant_id", request.getBusiness_id());
                bundle.putString("restaurant_name", request.getRes_name());

                Navigation.findNavController(v).navigate(R.id.action_inspector_dashboard2_to_new_inspection_form2, bundle);
            });

        } else {
            // Hide Card, Show "No Inspection" message
            cardNextInspection.setVisibility(View.GONE);
            tvNoInspection.setVisibility(View.VISIBLE);
        }
    }
}