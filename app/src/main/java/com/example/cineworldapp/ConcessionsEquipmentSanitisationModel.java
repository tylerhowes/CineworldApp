package com.example.cineworldapp;

public class ConcessionsEquipmentSanitisationModel {


    private boolean isSanitised;
    private String staffInitials;
    private String timeDue;
    private String areasToSanitise;
    private String timeCompleted;


    public ConcessionsEquipmentSanitisationModel(boolean isSanitised, String staffInitials, String timeDue, String areasToSanitise, String timeCompleted) {
        this.isSanitised = isSanitised;
        this.staffInitials = staffInitials;
        this.timeDue = timeDue;
        this.areasToSanitise = areasToSanitise;
        this.timeCompleted = timeCompleted;
    }

    public void setTimeCompleted(String timeCompleted){
        this.timeCompleted = timeCompleted;
    }

    public String getTimeCompleted(){
        return timeCompleted;
    }

    public boolean getIsSanitised() {
        return isSanitised;
    }

    public void setSanitised(boolean sanitised) {
        isSanitised = sanitised;
    }

    public String getStaffInitials() {
        return staffInitials;
    }

    public void setStaffInitials(String staffInitials) {
        this.staffInitials = staffInitials;
    }

    public String getSanitisedTime() {
        return timeDue;
    }

    public void setTimeDue(String timeDue) {
        this.timeDue = timeDue;
    }

    public String getTimeDue() {
        return timeDue;
    }

    public void setAreasToSanitise(String areasToSanitise) {
        this.areasToSanitise = areasToSanitise;
    }

    public String getAreasToSanitise() {
        return areasToSanitise;
    }

}
