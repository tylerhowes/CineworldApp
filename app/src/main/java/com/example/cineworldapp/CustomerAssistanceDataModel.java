package com.example.cineworldapp;

import android.os.Parcel;
import android.os.Parcelable;

public class CustomerAssistanceDataModel implements Parcelable{

    String customerName ;
    String screen;
    String seatNumber ;
    String startTime;
    String finishTime ;
    String assistanceRequired;
    String staffInitials;

    public CustomerAssistanceDataModel(String customerName, String screen, String seatNumber, String startTime, String finishTime, String assistanceRequired, String staffInitials){
        this.customerName = customerName;
        this.screen = screen;
        this.seatNumber = seatNumber;
        this.startTime = startTime;
        this.finishTime = finishTime;
        this.assistanceRequired =  assistanceRequired;
        this.staffInitials = staffInitials;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getScreen(){ return screen;}

    public void setScreen(String screen) {this.screen = screen;}

    public String getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(String finishTime) {
        this.finishTime = finishTime;
    }

    public String getAssistanceRequired() {
        return assistanceRequired;
    }

    public void setAssistanceRequired(String assistanceRequired) {
        this.assistanceRequired = assistanceRequired;
    }

    public void setStaffInitials(String staffInitials){
        this.staffInitials = staffInitials;
    }

    public String getStaffInitials(){
        return staffInitials;
    }

    // Parcelable implementation
    protected CustomerAssistanceDataModel(Parcel in) {
        customerName = in.readString();
        screen = in.readString();
        seatNumber = in.readString();
        startTime = in.readString();
        finishTime = in.readString();
        assistanceRequired = in.readString();
        staffInitials = in.readString();

    }

    public static final Parcelable.Creator<CustomerAssistanceDataModel> CREATOR = new Parcelable.Creator<CustomerAssistanceDataModel>() {
        @Override
        public CustomerAssistanceDataModel createFromParcel(Parcel in) {
            return new CustomerAssistanceDataModel(in);
        }

        @Override
        public CustomerAssistanceDataModel[] newArray(int size) {
            return new CustomerAssistanceDataModel[size];
        }
    };
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(customerName);
        dest.writeString(screen);
        dest.writeString(seatNumber);
        dest.writeString(startTime);
        dest.writeString(finishTime);
        dest.writeString(assistanceRequired);
        dest.writeString(staffInitials);
    }
    @Override
    public int describeContents() {
        return 0;
    }
}
