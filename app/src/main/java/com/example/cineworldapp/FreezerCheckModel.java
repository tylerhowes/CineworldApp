package com.example.cineworldapp;

public class FreezerCheckModel {
    String freezerName;
    String targetTemp;
    String freezerOpenTemp;
    String freezerCloseTemp;
    String freezerVisualCheck;
    String freezerPMVisualCheck;
    String freezerOpenStaffInitials;
    String freezerCloseStaffInitials;

    public FreezerCheckModel(String freezerName, String targetTemp, String freezerOpenTemp, String freezerCloseTemp, String freezerVisualCheck, String freezerPMVisualCheck, String freezerOpenStaffInitials, String freezerCloseStaffInitials) {
        this.freezerName = freezerName;
        this.targetTemp = targetTemp;
        this.freezerOpenTemp = freezerOpenTemp;
        this.freezerCloseTemp = freezerCloseTemp;
        this.freezerVisualCheck = freezerVisualCheck;
        this.freezerPMVisualCheck = freezerPMVisualCheck;
        this.freezerOpenStaffInitials = freezerOpenStaffInitials;
        this.freezerCloseStaffInitials = freezerCloseStaffInitials;
    }

    public FreezerCheckModel() {
        // Default constructor required for calls to DataSnapshot.getValue(FreezerCheckModel.class)
    }

    public String getFreezerName() {
        return freezerName;
    }

    public void setFreezerName(String freezerName) {
        this.freezerName = freezerName;
    }

    public String getTargetTemp() {
        return targetTemp;
    }

    public void setTargetTemp(String freezerTargetTemp) {
        this.targetTemp = freezerTargetTemp;
    }
    public String getFreezerOpenTemp() {
        return freezerOpenTemp;
    }
    public void setFreezerOpenTemp(String freezerOpenTemp) {
        this.freezerOpenTemp = freezerOpenTemp;
    }
    public String getFreezerCloseTemp() {
        return freezerCloseTemp;
    }
    public void setFreezerCloseTemp(String freezerCloseTemp) {
        this.freezerCloseTemp = freezerCloseTemp;
    }
    public String getFreezerVisualCheck() {
        return freezerVisualCheck;
    }
    public void setFreezerVisualCheck(String freezerVisualCheck) {
        this.freezerVisualCheck = freezerVisualCheck;
    }
    public String getFreezerPMVisualCheck() {
        return freezerPMVisualCheck;
    }
    public void setFreezerPMVisualCheck(String freezerPMVisualCheck) {
        this.freezerPMVisualCheck = freezerPMVisualCheck;
    }
    public String getFreezerOpenStaffInitials() {
        return freezerOpenStaffInitials;
    }
    public void setFreezerOpenStaffInitials(String freezerOpenStaffInitials) {
        this.freezerOpenStaffInitials = freezerOpenStaffInitials;
    }
    public String getFreezerCloseStaffInitials() {
        return freezerCloseStaffInitials;
    }
    public void setFreezerCloseStaffInitials(String freezerCloseStaffInitials) {
        this.freezerCloseStaffInitials = freezerCloseStaffInitials;
    }

}
