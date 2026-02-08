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

    // Capture strings at the VERY START of the function
    public void register_inspector() {
        try {
            // 1. Capture UI elements
            EditText etName = findViewById(R.id.inspector_full_name_reg);
            EditText etEmail = findViewById(R.id.et_inspector_email_reg);
            EditText etComp = findViewById(R.id.et_company_name_reg);
            EditText etId = findViewById(R.id.inspector_ID_reg);
            EditText etLic = findViewById(R.id.inspector_licence_reg);
            EditText etPass = findViewById(R.id.inspector_password_reg);

            // 2. CRASH CHECK: If any of these are null, your IDs are wrong
            if (etName == null || etEmail == null || etPass == null) {
                Toast.makeText(this, "Internal Error: UI not found", Toast.LENGTH_SHORT).show();
                return;
            }

            final String name = etName.getText().toString().trim();
            final String email = etEmail.getText().toString().trim();
            final String company = etComp.getText().toString().trim();
            final String id = etId.getText().toString().trim();
            final String license = etLic.getText().toString().trim();
            final String password = etPass.getText().toString();

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
                return;
            }


            // 3. Database Check
            inspectorsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    // ... (your existing check logic) ...

                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    // SAVE FIRST, THEN NAVIGATE
                                    writeToDB(name, email, company, id, license);
                                    navigateTo(new inspector_login());
                                    Toast.makeText(MainActivity.this, "Registered!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(MainActivity.this, "Registered Failed!", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });

        } catch (Exception e) {
            Log.e("DEBUG_APP", "CRASH CAUSE: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Pass clean strings, DO NOT use findViewById here!
    public void writeToDB(String name, String email, String company, String id, String license) {
        DatabaseReference myRef = database.getReference("inspectors").child(id);

        // We omit password here for security as discussed
        inspector_class inspector = new inspector_class(name, email, company, id, license, "");


        myRef.setValue(inspector);
    }

    /*public void readDB()
    {
        // Read from the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("inspectors").child("12345678");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                inspector_class value = dataSnapshot.getValue(inspector_class.class);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });
    }*/

    public void navigateTo(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

}