package com.communer.Models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ���� on 06/08/2015.
 */
public class ServiceComment implements Parcelable {

    private String imageUrl;
    private String name;
    private double rate;
    private String content;

    public ServiceComment(String imageUrl, String name, double rate, String content) {
        this.imageUrl = imageUrl;
        this.name = name;
        this.rate = rate;
        this.content = content;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.imageUrl);
        dest.writeString(this.name);
        dest.writeDouble(this.rate);
        dest.writeString(this.content);
    }

    private ServiceComment(Parcel in) {
        this.imageUrl = in.readString();
        this.name = in.readString();
        this.rate = in.readDouble();
        this.content = in.readString();
    }

    public static final Creator<ServiceComment> CREATOR = new Creator<ServiceComment>() {
        public ServiceComment createFromParcel(Parcel source) {
            return new ServiceComment(source);
        }

        public ServiceComment[] newArray(int size) {
            return new ServiceComment[size];
        }
    };
}
