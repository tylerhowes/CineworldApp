package com.example.cineworldapp;

import android.os.Parcel;
import android.os.Parcelable;

public class AlcoholRefusalLogDataModel implements Parcelable {

    String nameOrDescription;
    String product;
    String time;
    String date;
    String reason;

    public AlcoholRefusalLogDataModel(String nameOrDescription, String product, String time, String date, String reason) {
        this.nameOrDescription = nameOrDescription;
        this.product = product;
        this.time = time;
        this.date = date;
        this.reason = reason;
    }

    public String getNameOrDescription() {
        return nameOrDescription;
    }

    public void setNameOrDescription(String nameOrDescription) {
        this.nameOrDescription = nameOrDescription;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public static final Creator<AlcoholRefusalLogDataModel> CREATOR = new Creator<AlcoholRefusalLogDataModel>() {
        @Override
        public AlcoholRefusalLogDataModel createFromParcel(Parcel in) {
            return new AlcoholRefusalLogDataModel(in);
        }

        @Override
        public AlcoholRefusalLogDataModel[] newArray(int size) {
            return new AlcoholRefusalLogDataModel[size];
        }
    };

    protected AlcoholRefusalLogDataModel(Parcel in) {
        nameOrDescription = in.readString();
        product = in.readString();
        time = in.readString();
        date = in.readString();
        reason = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nameOrDescription);
        dest.writeString(product);
        dest.writeString(time);
        dest.writeString(date);
        dest.writeString(reason);
    }

    @Override
    public int describeContents() {
        return 0;
    }




}
