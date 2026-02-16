package com.example.final_project_android;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

// Fragment that displays and manages the inspector's personal schedule
public class inspector_schedule extends Fragment {

    private RecyclerView rvList;
    private ScheduledInspectionsAdapter adapter;
    private List<Inspection_Request_class> scheduledList;
    private SimpleDateFormat sdf;
    private String currentInspectorId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inspector_schedule, container, false);

        // Initialize UI components
        rvList = view.findViewById(R.id.rv_schedule_list);
        Button btnAddSchedule = view.findViewById(R.id.btn_add_schedule);
        ImageButton btnReturn = view.findViewById(R.id.btn_return);

        // Initialize data structures
        scheduledList = new ArrayList<>();
        sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        if (getArguments() != null) {
            currentInspectorId = getArguments().getString("inspector_id");
        }

        rvList.setLayoutManager(new LinearLayoutManager(getContext()));

        // Setup adapter with action listeners
        adapter = new ScheduledInspectionsAdapter(scheduledList, new ScheduledInspectionsAdapter.OnItemActionListener() {
            @Override
            public void onCancelClick(Inspection_Request_class request) {
                showCancelConfirmationDialog(request);
            }

            @Override
            public void onRescheduleClick(Inspection_Request_class request) {
                // validation happens inside the picker
                showTimePickerDialog(request);
            }
        });
        rvList.setAdapter(adapter);

        // Check if inspector ID was passed from Dashboard
        if (getArguments() != null) {
            String inspectorId = getArguments().getString("inspector_id");
            if (inspectorId != null) {
                // Load schedule directly using the ID
                fetchRequestsForInspector(inspectorId);
            }
        } else {
            // Error
            Toast.makeText(getContext(), "Error: Inspector ID not found.", Toast.LENGTH_SHORT).show();
        }

        // Navigation logic

        btnAddSchedule.setOnClickListener(v -> {
            if (currentInspectorId != null) {
                Bundle bundle = new Bundle();
                bundle.putString("inspector_id", currentInspectorId);
                Navigation.findNavController(v).navigate(R.id.action_inspector_schedule2_to_add_to_schedule, bundle);
            } else {
                Navigation.findNavController(v).navigate(R.id.action_inspector_schedule2_to_add_to_schedule);
            }
        });

        btnReturn.setOnClickListener(v ->
                Navigation.findNavController(v).popBackStack());

        return view;
    }

    // Fetches future inspections for the specific inspector ID
    private void fetchRequestsForInspector(String myInspectorId) {
        DatabaseReference requestsRef = FirebaseDatabase.getInstance().getReference("inspection_requests");

        requestsRef.orderByChild("inspector_id").equalTo(myInspectorId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                scheduledList.clear();
                Date today = getTodayDateZeroTime();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    Inspection_Request_class req = ds.getValue(Inspection_Request_class.class);
                    if (req != null && req.getRequested_date() != null) {
                        try {
                            Date reqDate = sdf.parse(req.getRequested_date());
                            // Filter: show only today or future dates
                            if (reqDate != null && !reqDate.before(today)) {
                                scheduledList.add(req);
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }

                // Sort the list by date (earliest first), and then by time if dates are equal
                java.util.Collections.sort(scheduledList, (r1, r2) -> {
                    try {
                        Date d1 = sdf.parse(r1.getRequested_date());
                        Date d2 = sdf.parse(r2.getRequested_date());

                        // Compare dates first
                        if (d1 != null && d2 != null) {
                            int dateResult = d1.compareTo(d2);
                            if (dateResult != 0) return dateResult; // Return result if dates are different
                        }

                        // If dates are the same, compare inspection times string
                        String t1 = r1.getInspection_time() != null ? r1.getInspection_time() : "";
                        String t2 = r2.getInspection_time() != null ? r2.getInspection_time() : "";
                        return t1.compareTo(t2);

                    } catch (ParseException e) {
                        return 0;
                    }
                });

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error loading schedule", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Cancels the meeting by clearing inspector details in DB
    private void cancelMeeting(Inspection_Request_class request) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("inspection_requests");

        request.setInspector_id("");
        request.setInspection_time("");

        ref.child(request.getRequest_uid()).setValue(request)
                .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Inspection Cancelled", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to cancel", Toast.LENGTH_SHORT).show());
    }

    // Updates the inspection time in DB
    private void updateMeetingTime(Inspection_Request_class request, String newTime) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("inspection_requests");

        request.setInspection_time(newTime);

        ref.child(request.getRequest_uid()).setValue(request)
                .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Time Updated!", Toast.LENGTH_SHORT).show());
    }

    // Shows confirmation dialog before cancellation
    private void showCancelConfirmationDialog(Inspection_Request_class request) {
        new AlertDialog.Builder(getContext())
                .setTitle("Cancel Inspection")
                .setMessage("Are you sure you want to cancel this inspection?\nIt will be removed from your schedule.")
                .setPositiveButton("Yes, Cancel", (dialog, which) -> cancelMeeting(request))
                .setNegativeButton("No", null)
                .show();
    }

    // Shows TimePicker dialog with validation for current day
    private void showTimePickerDialog(Inspection_Request_class request) {
        int hour = 12, minute = 0;

        // Parse current scheduled time to show as default
        if (request.getInspection_time() != null && request.getInspection_time().contains(":")) {
            String[] parts = request.getInspection_time().split(":");
            hour = Integer.parseInt(parts[0]);
            minute = Integer.parseInt(parts[1]);
        }

        TimePickerDialog mTimePicker = new TimePickerDialog(getContext(), (timePicker, selectedHour, selectedMinute) -> {

            // Logic: If the inspection is TODAY, ensure the selected time is in the future
            if (isToday(request.getRequested_date())) {
                java.util.Calendar now = java.util.Calendar.getInstance();
                int currentHour = now.get(java.util.Calendar.HOUR_OF_DAY);
                int currentMinute = now.get(java.util.Calendar.MINUTE);

                // Check if selected time is earlier than current time
                if (selectedHour < currentHour || (selectedHour == currentHour && selectedMinute <= currentMinute)) {
                    Toast.makeText(getContext(), "Please select a future time", Toast.LENGTH_LONG).show();
                    return; // Stop here, do not update DB
                }
            }

            String newTime = String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute);
            updateMeetingTime(request, newTime);

        }, hour, minute, true); // true for 24 hour format

        mTimePicker.setTitle("Update Time");
        mTimePicker.show();
    }

    // Helper to get today's date at 00:00:00 for accurate comparison
    private Date getTodayDateZeroTime() {
        try {
            Date now = new Date();
            return sdf.parse(sdf.format(now));
        } catch (ParseException e) {
            return new Date();
        }
    }

    // Helper to check if a specific date string matches today's date
    private boolean isToday(String dateString) {
        try {
            Date dateToCheck = sdf.parse(dateString);
            Date today = getTodayDateZeroTime();
            return dateToCheck != null && dateToCheck.equals(today);
        } catch (ParseException e) {
            return false;
        }
    }
}