package com.example.final_project_android;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for the Inspection History list.
 * Displays real data from Firebase (Date, Inspector, Grade).
 */
public class FilteredInspectionsAdapter extends RecyclerView.Adapter<FilteredInspectionsAdapter.ViewHolder> {

    // רשימה שתחזיק את הנתונים שיגיעו מה-Firebase
    private List<Inspection_Report_class> inspectionsList = new ArrayList<>();
    public interface OnReviewClickListener { void onReviewClick(); }
    private final OnReviewClickListener listener;

    public FilteredInspectionsAdapter(OnReviewClickListener listener) {
        this.listener = listener;
    }

    // פונקציה קריטית: משמשת את ה-Fragment לעדכון הרשימה אחרי הסינון
    public void setInspections(List<Inspection_Report_class> newList) {
        this.inspectionsList = newList;
        notifyDataSetChanged(); // רענון התצוגה על המסך
    }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_inspection_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // שליפת הנתונים האמיתיים מהרשימה
        Inspection_Report_class inspection = inspectionsList.get(position);

        // עדכון ה-UI עם הנתונים מהאובייקט
        holder.tvDate.setText(inspection.getDate()); // וודא שיש getGetter ב-POJO
        holder.tvGrade.setText("Grade: " + inspection.getFinal_grade());

        holder.itemView.setOnClickListener(v -> listener.onReviewClick());
    }

    @Override
    public int getItemCount() {
        return inspectionsList.size(); // מחזיר את כמות התוצאות שנמצאו בחיפוש/סינון
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvInspector, tvGrade;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tv_row_date);
            tvGrade = itemView.findViewById(R.id.tv_row_grade);
        }
    }
}