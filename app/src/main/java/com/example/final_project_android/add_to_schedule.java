package com.example.final_project_android;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
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
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class add_to_schedule extends Fragment {

    private RecyclerView recyclerView;
    private AvailableRequestsAdapter adapter;
    private List<Inspection_Request_class> availableList;
    private String currentInspectorId = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_to_schedule, container, false);

        recyclerView = view.findViewById(R.id.rv_available_requests);
        ImageButton btnReturn = view.findViewById(R.id.btn_return);
        SearchView searchView = view.findViewById(R.id.sv_requests_search);

        // Initialize list
        availableList = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        if (getArguments() != null) {
            currentInspectorId = getArguments().getString("inspector_id", "");
        }

        // Setup adapter with Time Validation Logic
        adapter = new AvailableRequestsAdapter(availableList, (request, time) -> {
            // Check if the selected time is valid (only if date is today)
            if (!isValidFutureTime(request.getRequested_date(), time)) {
                Toast.makeText(getContext(), "For today's inspection, please select a future time", Toast.LENGTH_LONG).show();
                return; // Stop execution, do not send to Firebase
            }

            // If time is valid, proceed to assign
            assignInspectorToRequest(request, time);
        });
        recyclerView.setAdapter(adapter);

        // Load Data
        loadOpenRequestsFromFirebase();

        // Setup Search Listener
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filter(newText);
                return false;
            }
        });

        // Return Button
        btnReturn.setOnClickListener(v -> {
            Navigation.findNavController(v).popBackStack();
        });

        return view;
    }

    private void loadOpenRequestsFromFirebase() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("inspection_requests");

        ref.orderByChild("inspector_id").equalTo("").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Use a temporary list to avoid clearing reference in adapter
                List<Inspection_Request_class> tempList = new ArrayList<>();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    Inspection_Request_class req = ds.getValue(Inspection_Request_class.class);
                    if (req != null) {
                        tempList.add(req);
                    }
                }

                // Sort by date (earliest first)
                java.util.Collections.sort(tempList, (r1, r2) -> {
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                        Date d1 = sdf.parse(r1.getRequested_date());
                        Date d2 = sdf.parse(r2.getRequested_date());
                        if (d1 != null && d2 != null) return d1.compareTo(d2);
                        return 0;
                    } catch (Exception e) { return 0; }
                });

                // Update adapter with the NEW temp list
                if (adapter != null) {
                    adapter.updateList(tempList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error loading requests", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Assigns the inspector to the request using the ID passed from the previous screen
    private void assignInspectorToRequest(Inspection_Request_class request, String time) {

        // ensure we received the ID
        if (currentInspectorId == null || currentInspectorId.isEmpty()) {
            Toast.makeText(getContext(), "Error: Inspector ID missing.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Set the ID and Time directly
        request.setInspector_id(currentInspectorId);
        request.setInspection_time(time);

        // Update Firebase
        DatabaseReference requestRef = FirebaseDatabase.getInstance().getReference("inspection_requests");
        requestRef.child(request.getRequest_uid()).setValue(request)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Added to schedule", Toast.LENGTH_SHORT).show();
                    // Navigate back to the schedule immediately
                    Navigation.findNavController(getView()).popBackStack();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Connection Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    // Helper method to validate time if date is today
    private boolean isValidFutureTime(String dateStr, String timeStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date reqDate = sdf.parse(dateStr);
            Date today = sdf.parse(sdf.format(new Date())); // Today at 00:00:00

            // If date is today, check the time
            if (reqDate != null && reqDate.equals(today)) {
                String[] parts = timeStr.split(":");
                int selectedHour = Integer.parseInt(parts[0]);
                int selectedMinute = Integer.parseInt(parts[1]);

                Calendar now = Calendar.getInstance();
                int currentHour = now.get(Calendar.HOUR_OF_DAY);
                int currentMinute = now.get(Calendar.MINUTE);

                // Return false if selected time is earlier than now
                if (selectedHour < currentHour || (selectedHour == currentHour && selectedMinute <= currentMinute)) {
                    return false;
                }
            }
            return true; // Valid if date is future OR date is today but time is future
        } catch (Exception e) {
            return true; // If parse fails, assume valid to avoid blocking user
        }
    }
}