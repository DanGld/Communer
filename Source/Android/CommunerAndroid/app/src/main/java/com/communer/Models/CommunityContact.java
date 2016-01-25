package com.communer.Models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ���� on 06/08/2015.
 */
public class CommunityContact implements Parcelable {

    private String Name;
    private String PhoneNumber;
    private String Position;
    private String ImageUrl;
    private String mail;
    private String id;

    public CommunityContact(String name, String phoneNumber, String position, String imageUrl, String mail, String id) {
        this.Name = name;
        this.PhoneNumber = phoneNumber;
        this.Position = position;
        this.ImageUrl = imageUrl;
        this.mail = mail;
        this.id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        PhoneNumber = phoneNumber;
    }

    public String getPosition() {
        return Position;
    }

    public void setPosition(String position) {
        Position = position;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
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
        dest.writeString(this.Name);
        dest.writeString(this.PhoneNumber);
        dest.writeString(this.Position);
        dest.writeString(this.ImageUrl);
        dest.writeString(this.mail);
        dest.writeString(this.id);
    }

    private CommunityContact(Parcel in) {
        this.Name = in.readString();
        this.PhoneNumber = in.readString();
        this.Position = in.readString();
        this.ImageUrl = in.readString();
        this.mail = in.readString();
        this.id = in.readString();
    }

    public static final Creator<CommunityContact> CREATOR = new Creator<CommunityContact>() {
        public CommunityContact createFromParcel(Parcel source) {
            return new CommunityContact(source);
        }

        public CommunityContact[] newArray(int size) {
            return new CommunityContact[size];
        }
    };
}
