package com.example.final_project_android;

// Represents a single violation item in the inspection checklist
public class new_inspection_item {

    // The category of the violation (e.g., Food Protection, Vermin)
    private String category;

    // The specific violation description based on NYC codes
    private String description;

    // Penalty points assigned by the inspector (0 means pass)
    private int currentPoints;

    // Optional remarks or evidence notes by the inspector
    private String comments;

    // Constructor to initialize the item
    public new_inspection_item(String category, String description) {
        this.category = category;
        this.description = description;
        this.currentPoints = 0; // Default is 0 (Clean)
        this.comments = ""; // Default is empty
    }

    // Getters and Setters
    public String getCategory() { return category; }

    public String getDescription() { return description; }

    public int getCurrentPoints() { return currentPoints; }
    public void setCurrentPoints(int points) { this.currentPoints = points; }

    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }
}