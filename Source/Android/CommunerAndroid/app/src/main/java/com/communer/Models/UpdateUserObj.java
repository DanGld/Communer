package com.communer.Models;

import java.util.Date;

public class UpdateUserObj {

    private String userID;
    private String name;
    private String lastname;
    private String gender;
    private String mStatus;
    private Date bDay;
    private String email;
    private String imageUrl;

    public UpdateUserObj(String userID, String name, String lastname, String gender, String mStatus, Date bDay, String email, String imageUrl) {
        this.userID = userID;
        this.name = name;
        this.lastname = lastname;
        this.gender = gender;
        this.mStatus = mStatus;
        this.bDay = bDay;
        this.email = email;
        this.imageUrl = imageUrl;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getmStatus() {
        return mStatus;
    }

    public void setmStatus(String mStatus) {
        this.mStatus = mStatus;
    }

    public Date getbDay() {
        return bDay;
    }

    public void setbDay(Date bDay) {
        this.bDay = bDay;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
