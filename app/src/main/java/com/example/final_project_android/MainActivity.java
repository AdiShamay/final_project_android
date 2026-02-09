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

        // טעינת דף הבית כברירת מחדל בעת פתיחת האפליקציה
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new home())
                    .commit();
        }
    }
    public void login_inspector() {
        String email_insp = ((EditText) findViewById(R.id.et_inspector_email_login)).getText().toString().trim();
        String password_insp = ((EditText) findViewById(R.id.password_insp_login)).getText().toString().trim();

        // Basic validation to prevent empty submissions
        if (email_insp.isEmpty() || password_insp.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email_insp, password_insp)
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
        // 1. Capture UI Elements
        EditText etName_insp = findViewById(R.id.inspector_full_name_reg);
        EditText etEmail_insp = findViewById(R.id.et_inspector_email_reg);
        EditText etComp_insp = findViewById(R.id.et_company_name_reg);
        EditText etId_insp = findViewById(R.id.inspector_ID_reg);
        EditText etLic_insp = findViewById(R.id.inspector_licence_reg);
        EditText etPass_insp = findViewById(R.id.inspector_password_reg);

        // 2. Safety Check
        if (etName_insp == null || etEmail_insp == null || etPass_insp == null || etComp_insp == null || etId_insp == null || etLic_insp == null) {
            return;
        }

        final String name = etName_insp.getText().toString().trim();
        final String email = etEmail_insp.getText().toString().trim();
        final String company = etComp_insp.getText().toString().trim();
        final String id = etId_insp.getText().toString().trim();
        final String license = etLic_insp.getText().toString().trim();
        final String password = etPass_insp.getText().toString().trim();

        // 3. Validation
        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || id.isEmpty() || license.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // 4. Database Check for existing ID or License
        inspectorsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean alreadyInDB = false;
                for (DataSnapshot ds : snapshot.getChildren()) {
                    inspector_class existing = ds.getValue(inspector_class.class);
                    if (existing != null && (existing.getID().equals(id) || existing.getLicence_number().equals(license))) {
                        alreadyInDB = true;
                        break;
                    }
                }

                if (alreadyInDB) {
                    Toast.makeText(MainActivity.this, "ID or License already registered!", Toast.LENGTH_LONG).show();
                } else {
                    // 5. Create Firebase Auth account
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(MainActivity.this, task -> {
                                if (task.isSuccessful()) {
                                    writeToDBInspector(name, email, company, id, license);
                                    Toast.makeText(MainActivity.this, "Registration Successful!", Toast.LENGTH_SHORT).show();
                                    navigateTo(new inspector_login());
                                } else {
                                    Toast.makeText(MainActivity.this, "Auth Failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Database Error", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void writeToDBInspector(String name, String email, String company, String id, String license) {
        DatabaseReference myRef = database.getReference("inspectors").child(id);
        inspector_class inspector = new inspector_class(name, email, company, id, license, "");
        myRef.setValue(inspector);
    }

    public void login_restaurant() {
        String email_res = ((EditText) findViewById(R.id.et_restaurant_email_login)).getText().toString().trim();
        String password_res = ((EditText) findViewById(R.id.restaurant_password_login)).getText().toString().trim();

        // Basic validation to prevent empty submissions
        if (email_res.isEmpty() || password_res.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email_res, password_res)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show();
                        // SUCCESS: Navigate to Dashboard
                        navigateTo(new restaurant_dashboard());
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

    public void register_restaurant(){
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

    // 4. Database Check for existing ID or License
        restaurantsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean alreadyInDB = false;
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Restaurant_class existing = ds.getValue(Restaurant_class.class);
                    if (existing != null && (existing.getBusiness_id().equals(Business_id_res))) {
                        alreadyInDB = true;
                        break;
                    }
                }

                if (alreadyInDB) {
                    Toast.makeText(MainActivity.this, "there is already a restaurant with this ID registered!", Toast.LENGTH_LONG).show();
                } else {
                    // 5. Create Firebase Auth account
                    mAuth.createUserWithEmailAndPassword(email_res, password_res)
                            .addOnCompleteListener(MainActivity.this, task -> {
                                if (task.isSuccessful()) {
                                    writeToDBrestaurant(name_res, email_res, Business_id_res, address_res);
                                    Toast.makeText(MainActivity.this, "Registration Successful!", Toast.LENGTH_SHORT).show();
                                    navigateTo(new restaurant_login());
                                } else {
                                    Toast.makeText(MainActivity.this, "Auth Failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                }
            }
        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            Toast.makeText(MainActivity.this, "Database Error", Toast.LENGTH_SHORT).show();
        }
    });
}

    public void writeToDBrestaurant(String name,String email,String Business_id,String address){
        DatabaseReference myRef = database.getReference("restaurants").child(Business_id);
        Restaurant_class restaurants = new Restaurant_class(name, email, Business_id, address, "");
        myRef.setValue(restaurants);
    }

    public void navigateTo(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

}