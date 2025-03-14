package com.example.cineworldapp;

import java.util.ArrayList;

public class FloorEquipmentSanitisationModel {

    private boolean isSanitised;
    private String staffInitials;
    private String timeDue;
    private String areasToSanitise;

    public FloorEquipmentSanitisationModel(boolean isSanitised, String staffInitials, String timeDue, String areasToSanitise) {
        this.isSanitised = isSanitised;
        this.staffInitials = staffInitials;
        this.timeDue = timeDue;
        this.areasToSanitise = areasToSanitise;
    }

    public boolean isSanitised() {
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
        return areasToSanitise;
    }

    public void setAreasToSanitise(String areasToSanitise) {
        this.areasToSanitise = areasToSanitise;
    }

    public String getAreasToSanitise() {
        return areasToSanitise;
    }





}
