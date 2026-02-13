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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class add_to_schedule extends Fragment {

    private RecyclerView recyclerView;
    private AvailableRequestsAdapter adapter;
    private List<Inspection_Request_class> availableList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_to_schedule, container, false);

        // Initialize UI components
        recyclerView = view.findViewById(R.id.rv_available_requests);
        ImageButton btnReturn = view.findViewById(R.id.btn_return);

        availableList = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Setup the adapter with a listener for the "Accept" button
        adapter = new AvailableRequestsAdapter(availableList, (request, time) -> {
            assignInspectorToRequest(request, time);
        });
        recyclerView.setAdapter(adapter);

        loadOpenRequestsFromFirebase();

        // Handle return button click to navigate back
        btnReturn.setOnClickListener(v -> {
            Navigation.findNavController(v).popBackStack();
        });

        return view;
    }

    /**
     * Fetches requests from Firebase where no inspector has been assigned yet.
     * It filters for requests where inspector_id is an empty string defined in the class.
     */
    private void loadOpenRequestsFromFirebase() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("inspection_requests");

        // Filter: inspector_id must be an empty string ("")
        ref.orderByChild("inspector_id").equalTo("").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                availableList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Inspection_Request_class req = ds.getValue(Inspection_Request_class.class);
                    if (req != null) {
                        availableList.add(req);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error loading requests", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Assigns the current logged-in inspector to the selected inspection request
    private void assignInspectorToRequest(Inspection_Request_class request, String time) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null) {
            // This is a rare edge case, keeping generic error
            Toast.makeText(getContext(), "System Error: Please relogin", Toast.LENGTH_SHORT).show();
            return;
        }

        String userEmail = currentUser.getEmail();

        // Correct reference based on DB structure (lowercase "inspectors")
        DatabaseReference inspectorsRef = FirebaseDatabase.getInstance().getReference("inspectors");

        // Query: Find inspector by Email
        // Note: Using "email" (lowercase) because that matches your DB screenshot
        inspectorsRef.orderByChild("email").equalTo(userEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Inspector_class inspector = ds.getValue(Inspector_class.class);

                        if (inspector != null) {
                            // Update request with real ID
                            request.setInspector_id(inspector.getID());
                            request.setInspection_time(time);

                            // Save to DB
                            DatabaseReference requestRef = FirebaseDatabase.getInstance().getReference("inspection_requests");
                            requestRef.child(request.getRequest_uid()).setValue(request)
                                    .addOnSuccessListener(aVoid -> {
                                        // User Feedback
                                        Toast.makeText(getContext(), "Added to schedule", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(getContext(), "Connection Error", Toast.LENGTH_SHORT).show();
                                    });
                        }
                        break;
                    }
                } else {
                    // Log to console for developer only, do not show to user
                    System.out.println("Debug: Inspector email not found in DB");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Log to console only
                System.out.println("Debug Error: " + error.getMessage());
            }
        });
    }
}