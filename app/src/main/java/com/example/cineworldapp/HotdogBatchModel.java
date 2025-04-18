package com.example.cineworldapp;

public class HotdogBatchModel {

    String timeStarted;
    String timeBagged;

    String staffInitialsStarted;
    String staffInitialsBagged;

    String regularQuantity;
    String largeQuantity;
    String veggieQuantity;

    String regularTemperatureReached;
    String largeTemperatureReached;
    String veggieTemperatureReached;

    String teamLeaderVisualCheck;
    String batchNumber;

    String status;


    public HotdogBatchModel(){

    }

    public HotdogBatchModel(String timeStarted, String timeBagged, String staffInitialsStarted, String staffInitialsBagged,
                            String regularQuantity, String largeQuantity, String veggieQuantity,
                            String regularTemperatureReached, String largeTemperatureReached, String veggieTemperatureReached, String teamLeaderVisualCheck, String batchNumber, String status) {
        this.timeStarted = timeStarted;
        this.timeBagged = timeBagged;
        this.staffInitialsStarted = staffInitialsStarted;
        this.staffInitialsBagged = staffInitialsBagged;
        this.regularQuantity = regularQuantity;
        this.largeQuantity = largeQuantity;
        this.veggieQuantity = veggieQuantity;
        this.regularTemperatureReached = regularTemperatureReached;
        this.largeTemperatureReached = largeTemperatureReached;
        this.veggieTemperatureReached = veggieTemperatureReached;
        this.teamLeaderVisualCheck = teamLeaderVisualCheck;
        this.batchNumber = batchNumber;
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBatchNumber() {
        return batchNumber;
    }

    public void setBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
    }

    public String getTimeStarted() {
        return timeStarted;
    }

    public void setTimeStarted(String timeStarted) {
        this.timeStarted = timeStarted;
    }

    public String getTimeBagged() {
        return timeBagged;
    }

    public void setTimeBagged(String timeBagged) {
        this.timeBagged = timeBagged;
    }

    public String getStaffInitialsStarted() {
        return staffInitialsStarted;
    }

    public void setStaffInitialsStarted(String staffInitialsStarted) {
        this.staffInitialsStarted = staffInitialsStarted;
    }

    public String getStaffInitialsBagged() {
        return staffInitialsBagged;
    }

    public void setStaffInitialsBagged(String staffInitialsBagged) {
        this.staffInitialsBagged = staffInitialsBagged;
    }

    public String getRegularQuantity() {
        return regularQuantity;
    }

    public void setRegularQuantity(String regularQuantity) {
        this.regularQuantity = regularQuantity;
    }

    public String getLargeQuantity() {
        return largeQuantity;
    }

    public void setLargeQuantity(String largeQuantity) {
        this.largeQuantity = largeQuantity;
    }

    public String getVeggieQuantity() {
        return veggieQuantity;
    }

    public void setVeggieQuantity(String veggieQuantity) {
        this.veggieQuantity = veggieQuantity;
    }

    public String getRegularTemperatureReached() {
        return regularTemperatureReached;
    }

    public void setRegularTemperatureReached(String regularTemperatureReached) {
        this.regularTemperatureReached = regularTemperatureReached;
    }

    public String getLargeTemperatureReached() {
        return largeTemperatureReached;
    }

    public void setLargeTemperatureReached(String largeTemperatureReached) {
        this.largeTemperatureReached = largeTemperatureReached;
    }

    public String getVeggieTemperatureReached() {
        return veggieTemperatureReached;
    }

    public void setVeggieTemperatureReached(String veggieTemperatureReached) {
        this.veggieTemperatureReached = veggieTemperatureReached;
    }

    public String getTeamLeaderVisualCheck() {
        return teamLeaderVisualCheck;
    }

    public void setTeamLeaderVisualCheck(String teamLeaderVisualCheck) {
        this.teamLeaderVisualCheck = teamLeaderVisualCheck;
    }


}
