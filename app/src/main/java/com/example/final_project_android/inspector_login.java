package com.example.final_project_android;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button; // Don't forget this import
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Handles the login process for Inspectors.
 * Connects "Login" button to the Inspector Dashboard.
 */
public class inspector_login extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inspector_login, container, false);

        // Register Link
        TextView tvRegister = view.findViewById(R.id.tv_go_to_inspector_register);
        tvRegister.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new inspector_register())
                    .addToBackStack(null)
                    .commit();
        });

        // Login Button Logic
        // Ensure your XML has a button with id: btn_login (or add it if missing)
        Button btnLogin = view.findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(v -> {
            MainActivity mainActivity = (MainActivity) getActivity();
            if (mainActivity != null) {
                mainActivity.login_inspector();
            }
        });
        //the Return button
        Button btnReturn = view.findViewById(R.id.btn_INS_login_return_to_home);
        btnReturn.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new home())
                    .commit();
        });





        return view;
    }
}