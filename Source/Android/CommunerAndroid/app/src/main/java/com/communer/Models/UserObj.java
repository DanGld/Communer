package com.communer.Models;

import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.util.ArrayList;

public class UserObj {

    private String userID;
    private String name;
    private String lastname;
    private String gender;
    private String mStatus;
    private CalendarDay bDay;
    private String email;
    private String imageUrl;
    private String currentCommunityID;
    private ArrayList<CommunityObj> cummunities;
    private boolean isRabai;

    public UserObj(String userID, String name, String lastname, String gender, String mStatus, CalendarDay bDay, String email, String imageUrl, String currentCommunityID, ArrayList<CommunityObj> cummunities, boolean isRabai) {
        this.userID = userID;
        this.name = name;
        this.lastname = lastname;
        this.gender = gender;
        this.mStatus = mStatus;
        this.bDay = bDay;
        this.email = email;
        this.imageUrl = imageUrl;
        this.currentCommunityID = currentCommunityID;
        this.cummunities = cummunities;
        this.isRabai = isRabai;
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

    public CalendarDay getbDay() {
        return bDay;
    }

    public void setbDay(CalendarDay bDay) {
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

    public String getCurrentCommunityID() {
        return currentCommunityID;
    }

    public void setCurrentCommunityID(String currentCommunityID) {
        this.currentCommunityID = currentCommunityID;
    }

    public ArrayList<CommunityObj> getCummunities() {
        return cummunities;
    }

    public void setCummunities(ArrayList<CommunityObj> cummunities) {
        this.cummunities = cummunities;
    }

    public boolean isRabai() {
        return isRabai;
    }

    public void setIsRabai(boolean isRabai) {
        this.isRabai = isRabai;
    }
}
