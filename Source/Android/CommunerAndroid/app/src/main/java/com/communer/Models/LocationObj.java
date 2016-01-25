package com.communer.Models;


import android.os.Parcel;
import android.os.Parcelable;

public class LocationObj implements Parcelable {

    private String title;
    private String cords;

    public LocationObj(String title, String cords) {
        this.title = title;
        this.cords = cords;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCords() {
        return cords;
    }

    public void setCords(String cords) {
        this.cords = cords;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.cords);
    }

    protected LocationObj(Parcel in) {
        this.title = in.readString();
        this.cords = in.readString();
    }

    public static final Parcelable.Creator<LocationObj> CREATOR = new Parcelable.Creator<LocationObj>() {
        public LocationObj createFromParcel(Parcel source) {
            return new LocationObj(source);
        }

        public LocationObj[] newArray(int size) {
            return new LocationObj[size];
        }
    };
}
