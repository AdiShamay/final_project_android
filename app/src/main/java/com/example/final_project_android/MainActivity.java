package com.example.final_project_android;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // טעינת דף הבית כברירת מחדל בעת פתיחת האפליקציה
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new home())
                    .commit();
        }
        mAuth = FirebaseAuth.getInstance();
    }
    public void login_inspector() {
        String email = ((EditText) findViewById(R.id.et_inspector_email)).getText().toString().trim();
        String password = ((EditText) findViewById(R.id.password_insp_login)).getText().toString();

        // Basic validation to prevent empty submissions
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "You have successfully logged in", Toast.LENGTH_LONG).show();
                        } else {
                            // Get the specific exception
                            Exception exception = task.getException();

                            if (exception instanceof FirebaseAuthInvalidUserException) {
                                // Case: User does not exist (or has been disabled)
                                Toast.makeText(MainActivity.this, "User does not exist. Please sign up.", Toast.LENGTH_LONG).show();
                            } else if (exception instanceof FirebaseAuthInvalidCredentialsException) {
                                // Case: Wrong password (or malformed email)
                                Toast.makeText(MainActivity.this, "Incorrect password. Please try again.", Toast.LENGTH_LONG).show();
                            } else {
                                // Case: Other errors (network issues, etc.)
                                Toast.makeText(MainActivity.this, "Login failed: " + exception.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
    }

    public void register_inspector(){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                        } else {

                        }
                    }
                });

    }
}