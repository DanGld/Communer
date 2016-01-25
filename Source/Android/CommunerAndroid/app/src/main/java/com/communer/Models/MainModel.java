package com.communer.Models;

import java.util.ArrayList;

/**
 * Created by יובל on 25/10/2015.
 */
public class MainModel {

    private String fistName, lastName, gender, mStatus, email, imageURL, supportMail;
    private long birthday;
    private ArrayList<CommunityObj> communitiesArray;
    private boolean isRabai;
    private ArrayList<String> inactiveItems;

    public MainModel(String fistName, String lastName, String gender, String mStatus, String email, String imageURL, String supportMail, long birthday, ArrayList<CommunityObj> communitiesArray, boolean isRabai, ArrayList<String> inactiveItems) {
        this.fistName = fistName;
        this.lastName = lastName;
        this.gender = gender;
        this.mStatus = mStatus;
        this.email = email;
        this.imageURL = imageURL;
        this.supportMail = supportMail;
        this.birthday = birthday;
        this.communitiesArray = communitiesArray;
        this.isRabai = isRabai;
        this.inactiveItems = inactiveItems;
    }

    public String getFistName() {
        return fistName;
    }

    public void setFistName(String fistName) {
        this.fistName = fistName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getSupportMail() {
        return supportMail;
    }

    public void setSupportMail(String supportMail) {
        this.supportMail = supportMail;
    }

    public long getBirthday() {
        return birthday;
    }

    public void setBirthday(long birthday) {
        this.birthday = birthday;
    }

    public ArrayList<CommunityObj> getCommunitiesArray() {
        return communitiesArray;
    }

    public void setCommunitiesArray(ArrayList<CommunityObj> communitiesArray) {
        this.communitiesArray = communitiesArray;
    }

    public boolean isRabai() {
        return isRabai;
    }

    public void setIsRabai(boolean isRabai) {
        this.isRabai = isRabai;
    }

    public ArrayList<String> getInactiveItems() {
        return inactiveItems;
    }

    public void setInactiveItems(ArrayList<String> inactiveItems) {
        this.inactiveItems = inactiveItems;
    }
}
