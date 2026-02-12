package com.example.final_project_android;

import java.util.List;

public class Inspection_Report_class {

    private String Report_ID;
    private String Business_ID;
    private String Inspector_Email;
    private String Restaurant_Name;
    private String Date;
    private int Total_Score;
    private String Final_Grade;
    private List<new_inspection_item> Inspection_Items_List;

    // Empty constructor required for Firebase
    public Inspection_Report_class() {
    }

    // Full constructor to initialize all fields
    public Inspection_Report_class(String report_ID, String business_ID, String inspector_Email, String restaurant_Name, String date, int total_Score, String final_Grade, List<new_inspection_item> inspection_Items_List) {
        Report_ID = report_ID;
        Business_ID = business_ID;
        Inspector_Email = inspector_Email;
        Restaurant_Name = restaurant_Name;
        Date = date;
        Total_Score = total_Score;
        Final_Grade = final_Grade;
        Inspection_Items_List = inspection_Items_List;
    }

    // Getters and Setters
    public String getReport_ID() {
        return Report_ID;
    }

    public void setReport_ID(String report_ID) {
        Report_ID = report_ID;
    }

    public String getBusiness_ID() {
        return Business_ID;
    }

    public void setBusiness_ID(String business_ID) {
        Business_ID = business_ID;
    }

    public String getInspector_Email() {
        return Inspector_Email;
    }

    public void setInspector_Email(String inspector_Email) {
        Inspector_Email = inspector_Email;
    }

    public String getRestaurant_Name() {
        return Restaurant_Name;
    }

    public void setRestaurant_Name(String restaurant_Name) {
        Restaurant_Name = restaurant_Name;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public int getTotal_Score() {
        return Total_Score;
    }

    public void setTotal_Score(int total_Score) {
        Total_Score = total_Score;
    }

    public String getFinal_Grade() {
        return Final_Grade;
    }

    public void setFinal_Grade(String final_Grade) {
        Final_Grade = final_Grade;
    }

    public List<new_inspection_item> getInspection_Items_List() {
        return Inspection_Items_List;
    }

    public void setInspection_Items_List(List<new_inspection_item> inspection_Items_List) {
        Inspection_Items_List = inspection_Items_List;
    }
}