package com.example.final_project_android;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import androidx.fragment.app.Fragment;

/**
 * Fragment for editing Inspector details.
 */
public class edit_inspector_profile extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_inspector_profile, container, false);

        Button btnSave = view.findViewById(R.id.btn_insp_save);

        btnSave.setOnClickListener(v -> {
            Toast.makeText(getActivity(), "Inspector Profile Updated!", Toast.LENGTH_SHORT).show();
            getParentFragmentManager().popBackStack();
        });

        return view;
    }
}