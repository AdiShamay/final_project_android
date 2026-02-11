package com.example.final_project_android;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import android.widget.ImageButton;

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
            // Navigate to registration using the NavGraph action
            Navigation.findNavController(v).navigate(R.id.action_restaurant_login_to_restaurant_register2);
        });

        // Find the Login button
        Button btnLogin = view.findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(v -> {
            MainActivity mainActivity = (MainActivity) getActivity();
            if (mainActivity != null) {
                mainActivity.login_restaurant();
            }
        });

        //the Return button
        // Initialize the professional back button and use popBackStack
        ImageButton btnReturn = view.findViewById(R.id.btn_return);
        btnReturn.setOnClickListener(v -> Navigation.findNavController(v).popBackStack());

        return view;
    }
}