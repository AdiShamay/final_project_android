package com.example.final_project_android;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference inspectorsRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        inspectorsRef = database.getReference("inspectors"); // Single collection for inspectors

        // טעינת דף הבית כברירת מחדל בעת פתיחת האפליקציה
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new home())
                    .commit();
        }
    }
    public void login_inspector() {
        String email = ((EditText) findViewById(R.id.et_inspector_email_login)).getText().toString().trim();
        String password = ((EditText) findViewById(R.id.password_insp_login)).getText().toString().trim();

        // Basic validation to prevent empty submissions
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show();
                        // SUCCESS: Navigate to Dashboard
                        navigateTo(new inspector_dashboard());
                        } else {
                        Exception e = task.getException();
                        if (e instanceof FirebaseAuthInvalidUserException) {
                            Toast.makeText(this, "Account not found. Please register.", Toast.LENGTH_LONG).show();
                        } else if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            Toast.makeText(this, "Incorrect password.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    public void register_inspector() {
        //inspector input
        final String name = ((EditText) findViewById(R.id.inspector_full_name_reg)).getText().toString().trim();
        final String email = ((EditText) findViewById(R.id.et_inspector_email_reg)).getText().toString().trim();
        final String company = ((EditText) findViewById(R.id.et_company_name_reg)).getText().toString().trim();
        final String id = ((EditText) findViewById(R.id.inspector_ID_reg)).getText().toString().trim();
        final String license = ((EditText) findViewById(R.id.inspector_licence_reg)).getText().toString().trim();
        final String password = ((EditText) findViewById(R.id.inspector_password_reg)).getText().toString().trim();

        //checking all the fields are complete
        if (name.isEmpty()) { Toast.makeText(this, "Name is not completed", Toast.LENGTH_SHORT).show(); return; }
        if (email.isEmpty()) { Toast.makeText(this, "Email is not completed", Toast.LENGTH_SHORT).show(); return; }
        if (company.isEmpty()) { Toast.makeText(this, "Company name is not completed", Toast.LENGTH_SHORT).show(); return; }
        if (id.isEmpty()) { Toast.makeText(this, "ID is not completed", Toast.LENGTH_SHORT).show(); return; }
        if (license.isEmpty()) { Toast.makeText(this, "License is not completed", Toast.LENGTH_SHORT).show(); return; }
        if (password.isEmpty()) { Toast.makeText(this, "Password is not completed", Toast.LENGTH_SHORT).show(); return; }


        // 3. Database Check: Ensure ID or License doesn't already exist
        inspectorsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean idOrLicenseExists = false;
                for (DataSnapshot ds : snapshot.getChildren()) {
                    inspector_class existing = ds.getValue(inspector_class.class);
                    if (existing != null && (existing.getID().equals(id) || existing.getLicence_number().equals(license))||existing.getEmail().equals(email)) {
                        idOrLicenseExists = true;
                        break;
                    }
                }

                if (idOrLicenseExists) {
                    // Case: User already exists (ID or License)
                    Toast.makeText(MainActivity.this, "User already exist!", Toast.LENGTH_LONG).show();
                } else {
                    // 4. Create User in Firebase Auth
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(MainActivity.this, task -> {
                                if (task.isSuccessful()) {
                                    // 5. Save to Realtime Database using the ID as the key
                                    writeToDBInspector(name, email, company, id, license);

                                    // Show success toast only when registration works
                                    Toast.makeText(MainActivity.this, "Registration Successful!", Toast.LENGTH_SHORT).show();

                                    // Navigate back to login page
                                    navigateTo(new inspector_login());
                                } else {
                                    // Handle failure (e.g., password too weak, email malformed)
                                    Toast.makeText(MainActivity.this, "Registration Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void writeToDBInspector(String name, String email, String company, String id, String license) {
        DatabaseReference myRef = database.getReference("inspectors").child(id);
        inspector_class inspector = new inspector_class(name, email, company, id, license, "");
        myRef.setValue(inspector);
    }

    public void navigateTo(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

}