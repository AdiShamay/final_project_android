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
 * Fragment for editing Inspector details.
 */
public class edit_inspector_profile extends Fragment {

    private EditText etName, etEmail, etCompany, etLicId, etPassword;
    private DatabaseReference resRef;
    private FirebaseUser currentUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_inspector_profile, container, false);

        // Initialize UI
        etName = view.findViewById(R.id.et_insp_edit_name);
        etEmail = view.findViewById(R.id.et_insp_edit_email);
        etCompany = view.findViewById(R.id.et_insp_edit_company);
        etPassword = view.findViewById(R.id.et_insp_edit_password);
        etLicId = view.findViewById(R.id.et_insp_edit_licNum);

        // Disable editing for Email and Tax ID since they are unique identifiers
        etEmail.setEnabled(false);
        etLicId.setEnabled(false);
        etLicId.setOnClickListener(v ->
                Toast.makeText(getActivity(), "Licence number cannot be changed.", Toast.LENGTH_SHORT).show());
        etEmail.setOnClickListener(v ->
                Toast.makeText(getActivity(), "Email cannot be changed.", Toast.LENGTH_SHORT).show());

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        resRef = FirebaseDatabase.getInstance().getReference("inspectors");

        // Check if data was passed via Bundle to avoid extra DB calls
        if (getArguments() != null) {
            String passedName = getArguments().getString("name");
            String passedEmail = getArguments().getString("email");
            String passedCompany = getArguments().getString("company");
            String passedLicId = getArguments().getString("license_id");
            String passedKey = getArguments().getString("db_key");

            // Populate UI immediately
            etName.setText(passedName);
            etEmail.setText(passedEmail);
            etCompany.setText(passedCompany);
            etLicId.setText(passedLicId);

            // Set the tag needed for the save function
            etLicId.setTag(passedKey);
        } else {
            // Load from DB if no arguments were passed
            loadProfileData();
        }

        // Find views
        Button btnSave = view.findViewById(R.id.btn_insp_save);
        ImageButton btnReturn = view.findViewById(R.id.btn_return);

        // Handle the Save button click
        btnSave.setOnClickListener(v -> {
            // Save logic to MongoDB will go here
            saveProfileChange();
        });

        // Handle the professional back button (Back Arrow)
        btnReturn.setOnClickListener(v -> {
            // Navigate back to Dashboard without saving changes
            Navigation.findNavController(v).popBackStack();
        });

        return view;
    }

    private void loadProfileData() {
        if (currentUser == null || currentUser.getEmail() == null) return;

        String targetEmail = currentUser.getEmail();

        // Query by email to find the correct inspector node
        resRef.orderByChild("email").equalTo(targetEmail)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot child : snapshot.getChildren()) {
                                Inspector_class insp = child.getValue(Inspector_class.class);
                                if (insp != null) {
                                    etName.setText(insp.getFull_name());
                                    etEmail.setText(insp.getEmail());
                                    etCompany.setText(insp.getCompany_name());
                                    etLicId.setText(insp.getLicence_number());

                                    // IMPORTANT: Store the database key (e.g., "123456789")
                                    // so we know which node to update later.
                                    etLicId.setTag(child.getKey());
                                }
                            }
                        } else {
                            Toast.makeText(getContext(), "Profile data not found.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        if (isAdded())
                            Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveProfileChange() {
        // Check if we have the user and the stored database key (tag)
        if (currentUser == null || etLicId.getTag() == null) {
            Toast.makeText(getContext(), "Error: Session expired or ID missing", Toast.LENGTH_SHORT).show();
            return;
        }

        String newName = etName.getText().toString().trim();
        String newCompany = etCompany.getText().toString().trim();
        String newPass = etPassword.getText().toString().trim();
        String dbNodeKey = etLicId.getTag().toString(); // This is the ID anchor

        if (newName.isEmpty() || newCompany.isEmpty()) {
            Toast.makeText(getContext(), "Name and Company are required", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put("full_name", newName);
        updates.put("company_name", newCompany);

        // Update the specific node in the "inspectors" list
        resRef.child(dbNodeKey).updateChildren(updates).addOnSuccessListener(aVoid -> {

            // Handle optional password change
            if (!newPass.isEmpty()) {
                currentUser.updatePassword(newPass).addOnCompleteListener(task -> {
                    if (!isAdded() || getContext() == null) return;

                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "Profile and Password Updated!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Profile updated, but password failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                    // Return to Dashboard after password attempt
                    Navigation.findNavController(requireView()).popBackStack();
                });
            } else {
                // No password change, just notify and return
                if (isAdded() && getContext() != null) {
                    Toast.makeText(getContext(), "Profile Updated!", Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(requireView()).popBackStack();
                }
            }
        }).addOnFailureListener(e -> {
            if (isAdded() && getContext() != null) {
                Toast.makeText(getContext(), "Update Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}