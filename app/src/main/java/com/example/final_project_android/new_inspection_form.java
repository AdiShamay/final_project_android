package com.example.final_project_android;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class new_inspection_form extends Fragment {

    private InspectionAdapter adapter;
    private TextView tvTotal;
    private EditText etBusinessId,etResName;
    // Firebase variables
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference inspectionsRef;

    // Global list to hold data for submission
    private List<new_inspection_item> inspectionList;

    private String restaurantAddress = "";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_inspection_form, container, false);

        // Initialize Firebase instances
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        inspectionsRef = database.getReference("inspections");

        // Initialize UI components
        etResName = view.findViewById(R.id.et_restaurant_name);
        etBusinessId = view.findViewById(R.id.et_business_id);
        tvTotal = view.findViewById(R.id.tv_total_score);
        RecyclerView rvItems = view.findViewById(R.id.rv_inspection_items);
        Button btnSubmit = view.findViewById(R.id.btn_submit_inspection);
        ImageButton btnReturn = view.findViewById(R.id.btn_return);

        // Retrieve the data passed from the Dashboard
        String passedName = "";
        String passedId = "";

        if (getArguments() != null) {
            passedName = getArguments().getString("restaurant_name", "");
            passedId = getArguments().getString("restaurant_id", "");
            restaurantAddress = getArguments().getString("restaurant_address", "");
        }

        // Set the text and ensure they are locked
        etResName.setText(passedName);
        etBusinessId.setText(passedId);

        // Setup RecyclerView layout
        rvItems.setLayoutManager(new LinearLayoutManager(getContext()));
        inspectionList = new ArrayList<>();

        // Populate list with items
        inspectionList.add(new new_inspection_item("1. FOOD PROTECTION", "Cold food item held above 5°C"));
        inspectionList.add(new new_inspection_item("1. FOOD PROTECTION", "Hot food item not held at or above 60°C"));
        inspectionList.add(new new_inspection_item("1. FOOD PROTECTION", "Food not cooled by an approved method"));
        inspectionList.add(new new_inspection_item("2. VERMIN & PESTS", "Evidence of mice or live mice present"));
        inspectionList.add(new new_inspection_item("2. VERMIN & PESTS", "Evidence of roaches or live roaches present"));
        inspectionList.add(new new_inspection_item("3. PERSONAL HYGIENE", "Food worker does not wash hands thoroughly"));
        inspectionList.add(new new_inspection_item("3. PERSONAL HYGIENE", "Food worker contacting food with bare hands"));
        inspectionList.add(new new_inspection_item("4. MAINTENANCE", "Plumbing not properly installed"));
        inspectionList.add(new new_inspection_item("4. MAINTENANCE", "Non-food contact surface not clean"));

        // Initialize Adapter with listener
        adapter = new InspectionAdapter(inspectionList, () -> {
            updateTotalScoreUI();
        });
        rvItems.setAdapter(adapter);
        updateTotalScoreUI();

        // Handle Submit button click
        btnSubmit.setOnClickListener(v -> {

            String Restaurant_Name = etResName.getText().toString().trim();
            String businessIdInput = etBusinessId.getText().toString().trim();

            // Basic safety check in case navigation went wrong
            if (Restaurant_Name.isEmpty() || businessIdInput.isEmpty()) {
                Toast.makeText(getContext(), "Error: Missing restaurant data", Toast.LENGTH_SHORT).show();
                return;
            }

            // Get current user email or default
            String Inspector_Email = "";
            if (mAuth.getCurrentUser() != null) {
                Inspector_Email = mAuth.getCurrentUser().getEmail();
            } else {
                Toast.makeText(getContext(), "Error: Missing Inspector data", Toast.LENGTH_SHORT).show();
                return;
            }

            // Get current date formatted
            String Current_Date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

            // Generate unique key in Firebase
            String Report_ID = inspectionsRef.push().getKey();

            int Total_Score = adapter.calculateTotalPoints();
            String Final_Grade = getGradeFromPoints(Total_Score);

            // Write to database if ID is valid
            if (Report_ID != null) {
                // Create the Inspection Report
                writeToDBInspection(Report_ID, businessIdInput, Inspector_Email, Restaurant_Name, restaurantAddress, Current_Date, Total_Score, Final_Grade, inspectionList);

                // Delete the open request for this restaurant
                deleteRequestByBusinessId(businessIdInput);

                Toast.makeText(getActivity(), "Inspection Saved Successfully!", Toast.LENGTH_SHORT).show();

                // Navigate back
                Navigation.findNavController(v).popBackStack();
            }
        });

        // Handle Back button click
        btnReturn.setOnClickListener(v -> {
            Navigation.findNavController(v).popBackStack();
        });

        return view;
    }

    // Function to write report object to Firebase
    public void writeToDBInspection(String Report_ID, String Business_ID, String Inspector_Email, String Restaurant_Name, String Restaurant_Address, String Date, int Total_Score, String Final_Grade, List<new_inspection_item> Items) {
        DatabaseReference myRef = database.getReference("inspections").child(Report_ID);
        Inspection_Report_class report = new Inspection_Report_class(Report_ID, Business_ID, Inspector_Email, Restaurant_Name, Restaurant_Address, Date, Total_Score, Final_Grade, Items);
        myRef.setValue(report);

        //updating the restaurants DB to the newest grade and date
        DatabaseReference resRef = database.getReference("restaurants").child(Business_ID);
        java.util.HashMap<String, Object> updates = new java.util.HashMap<>();
        updates.put("health_score", Final_Grade);
        updates.put("date", Date);
        resRef.updateChildren(updates);
    }

    // Helper to update the score text view
    private void updateTotalScoreUI() {
        if (adapter == null) return;
        int totalPoints = adapter.calculateTotalPoints();
        String grade = getGradeFromPoints(totalPoints);

        int color;
        if (grade.equals("A")) color = android.graphics.Color.parseColor("#4CAF50");
        else if (grade.equals("B")) color = android.graphics.Color.parseColor("#FFC107");
        else color = android.graphics.Color.parseColor("#D32F2F");

        tvTotal.setText("Grade: " + grade + " (" + totalPoints + " pts)");
        tvTotal.setTextColor(color);
    }

    // Helper to calculate grade letter
    private String getGradeFromPoints(int points) {
        if (points <= 13) return "A";
        else if (points <= 27) return "B";
        else return "C";
    }

    // Helper function to find and delete the open request for this business
    private void deleteRequestByBusinessId(String businessId) {
        DatabaseReference requestsRef = database.getReference("inspection_requests");

        // Query finding the request with this business_id
        requestsRef.orderByChild("business_id").equalTo(businessId)
                .addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
                    @Override
                    public void onDataChange(@androidx.annotation.NonNull com.google.firebase.database.DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (com.google.firebase.database.DataSnapshot child : snapshot.getChildren()) {
                                // Delete the request node
                                child.getRef().removeValue();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {
                        // Log error if needed
                    }
                });
    }
}