package com.example.cineworldapp;

public class ScreenCardModel {

    private String screen;
    private String title;
    private String startTime;
    private String featureTime;
    private String finishTime;

    public ScreenCardModel(String screen, String title, String startTime, String featureTime, String finishTime) {
        this.screen = screen;
        this.title = title;
        this.startTime = startTime;
        this.featureTime = featureTime;
        this.finishTime = finishTime;
    }

    public String getScreen() {
        return screen;
    }

    public String getTitle() {
        return title;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getFeatureTime() {
        return featureTime;
    }

    public String getFinishTime() {
        return finishTime;
    }
}
