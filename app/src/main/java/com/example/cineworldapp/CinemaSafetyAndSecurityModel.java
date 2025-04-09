package com.example.cineworldapp;

public class CinemaSafetyAndSecurityModel {

    public CinemaSafetyAndSecurityModel(String timeOfCheck, String areas, String screens, String staffInitials, String timeCompleted){
        this.timeOfCheck = timeOfCheck;
        this.areas = areas;
        this.screens = screens;
        this.staffInitials = staffInitials;
        this.timeCompleted = timeCompleted;
    }
    private String timeOfCheck;
    private String areas;
    private String screens;
    private String staffInitials;
    private String timeCompleted;

    public String getTimeOfCheck(){
        return timeOfCheck;
    }
    public void setTimeOfCheck(String timeOfCheck){
        this.timeOfCheck = timeOfCheck;
    }
    public String getTimeCompleted(){
        return timeCompleted;
    }
    public void setTimeCompleted(String timeCompleted){
        this.timeCompleted = timeCompleted;
    }
    public String getAreas(){
        return areas;
    }

    public void setAreas(String areas){
        this.areas = areas;
    }

    public String getScreens(){
        return screens;
    }

    public void setScreens(String screens){
        this.screens = screens;
    }

    public String getStaffInitials(){
        return staffInitials;
    }

    public void setStaffInitials(String staffInitials){
        this.staffInitials = staffInitials;
    }

}
