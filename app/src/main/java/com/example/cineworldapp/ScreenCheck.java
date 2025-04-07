package com.example.cineworldapp;

public class ScreenCheck {
    private String time;
    private boolean screenPerfection;
    private boolean nightVisionGogglesUsed;
    private String staffInitials;

    public ScreenCheck() {
        // Needed for Firestore deserialization
    }

    public ScreenCheck(String time, boolean screenPerfection, boolean nightVisionGogglesUsed, String staffInitials) {
        this.time = time;
        this.screenPerfection = screenPerfection;
        this.nightVisionGogglesUsed = nightVisionGogglesUsed;
        this.staffInitials = staffInitials;
    }

    // Getters and setters
    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public boolean isScreenPerfection() { return screenPerfection; }
    public void setScreenPerfection(boolean screenPerfection) { this.screenPerfection = screenPerfection; }

    public boolean isNightVisionGogglesUsed() { return nightVisionGogglesUsed; }
    public void setNightVisionGogglesUsed(boolean nightVisionGogglesUsed) { this.nightVisionGogglesUsed = nightVisionGogglesUsed; }

    public String getStaffInitials() { return staffInitials; }
    public void setStaffInitials(String staffInitials) { this.staffInitials = staffInitials; }
}