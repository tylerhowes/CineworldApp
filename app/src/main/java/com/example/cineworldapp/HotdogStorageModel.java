package com.example.cineworldapp;

public class HotdogStorageModel {

    private String timeDue;
    private String unit1TopTemp;
    private String unit1BottomTemp;
    private String unit2TopTemp;
    private String unit2BottomTemp;
    private String staffInitials;
    private String teamLeaderInitials;
    private Boolean checkComplete;
    private String timeCompleted;

    public HotdogStorageModel(String checkTime, String unit1TopTemp, String unit1BottomTemp, String unit2TopTemp, String unit2BottomTemp, String userInitials, String teamLeaderInitials, Boolean checkComplete, String timeCompleted) {
        this.timeDue = checkTime;
        this.unit1TopTemp = unit1TopTemp;
        this.unit1BottomTemp = unit1BottomTemp;
        this.unit2TopTemp = unit2TopTemp;
        this.unit2BottomTemp = unit2BottomTemp;
        this.staffInitials = userInitials;
        this.teamLeaderInitials = teamLeaderInitials;
        this.checkComplete = checkComplete;
        this.timeCompleted = timeCompleted;
    }

    public HotdogStorageModel() {
        // Default constructor required for calls to DataSnapshot.getValue(HotdogStorageModel.class)
    }

    public String getTimeCompleted() {
        return timeCompleted;
    }

    public void setTimeCompleted(String timeCompleted) {
        this.timeCompleted = timeCompleted;
    }

    public Boolean getCheckComplete() {
        return checkComplete;
    }

    public void setCheckComplete(Boolean checkComplete) {
        this.checkComplete = checkComplete;
    }

    public String getTeamLeaderInitials() {
        return teamLeaderInitials;
    }

    public void setTeamLeaderInitials(String teamLeaderInitials) {
        this.teamLeaderInitials = teamLeaderInitials;
    }

    public String getTimeDue() {
        return timeDue;
    }

    public String getUnit1TopTemp() {
        return unit1TopTemp;
    }

    public String getUnit1BottomTemp() {
        return unit1BottomTemp;
    }

    public String getUnit2TopTemp() {
        return unit2TopTemp;
    }

    public String getUnit2BottomTemp() {
        return unit2BottomTemp;
    }

    public String getStaffInitials() {
        return staffInitials;
    }

    public void setStaffInitials(String staffInitials) {
        this.staffInitials = staffInitials;
    }

    public void setTimeDue(String timeDue) {
        this.timeDue = timeDue;
    }

    public void setUnit1TopTemp(String unit1TopTemp) {
        this.unit1TopTemp = unit1TopTemp;
    }

    public void setUnit1BottomTemp(String unit1BottomTemp) {
        this.unit1BottomTemp = unit1BottomTemp;
    }

    public void setUnit2TopTemp(String unit2TopTemp) {
        this.unit2TopTemp = unit2TopTemp;
    }

    public void setUnit2BottomTemp(String unit2BottomTemp) {
        this.unit2BottomTemp = unit2BottomTemp;
    }
}
