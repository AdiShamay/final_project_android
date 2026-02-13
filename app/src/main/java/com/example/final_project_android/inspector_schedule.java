package com.example.final_project_android;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button; // שימוש ב-Button רגיל
import android.widget.CalendarView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.navigation.Navigation;
import android.widget.ImageButton;

public class inspector_schedule extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inspector_schedule, container, false);

        // Find views
        Button btnList = view.findViewById(R.id.btn_view_list);
        Button btnCalendar = view.findViewById(R.id.btn_view_calendar);
        RecyclerView rvList = view.findViewById(R.id.rv_schedule_list);
        CalendarView cvCalendar = view.findViewById(R.id.cv_schedule_calendar);

        // Setup dummy list
        rvList.setLayoutManager(new LinearLayoutManager(getContext()));
        rvList.setAdapter(new RestaurantAdapter(name -> {}));

        // Toggle Logic: Show List
        btnList.setOnClickListener(v -> {
            rvList.setVisibility(View.VISIBLE);
            cvCalendar.setVisibility(View.GONE);

            btnList.setBackgroundColor(0xFF2196F3); // Blue
            btnList.setTextColor(Color.WHITE);
            btnCalendar.setBackgroundColor(Color.WHITE);
            btnCalendar.setTextColor(Color.BLACK);
        });

        // Toggle Logic: Show Calendar
        btnCalendar.setOnClickListener(v -> {
            rvList.setVisibility(View.GONE);
            cvCalendar.setVisibility(View.VISIBLE);

            btnCalendar.setBackgroundColor(0xFF2196F3); // Blue
            btnCalendar.setTextColor(Color.WHITE);
            btnList.setBackgroundColor(Color.WHITE);
            btnList.setTextColor(Color.BLACK);
        });

        // Add to schedule
        Button btnAddSchedule = view.findViewById(R.id.btn_add_schedule);
        btnAddSchedule.setOnClickListener(v -> {
            Navigation.findNavController(v)
                    .navigate(R.id.action_inspector_schedule2_to_add_to_schedule);
        });

        // Initialize return button
        ImageButton btnReturn = view.findViewById(R.id.btn_return);
        btnReturn.setOnClickListener(v -> {
            // Navigate back to the Inspector Dashboard
            Navigation.findNavController(v).popBackStack();
        });

        return view;
    }
}