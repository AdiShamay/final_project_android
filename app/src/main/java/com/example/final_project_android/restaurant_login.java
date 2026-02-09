package com.example.final_project_android;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.fragment.app.Fragment;

/**
 * Handles the login process for Restaurant Owners.
 * Navigates to the Dashboard upon successful login.
 */
public class restaurant_login extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout
        View view = inflater.inflate(R.layout.fragment_restaurant_login, container, false);

        // Find the Register text link
        TextView tvRegister = view.findViewById(R.id.tv_go_to_register);

        // Navigate to registration when text is clicked
        tvRegister.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new restaurant_register())
                    .addToBackStack(null)
                    .commit();
        });

        // Find the Login button
        Button btnLogin = view.findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(v -> {
            MainActivity mainActivity = (MainActivity) getActivity();
            if (mainActivity != null) {
                mainActivity.login_restaurant();
            }
        });
        return view;
    }
}