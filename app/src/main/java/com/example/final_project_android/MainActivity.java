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
        String email = ((EditText) findViewById(R.id.et_inspector_email_login)).getText().toString();
        String password = ((EditText) findViewById(R.id.password_insp_login)).getText().toString();

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
                        Exception exception = task.getException();
                        if (exception instanceof FirebaseAuthInvalidUserException) {
                            Toast.makeText(this, "User does not exist.", Toast.LENGTH_LONG).show();
                        } else if (exception instanceof FirebaseAuthInvalidCredentialsException) {
                            Toast.makeText(this, "Incorrect password.", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(this, "Login failed: " + exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    public void register_inspector(){
       String name= ((EditText) findViewById(R.id.inspector_full_name_reg)).getText().toString();
       String email = ((EditText) findViewById(R.id.et_inspector_email_reg)).getText().toString();
       String company = ((EditText) findViewById(R.id.et_company_name_reg)).getText().toString();
       String id = ((EditText) findViewById(R.id.inspector_ID_reg)).getText().toString().trim();
       String license = ((EditText) findViewById(R.id.inspector_licence_reg)).getText().toString().trim();
       String password = ((EditText) findViewById(R.id.inspector_password_reg)).getText().toString();

        // Validation: Check if any field is empty
        if (name.isEmpty()) { Toast.makeText(this, "Name is not completed", Toast.LENGTH_SHORT).show(); return; }
        if (email.isEmpty()) { Toast.makeText(this, "Email is not completed", Toast.LENGTH_SHORT).show(); return; }
        if (company.isEmpty()) { Toast.makeText(this, "company name is not completed", Toast.LENGTH_SHORT).show(); return; }
        if (id.isEmpty()) { Toast.makeText(this, "ID is not completed", Toast.LENGTH_SHORT).show(); return; }
        if (license.isEmpty()) { Toast.makeText(this, "License is not completed", Toast.LENGTH_SHORT).show(); return; }
        if (password.isEmpty()) { Toast.makeText(this, "Password is not completed", Toast.LENGTH_SHORT).show(); return; }

        //check if the inspector already exist
        inspectorsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean idExists = false;
                for (DataSnapshot ds : snapshot.getChildren()) {
                    inspector_class existing = ds.getValue(inspector_class.class);
                    if (existing != null && (existing.getID().equals(id) || existing.getLicence_number().equals(license))) {
                        idExists = true;
                        break;
                    }
                }

                if (idExists) {
                    Toast.makeText(MainActivity.this, "ID or License already registered!", Toast.LENGTH_LONG).show();
                } else {
                    // Create User in Firebase Auth
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    // Save to Realtime Database with the Role
                                    writeToDB(name, email,company, id, license, password);
                                    Toast.makeText(MainActivity.this, "Registration Successful!", Toast.LENGTH_SHORT).show();
                                    // Go back to login page
                                    navigateTo(new inspector_login());
                                } else {
                                    Toast.makeText(MainActivity.this, "Auth Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
    public void writeToDB(String name,String email,String company,String id,String licence,String password)
    {
        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("inspectors").child(id);

        inspector_class inspector = new inspector_class(
                ((EditText) findViewById(R.id.inspector_full_name_reg)).getText().toString(),
                ((EditText) findViewById(R.id.et_inspector_email_reg)).getText().toString(),
                ((EditText) findViewById(R.id.et_company_name_reg)).getText().toString(),
                ((EditText) findViewById(R.id.inspector_ID_reg)).getText().toString(),
                ((EditText) findViewById(R.id.inspector_licence_reg)).getText().toString(),
                ((EditText) findViewById(R.id.inspector_password_reg)).getText().toString()
        );

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