package com.example.final_project_android;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class inspection_request extends Fragment {

    // UI Components
    private TextView tvStatus, tvMessage;
    private ImageView ivIcon;
    private Button btnAction;
    private EditText etDate;
    private com.google.android.material.textfield.TextInputLayout dateLayout;

    // Firebase
    private DatabaseReference dbRef;
    private String currentUserEmail;

    // Data objects
    private Restaurant_class currentRestaurant;
    private Inspection_Request_class activeRequest;
    private String passedBusinessId = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inspection_request, container, false);

        // Initialize Views
        tvStatus = view.findViewById(R.id.tv_request_status);
        tvMessage = view.findViewById(R.id.tv_eligibility_message);
        btnAction = view.findViewById(R.id.btn_action_request);
        ivIcon = view.findViewById(R.id.iv_status_icon);
        etDate = view.findViewById(R.id.et_requested_date);
        ImageButton btnReturn = view.findViewById(R.id.btn_return);
        // Layout to handle icon clicks
        dateLayout = view.findViewById(R.id.textInputLayoutDate);
        // Initialize Firebase
        dbRef = FirebaseDatabase.getInstance().getReference();

        // Get the Business ID from the Bundle
        if (getArguments() != null) {
            passedBusinessId = getArguments().getString("business_id");
        }

        // Load data ONLY if we have the ID
        if (passedBusinessId != null && !passedBusinessId.isEmpty()) {
            loadRestaurantData();
        } else {
            Toast.makeText(getContext(), "Error: Business ID missing", Toast.LENGTH_SHORT).show();
        }

        // 1. Disable keyboard input
        etDate.setFocusable(false);
        etDate.setClickable(true);

        // 2. Open Calendar on click
        etDate.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();

            // Set minimum date to Tomorrow
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            long minDate = calendar.getTimeInMillis();

            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    getContext(),
                    (view1, selectedYear, selectedMonth, selectedDay) -> {
                        // Format the date properly (DD/MM/YYYY)
                        String selectedDate = String.format(Locale.getDefault(), "%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear);
                        etDate.setText(selectedDate);
                    },
                    year, month, day
            );

            // Restrict past dates in the dialog
            datePickerDialog.getDatePicker().setMinDate(minDate);
            datePickerDialog.show();
        });

        dateLayout.setEndIconOnClickListener(v -> etDate.performClick());

        // Submit Button Logic
        btnAction.setOnClickListener(v -> submitRequest());

        // Back Button Logic
        btnReturn.setOnClickListener(v -> Navigation.findNavController(v).popBackStack());

        return view;
    }

    // Query the database by the passed Business ID
    private void loadRestaurantData() {
        DatabaseReference restRef = FirebaseDatabase.getInstance().getReference("restaurants");

        // Search by "business_id"
        restRef.orderByChild("business_id").equalTo(passedBusinessId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            // Loop to find the match (should be only one)
                            for (DataSnapshot child : snapshot.getChildren()) {
                                currentRestaurant = child.getValue(Restaurant_class.class);
                            }
                            // Continue to next step
                            checkExistingRequests();
                        } else {
                            Toast.makeText(getContext(), "Restaurant not found in DB", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Connection Error", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Check for ANY active requests for this Business ID
    private void checkExistingRequests() {
        if (currentRestaurant == null) return;

        dbRef.child("inspection_requests").orderByChild("business_id").equalTo(currentRestaurant.getBusiness_id())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        activeRequest = null;

                        if (snapshot.exists()) {
                            for (DataSnapshot child : snapshot.getChildren()) {
                                Inspection_Request_class req = child.getValue(Inspection_Request_class.class);
                                if (req != null) {
                                    // Check if this request is still relevant (Date hasn't passed)
                                    if (isDateInFuture(req.getRequested_date())) {
                                        activeRequest = req;
                                        break; // Found an active request
                                    }
                                }
                            }
                        }
                        // Update UI based on what we found
                        updateUI();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });
    }

    // Logic Brain (Update UI States)
    private void updateUI() {
        // Handle cases where an active request exists in the system
        if (activeRequest != null) {

            // Check if an inspector has been assigned to this request
            if (activeRequest.getInspector_id() != null && !activeRequest.getInspector_id().trim().isEmpty()) {

                // Set UI for Scheduled state: Inspector has picked up the request
                ivIcon.setImageResource(android.R.drawable.ic_dialog_info);
                ivIcon.setColorFilter(0xFF2196F3); // Blue theme
                tvStatus.setText("Inspection Scheduled");
                tvMessage.setText("Inspector arriving on " + activeRequest.getRequested_date() +
                        " at " + activeRequest.getInspection_time());
            } else {

                // Set UI for Pending state: Request is live but waiting for an inspector
                ivIcon.setImageResource(android.R.drawable.ic_menu_agenda);
                ivIcon.setColorFilter(0xFFFFA000); // Orange theme
                tvStatus.setText("Request Pending");
                tvMessage.setText("Waiting for an inspector to accept your request for " + activeRequest.getRequested_date());
            }

            // Lock input fields and action button while a request is active
            etDate.setText(activeRequest.getRequested_date());
            etDate.setEnabled(false);
            if (dateLayout != null) dateLayout.setEndIconOnClickListener(null);
            btnAction.setText("Request Sent");
            btnAction.setEnabled(false);
            btnAction.setBackgroundColor(0xFFB0BEC5); // Neutral gray

        } else {
            // No active request found, proceed to check if the restaurant is eligible to create one
            checkEligibility();
        }
    }

    // Check 4 Month Rule
    private void checkEligibility() {
        String lastDateStr = currentRestaurant.getDate(); // Assuming format YYYY-MM-DD or DD/MM/YYYY

        // If no previous inspection, they are eligible
        if (lastDateStr == null || lastDateStr.isEmpty()) {
            setEligibleState();
            return;
        }

        try {
            // Handle date parsing (support both formats just in case)
            SimpleDateFormat sdf;
            if (lastDateStr.contains("-")) sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            else sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

            Date lastDate = sdf.parse(lastDateStr);
            Calendar cal = Calendar.getInstance();
            cal.setTime(lastDate);
            cal.add(Calendar.MONTH, 4); // Add 4 months

            Date eligibleDate = cal.getTime();
            Date today = new Date();

            if (today.before(eligibleDate)) {
                // LOCKED - Not yet 4 months
                SimpleDateFormat displayFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                setLockedState(displayFormat.format(eligibleDate));
            } else {
                // ELIGIBLE
                setEligibleState();
            }

        } catch (ParseException e) {
            // If date parse fails, assume eligible to avoid blocking user permanently
            setEligibleState();
        }
    }

    private void setLockedState(String availableDate) {
        ivIcon.setImageResource(android.R.drawable.ic_lock_idle_lock);
        ivIcon.setColorFilter(0xFFD32F2F); // Red
        tvStatus.setText("Not Available");
        tvStatus.setTextColor(0xFFD32F2F);
        tvMessage.setText("4 months required between inspections.\nYou can request a new inspection starting from: " + availableDate);

        etDate.setEnabled(false);
        if (dateLayout != null) dateLayout.setEndIconOnClickListener(null);
        etDate.setText("");
        btnAction.setEnabled(false);
        btnAction.setText("Not Available Yet");
        btnAction.setBackgroundColor(0xFFB0BEC5);
    }

    private void setEligibleState() {
        ivIcon.setImageResource(android.R.drawable.ic_input_add);
        ivIcon.setColorFilter(0xFF4CAF50); // Green
        tvStatus.setText("Request Inspection");
        tvStatus.setTextColor(0xFF4CAF50);
        tvMessage.setText("Please select a date (starting from tomorrow)");

        etDate.setEnabled(true);
        if (dateLayout != null) dateLayout.setEndIconOnClickListener(v -> etDate.performClick());
        etDate.setText("");
        btnAction.setEnabled(true);
        btnAction.setText("Submit Request");
        btnAction.setBackgroundColor(0xFF4CAF50);
    }

    // Submit to DB
    private void submitRequest() {
        String dateInput = etDate.getText().toString().trim();

        // Validate Date (Ensure not empty and future)
        if (dateInput.isEmpty() || !isValidFutureDate(dateInput)) {
            Toast.makeText(getContext(), "Please select a valid future date", Toast.LENGTH_LONG).show();
            return;
        }

        // Generate ID
        DatabaseReference reqRef = dbRef.child("inspection_requests");
        String requestID = reqRef.push().getKey();

        // Create Object
        Inspection_Request_class newRequest = new Inspection_Request_class(
                requestID,
                currentRestaurant.getBusiness_id(),
                currentRestaurant.getRes_name(),
                currentRestaurant.getAddress(),
                dateInput
        );

        // Write to DB
        reqRef.child(requestID).setValue(newRequest).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getContext(), "Request Sent Successfully!", Toast.LENGTH_SHORT).show();
                // Refresh UI
                checkExistingRequests();
            } else {
                Toast.makeText(getContext(), "Failed to send request.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Helper: Check if date string is in the future (Tomorrow onwards)
    private boolean isValidFutureDate(String dateStr) {
        if (dateStr.length() != 10) return false;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            sdf.setLenient(false); // Strict parsing
            Date inputDate = sdf.parse(dateStr);

            Calendar today = Calendar.getInstance();
            // Reset hours to compare only dates
            today.set(Calendar.HOUR_OF_DAY, 0); today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0); today.set(Calendar.MILLISECOND, 0);

            return inputDate != null && inputDate.after(today.getTime());
        } catch (ParseException e) {
            return false;
        }
    }

    // Helper: Check if existing request date is still valid (Today or Future)
    private boolean isDateInFuture(String dateStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date reqDate = sdf.parse(dateStr);

            // Compare with "Yesterday" essentially (if date is today, it is still active)
            Calendar yesterday = Calendar.getInstance();
            yesterday.add(Calendar.DAY_OF_YEAR, -1);

            return reqDate != null && reqDate.after(yesterday.getTime());
        } catch (ParseException e) {
            return false;
        }
    }
}