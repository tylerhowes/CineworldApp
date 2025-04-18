package com.example.cineworldapp;

import android.os.Parcel;
import android.os.Parcelable;

public class ToiletCheckDataModel implements Parcelable {

    String toiletCheckNumber;
    String toiletCheckTime;
    String staffInitials;
    String mensCorrectiveActions;
    String womensCorrectiveActions;
    String disabledCorrectiveActions;

    public ToiletCheckDataModel(String toiletCheckNumber, String toiletCheckTime, String staffInitials, String mensCorrectiveActions, String womensCorrectiveActions, String disabledCorrectiveActions) {
        this.toiletCheckNumber = toiletCheckNumber;
        this.toiletCheckTime = toiletCheckTime;
        this.staffInitials = staffInitials;
        this.mensCorrectiveActions = mensCorrectiveActions;
        this.womensCorrectiveActions = womensCorrectiveActions;
        this.disabledCorrectiveActions = disabledCorrectiveActions;
    }

    public String getMensCorrectiveActions(){
        return mensCorrectiveActions;
    }

    public void setMensCorrectiveActions(String mensCorrectiveActions){
        this.mensCorrectiveActions = mensCorrectiveActions;
    }

    public String getWomensCorrectiveActions(){
        return womensCorrectiveActions;
    }

    public void setWomensCorrectiveActions(String womensCorrectiveActions){
        this.mensCorrectiveActions = womensCorrectiveActions;
    }

    public String getDisabledCorrectiveActions(){
        return mensCorrectiveActions;
    }

    public void setDisabledCorrectiveActions(String disabledCorrectiveActions){
        this.disabledCorrectiveActions = disabledCorrectiveActions;
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

    // Parcelable implementation
    protected ToiletCheckDataModel(Parcel in) {
        toiletCheckNumber = in.readString();
        toiletCheckTime = in.readString();
        staffInitials = in.readString();
    }

    public static final Creator<ToiletCheckDataModel> CREATOR = new Creator<ToiletCheckDataModel>() {
        @Override
        public ToiletCheckDataModel createFromParcel(Parcel in) {
            return new ToiletCheckDataModel(in);
        }

        @Override
        public ToiletCheckDataModel[] newArray(int size) {
            return new ToiletCheckDataModel[size];
        }
    };
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(toiletCheckNumber);
        dest.writeString(toiletCheckTime);
        dest.writeString(staffInitials);
    }
    @Override
    public int describeContents() {
        return 0;
    }
}
