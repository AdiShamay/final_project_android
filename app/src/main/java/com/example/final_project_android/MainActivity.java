package com.example.final_project_android;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.navigation.Navigation;

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

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference inspectorsRef;
    private DatabaseReference restaurantsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        //separate inside the database inspectors from restaurants
        inspectorsRef = database.getReference("inspectors");
        restaurantsRef = database.getReference("restaurants");

    }

    public void login_inspector() {
        String email_insp = ((EditText) findViewById(R.id.et_inspector_email_login)).getText().toString().trim();
        String password_insp = ((EditText) findViewById(R.id.password_insp_login)).getText().toString().trim();

        if (email_insp.isEmpty() || password_insp.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // User Authentication with Firebase Auth
        mAuth.signInWithEmailAndPassword(email_insp, password_insp)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Checking that the user exists in the inspectors table
                        // We are looking for a match for the email in the inspectors folder
                        inspectorsRef.orderByChild("email").equalTo(email_insp).limitToFirst(1)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            // An inspector with this email was found - successful login
                                            Toast.makeText(MainActivity.this, "Inspector Login Successful", Toast.LENGTH_SHORT).show();
                                            navigateTo(R.id.action_inspector_login_to_inspector_dashboard2);
                                        } else {
                                            // The user exists in Auth but is not a supervisor (probably a restaurant)
                                            mAuth.signOut(); // We will disconnect it immediately
                                            Toast.makeText(MainActivity.this, "Error: You are not authorized as an inspector.", Toast.LENGTH_LONG).show();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(MainActivity.this, "Database Error", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        // Handling Login errors
                        handleAuthError(task.getException());
                    }
                });
    }

    public void register_inspector() {
        // Capture UI Elements
        EditText etName_insp = findViewById(R.id.inspector_full_name_reg);
        EditText etEmail_insp = findViewById(R.id.et_inspector_email_reg);
        EditText etComp_insp = findViewById(R.id.et_company_name_reg);
        EditText etId_insp = findViewById(R.id.inspector_ID_reg);
        EditText etLic_insp = findViewById(R.id.inspector_licence_reg);
        EditText etPass_insp = findViewById(R.id.inspector_password_reg);

        // Safety Check
        if (etName_insp == null || etEmail_insp == null || etPass_insp == null || etComp_insp == null || etId_insp == null || etLic_insp == null) {
            return;
        }

        final String name = etName_insp.getText().toString().trim();
        final String email = etEmail_insp.getText().toString().trim();
        final String company = etComp_insp.getText().toString().trim();
        final String id = etId_insp.getText().toString().trim();
        final String license = etLic_insp.getText().toString().trim();
        final String password = etPass_insp.getText().toString().trim();

        // Validation
        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || id.isEmpty() || license.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Database Check by ID
        inspectorsRef.child(id).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot snapshot = task.getResult();
                if (snapshot.exists()) {
                    //The ID already exists in the system.
                    Toast.makeText(MainActivity.this, "Inspector ID already registered!", Toast.LENGTH_LONG).show();
                } else {
                    // Create Firebase Auth account
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(MainActivity.this, authTask -> {
                                if (authTask.isSuccessful()) {
                                    // Calling the writetoDB function
                                    writeToDBInspector(name, email, company, id, license);
                                    Toast.makeText(MainActivity.this, "Registration Successful!", Toast.LENGTH_SHORT).show();
                                    //Calling the navigateTo function
                                    navigateTo(R.id.action_inspector_register_to_inspector_login2);
                                } else {
                                    Toast.makeText(MainActivity.this, "Auth Failed: " + authTask.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                }
            } else {
                Toast.makeText(MainActivity.this, "Database Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void writeToDBInspector(String name, String email, String company, String id, String license) {
        DatabaseReference myRef = database.getReference("inspectors").child(String.valueOf(id));
        inspector_class inspector = new inspector_class(name, email, company, id, license, "");
        myRef.setValue(inspector);
    }

    public void login_restaurant() {
        String email_res = ((EditText) findViewById(R.id.et_restaurant_email_login)).getText().toString().trim();
        String password_res = ((EditText) findViewById(R.id.restaurant_password_login)).getText().toString().trim();

        if (email_res.isEmpty() || password_res.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Authentication with Firebase Auth
        mAuth.signInWithEmailAndPassword(email_res, password_res)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Data retrieval
                        // We search for the restaurant by its email directly
                        restaurantsRef.orderByChild("email").equalTo(email_res).limitToFirst(1)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            // A restaurant with this email address was found.
                                            Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                            navigateTo(R.id.action_restaurant_login_to_restaurant_dashboard2);
                                        } else {
                                            // The user has been authenticated with Auth but does not appear in the restaurant table
                                            mAuth.signOut();
                                            Toast.makeText(MainActivity.this, "Error: You are not registered as a restaurant.", Toast.LENGTH_LONG).show();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(MainActivity.this, "Database Error", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        // Handling Login errors
                        handleAuthError(task.getException());
                    }
                });
    }

    public void register_restaurant() {
        // 1. Capture UI Elements
        EditText etName_res = findViewById(R.id.res_name_reg);
        EditText etEmail_res = findViewById(R.id.et_rest_email_reg);
        EditText etBusiness_id_res = findViewById(R.id.res_id_reg);
        EditText etaddress_res = findViewById(R.id.res_address_reg);
        EditText etPass_res = findViewById(R.id.res_password_reg);

        // 2. Safety Check
        if (etName_res == null || etEmail_res == null || etBusiness_id_res == null || etaddress_res == null || etPass_res == null) {
            return;
        }

        final String name_res = etName_res.getText().toString().trim();
        final String email_res = etEmail_res.getText().toString().trim();
        final String Business_id_res = etBusiness_id_res.getText().toString().trim();
        final String address_res = etaddress_res.getText().toString().trim();
        final String password_res = etPass_res.getText().toString().trim();

        // 3. Validation
        if (name_res.isEmpty() || email_res.isEmpty() || Business_id_res.isEmpty() || address_res.isEmpty() || password_res.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        //4. Database Check for existing ID or License
        database.getReference("restaurants").child(Business_id_res).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().exists()) {
                    //Checking if the ID already exists
                    Toast.makeText(MainActivity.this, "ID already registered!", Toast.LENGTH_LONG).show();
                } else {
                    // ID is available - continue to register
                    mAuth.createUserWithEmailAndPassword(email_res, password_res)
                            .addOnCompleteListener(this, authTask -> {
                                if (authTask.isSuccessful()) {
                                    // write to DB
                                    writeToDBrestaurant(name_res, email_res, Business_id_res, address_res);

                                    Toast.makeText(this, "Success!", Toast.LENGTH_SHORT).show();

                                    // go back to login view
                                    navigateTo(R.id.action_restaurant_register2_to_restaurant_login);
                                } else {
                                    Toast.makeText(this, "Failed: " + authTask.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                }
            } else {
                Toast.makeText(this, "Database Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void writeToDBrestaurant(String name, String email, String Business_id, String address) {
        DatabaseReference myRef = database.getReference("restaurants").child(String.valueOf(Business_id));
        Restaurant_class restaurants = new Restaurant_class(name, email, Business_id, address, "");
        myRef.setValue(restaurants);
    }

    public void navigateTo(int actionId) {
        Navigation.findNavController(this, R.id.nav_host_fragment).navigate(actionId);
    }

    // Login Error handling function
    private void handleAuthError(Exception e) {
        if (e instanceof FirebaseAuthInvalidUserException) {
            Toast.makeText(this, "Account not found. Please register.", Toast.LENGTH_LONG).show();
        } else if (e instanceof FirebaseAuthInvalidCredentialsException) {
            Toast.makeText(this, "Incorrect password.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

}