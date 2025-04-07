package com.example.cineworldapp;

import java.util.ArrayList;

public class ScreenCardModel {

    private String screen;
    private String title;
    private String startTime;
    private String featureTime;
    private String finishTime;
    private ArrayList<ScreenCheck> checks;

    public ScreenCardModel(){

    }

    public ScreenCardModel(String screen, String title, String startTime, String featureTime, String finishTime, ArrayList<ScreenCheck> checks) {
        this.screen = screen;
        this.title = title;
        this.startTime = startTime;
        this.featureTime = featureTime;
        this.finishTime = finishTime;
        this.checks = checks;
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


    public ArrayList<ScreenCheck> getChecks() { return checks; }
    public void setChecks(ArrayList<ScreenCheck> checks) { this.checks = checks; }

}

