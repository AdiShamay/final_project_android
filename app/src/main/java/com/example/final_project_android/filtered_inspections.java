package com.example.final_project_android;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.navigation.Navigation;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class filtered_inspections extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_filtered_inspections, container, false);

        // 1. אתחול ה-RecyclerView והאדאפטר
        RecyclerView rvReviews = view.findViewById(R.id.rv_reviews_history);
        rvReviews.setLayoutManager(new LinearLayoutManager(getContext()));

        FilteredInspectionsAdapter adapter = new FilteredInspectionsAdapter(() -> {
            Navigation.findNavController(view).navigate(R.id.action_restaurant_reviews2_to_review_details2);
        });
        rvReviews.setAdapter(adapter);

        // 2. שליפת נתוני הסינון מה-Bundle
        if (getArguments() != null) {
            String filterType = getArguments().getString("filterType");
            String filterValue = getArguments().getString("filterValue");

            Log.d("CHECK_FIREBASE", "Bundle arrived! Type: " + filterType + ", Value: " + filterValue);

            if (filterValue != null) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("inspections");
                Query query;

                // 3. בניית השאילתה הדינמית
                if ("inspector_Email".equals(filterType)) {
                    query = ref.orderByChild("inspector_Email").equalTo(filterValue.toLowerCase().trim());
                } else if ("business_ID".equals(filterType)) {
                    query = ref.orderByChild("business_ID").equalTo(filterValue);
                } else {
                    query = ref; // הצגת הכל במידה ואין פילטר מוכר
                }

                // 4. מאזין יחיד ומאוחד לקבלת הנתונים
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Log.d("CHECK_FIREBASE", "Results found: " + snapshot.getChildrenCount());

                        List<Inspection_Report_class> inspectionsList = new ArrayList<>();

                        if (snapshot.exists()) {
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                Inspection_Report_class inspection = ds.getValue(Inspection_Report_class.class);
                                if (inspection != null) {
                                    inspectionsList.add(inspection);
                                }
                            }
                        } else {
                            Log.d("CHECK_FIREBASE", "No records found for: " + filterValue);
                        }

                        // עדכון האדאפטר ברשימה (גם אם היא ריקה)
                        adapter.setInspections(inspectionsList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "שגיאת מסד נתונים: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Log.e("CHECK_FIREBASE", "filterValue is null!");
            }
        } else {
            Log.e("CHECK_FIREBASE", "No arguments received in Fragment!");
        }

        // כפתור חזרה
        ImageButton btnReturn = view.findViewById(R.id.btn_return);
        btnReturn.setOnClickListener(v -> Navigation.findNavController(v).popBackStack());

        return view;
    }
}