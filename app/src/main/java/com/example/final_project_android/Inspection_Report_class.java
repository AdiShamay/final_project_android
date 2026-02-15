package com.example.final_project_android;

import com.google.firebase.database.PropertyName;
import java.util.List;

public class Inspection_Report_class {

    // השמות נשארים באות גדולה כדי לא לשבור לך את הקוד בפרויקט
    private String Report_ID;
    private String Business_ID;
    private String Inspector_Email;
    private String Restaurant_Name;
    private String Date;
    private int Total_Score;
    private String Final_Grade;
    private List<new_inspection_item> Inspection_Items_List;

    public Inspection_Report_class() {}

    public Inspection_Report_class(String report_ID, String business_ID, String inspector_Email, String restaurant_Name, String date, int total_Score, String final_Grade, List<new_inspection_item> inspection_Items_List) {
        this.Report_ID = report_ID;
        this.Business_ID = business_ID;
        this.Inspector_Email = inspector_Email;
        this.Restaurant_Name = restaurant_Name;
        this.Date = date;
        this.Total_Score = total_Score;
        this.Final_Grade = final_Grade;
        this.Inspection_Items_List = inspection_Items_List;
    }

    // כאן הקסם: אומרים ל-Firebase לאיזה שם ב-DB להתייחס
    @PropertyName("report_ID")
    public String getReport_ID() { return Report_ID; }
    @PropertyName("report_ID")
    public void setReport_ID(String report_ID) { this.Report_ID = report_ID; }

    @PropertyName("business_ID")
    public String getBusiness_ID() {return Business_ID;}

    @PropertyName("business_ID")
    public void setBusiness_ID(String business_ID) { this.Business_ID = business_ID; }

    @PropertyName("inspector_Email")
    public String getInspector_Email() { return Inspector_Email; }
    @PropertyName("inspector_Email")
    public void setInspector_Email(String inspector_Email) { this.Inspector_Email = inspector_Email; }

    @PropertyName("restaurant_Name")
    public String getRestaurant_Name() { return Restaurant_Name; }
    @PropertyName("restaurant_Name")
    public void setRestaurant_Name(String restaurant_Name) { this.Restaurant_Name = restaurant_Name; }

    @PropertyName("date")
    public String getDate() { return Date; }
    @PropertyName("date")
    public void setDate(String date) { this.Date = date; }

    @PropertyName("total_Score")
    public int getTotal_Score() { return Total_Score; }
    @PropertyName("total_Score")
    public void setTotal_Score(int total_Score) { this.Total_Score = total_Score; }

    @PropertyName("final_Grade")
    public String getFinal_Grade() { return Final_Grade; }
    @PropertyName("final_Grade")
    public void setFinal_Grade(String final_Grade) { this.Final_Grade = final_Grade; }

    @PropertyName("inspection_Items_List")
    public List<new_inspection_item> getInspection_Items_List() { return Inspection_Items_List; }
    @PropertyName("inspection_Items_List")
    public void setInspection_Items_List(List<new_inspection_item> inspection_Items_List) { this.Inspection_Items_List = inspection_Items_List; }
}