package com.communer.Models;


import android.os.Parcel;
import android.os.Parcelable;

public class CommunityMember implements Parcelable {

    private String imageUrl;
    private String name;
    private String phone;
    private String id;

    public CommunityMember(String imageUrl, String name, String phone, String id) {
        this.imageUrl = imageUrl;
        this.name = name;
        this.phone = phone;
        this.id = id;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.imageUrl);
        dest.writeString(this.name);
        dest.writeString(this.phone);
        dest.writeString(this.id);
    }

    protected CommunityMember(Parcel in) {
        this.imageUrl = in.readString();
        this.name = in.readString();
        this.phone = in.readString();
        this.id = in.readString();
    }

    public static final Parcelable.Creator<CommunityMember> CREATOR = new Parcelable.Creator<CommunityMember>() {
        public CommunityMember createFromParcel(Parcel source) {
            return new CommunityMember(source);
        }

        public CommunityMember[] newArray(int size) {
            return new CommunityMember[size];
        }
    };
}
