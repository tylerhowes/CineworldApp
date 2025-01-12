package com.example.cineworldapp;

public class ToiletCheckDataModel {

    String toiletCheckNumber;
    String toiletCheckTime;
    String staffInitials;

    public ToiletCheckDataModel(String toiletCheckNumber, String toiletCheckTime, String staffInitials) {
        this.toiletCheckNumber = toiletCheckNumber;
        this.toiletCheckTime = toiletCheckTime;
        this.staffInitials = staffInitials;
    }

    public String getToiletCheckNumber() {
        return toiletCheckNumber;
    }

    public void setToiletCheckNumber(String toiletCheckNumber) {
        this.toiletCheckNumber = toiletCheckNumber;
    }

    public String getToiletCheckTime() {
        return toiletCheckTime;
    }

    public void setToiletCheckTime(String toiletCheckTime) {
        this.toiletCheckTime = toiletCheckTime;
    }

    public String getStaffInitials() {
        return staffInitials;
    }

    public void setStaffInitials(String staffInitials) {
        this.staffInitials = staffInitials;
    }

}
