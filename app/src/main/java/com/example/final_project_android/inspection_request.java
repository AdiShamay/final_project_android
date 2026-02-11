package com.example.final_project_android;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import android.widget.ImageButton;

/**
 * Handles inspection requests logic.
 */
public class inspection_request extends Fragment {

    // Simulating data (In real app, fetch from DB)
    private boolean isEligible = false; // We assume the user is eligible for now
    private boolean isRequestActive = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inspection_request, container, false);

        // Find views
        TextView tvStatus = view.findViewById(R.id.tv_request_status);
        TextView tvMessage = view.findViewById(R.id.tv_eligibility_message);
        Button btnAction = view.findViewById(R.id.btn_action_request);
        ImageView ivIcon = view.findViewById(R.id.iv_status_icon);

        // Logic check
        if (!isEligible) {
            // Case 1: Not eligible yet - Block the button
            tvMessage.setText("Next inspection available from: 20/02/2026");
            tvMessage.setTextColor(0xFFD32F2F); // Red
            btnAction.setEnabled(false); // Make sure it's disabled
            btnAction.setText("Not Available Yet");
            btnAction.setBackgroundColor(0xFFB0BEC5); // Gray
        } else {
            // Case 2: Eligible - Enable the button!

            // --- THIS WAS MISSING ---
            btnAction.setEnabled(true);
            // ------------------------

            // Update UI to show current state (Order or Cancel)
            updateUI(btnAction, tvStatus, tvMessage, ivIcon);

            // Set the click listener
            btnAction.setOnClickListener(v -> {
                // Toggle state
                isRequestActive = !isRequestActive;

                // Update UI immediately
                updateUI(btnAction, tvStatus, tvMessage, ivIcon);

                // Show confirmation
                if (isRequestActive) {
                    Toast.makeText(getActivity(), "Inspection Requested", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Request Cancelled", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Initialize the professional back button
        ImageButton btnReturn = view.findViewById(R.id.btn_return);
        btnReturn.setOnClickListener(v -> {
            // Navigate back to the Dashboard
            Navigation.findNavController(v).popBackStack();
        });

        return view;
    }

    // Helper to switch between "Request" and "Cancel" visuals
    private void updateUI(Button btn, TextView status, TextView msg, ImageView icon) {
        if (isRequestActive) {
            // State: Active Request
            status.setText("Status: Pending Approval");
            status.setTextColor(0xFFFFA000); // Orange

            msg.setText("Waiting for inspector assignment...");
            msg.setTextColor(0xFF757575);

            btn.setText("Cancel Request");
            btn.setBackgroundColor(0xFFD32F2F); // Red

            icon.setColorFilter(0xFFFFA000);
        } else {
            // State: Can Order
            status.setText("Status: No Active Request");
            status.setTextColor(0xFF757575);

            msg.setText("You are eligible for a new inspection.");
            msg.setTextColor(0xFF4CAF50); // Green

            btn.setText("Request Inspection");
            btn.setBackgroundColor(0xFF4CAF50); // Green

            icon.setColorFilter(0xFF757575);
        }
    }
}