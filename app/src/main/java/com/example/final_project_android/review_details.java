package com.example.final_project_android;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;

/**
 * Fragment showing the specific details of a single inspection.
 * Reached by clicking a row in the history list.
 */
public class review_details extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the details layout
        return inflater.inflate(R.layout.fragment_review_details, container, false);
    }
}