package com.example.final_project_android;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Fragment for editing restaurant details.
 */
public class edit_restaurant_profile extends Fragment {

    private EditText etName, etEmail, etAddress, etTaxId, etPassword;
    private DatabaseReference resRef;
    private FirebaseUser currentUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_restaurant_profile, container, false);

        // Initialize UI
        etName = view.findViewById(R.id.et_edit_name);
        etEmail = view.findViewById(R.id.et_edit_email);
        etAddress = view.findViewById(R.id.et_edit_address);
        etPassword = view.findViewById(R.id.et_edit_password);
        etTaxId = view.findViewById(R.id.et_edit_tax_id);

        // Disable editing for Email and Tax ID since they are unique identifiers
        etEmail.setEnabled(false);
        etTaxId.setEnabled(false);
        etTaxId.setOnClickListener(v ->
                Toast.makeText(getActivity(), "ID cannot be changed.", Toast.LENGTH_SHORT).show());
        etEmail.setOnClickListener(v ->
                Toast.makeText(getActivity(), "Email cannot be changed.", Toast.LENGTH_SHORT).show());

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        resRef = FirebaseDatabase.getInstance().getReference("restaurants");

        // Check if data was passed via Bundle to avoid extra DB calls
        if (getArguments() != null) {
            String passedName = getArguments().getString("res_name");
            String passedEmail = getArguments().getString("email");
            String passedAddress = getArguments().getString("address");
            String passedTaxId = getArguments().getString("business_id");
            String passedKey = getArguments().getString("db_key");

            // Populate UI immediately from the bundle
            etName.setText(passedName);
            etEmail.setText(passedEmail);
            etAddress.setText(passedAddress);
            etTaxId.setText(passedTaxId);

            // Store the database key in the tag for the save function
            etTaxId.setTag(passedKey);
        } else {
            // Load from DB if no arguments were passed
            loadProfileData();
        }

        // Handle the Save button click
        Button btnSave = view.findViewById(R.id.btn_save_changes);
        btnSave.setOnClickListener(v -> {
            saveProfileChange();
        });

        ImageButton btnReturn = view.findViewById(R.id.btn_return);
        btnReturn.setOnClickListener(v -> Navigation.findNavController(v).popBackStack());

        return view;
    }

    private void loadProfileData() {
        if (currentUser == null || currentUser.getEmail() == null) return;

        String targetEmail = currentUser.getEmail();

        // Directly reference the restaurant by its encoded email anchor
        resRef.orderByChild("email").equalTo(targetEmail)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            // loop through the results
                            for (DataSnapshot child : snapshot.getChildren()) {
                                Restaurant_class res = child.getValue(Restaurant_class.class);
                                if (res != null) {
                                    etName.setText(res.getRes_name());
                                    etEmail.setText(res.getEmail());
                                    etAddress.setText(res.getAddress());
                                    etTaxId.setText(res.getBusiness_id());
                                }
                            }
                        } else {
                            Toast.makeText(getActivity(), "No profile found for this email", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getActivity(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveProfileChange() {
        if (currentUser == null || currentUser.getEmail() == null) return;

        // Get values from the UI
        String newName = etName.getText().toString().trim();
        String newAddress = etAddress.getText().toString().trim();
        String newPass = etPassword.getText().toString().trim();
        String businessId = etTaxId.getText().toString().trim();

        if (newName.isEmpty() || newAddress.isEmpty()) {
            Toast.makeText(getActivity(), "Name and Address are required", Toast.LENGTH_SHORT).show();
            return;
        }

        // Prepare the update map
        Map<String, Object> updates = new HashMap<>();
        updates.put("res_name", newName);
        updates.put("address", newAddress);

        resRef.child(businessId).updateChildren(updates).addOnSuccessListener(aVoid -> {

            // Check if a password update is actually requested
            if (!newPass.isEmpty()) {
                currentUser.updatePassword(newPass).addOnCompleteListener(task -> {
                    // Check if fragment is still active
                    if (isAdded() && getContext() != null) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Profile Updated!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                        Navigation.findNavController(requireView()).popBackStack();
                    }
                });
            } else {
                // No password change, move back immediately after DB update
                if (isAdded() && getContext() != null) {
                    Toast.makeText(getContext(), "Profile Updated!", Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(requireView()).popBackStack();
                }
            }
        });
    }
}

