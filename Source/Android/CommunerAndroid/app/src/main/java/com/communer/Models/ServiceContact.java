package com.communer.Models;

import java.util.ArrayList;

public class ServiceContact {

    private String serviceContactID;
    private String imageUrl;
    private String name;
    private LocationObj locationObj;

    private String phoneNumber;
    private double rate;
    private double votes;
    private ArrayList<ServiceComment> Comments;

    public ServiceContact(String serviceContactID, String imageUrl, String name, LocationObj locationObj, String phoneNumber, double rate, double votes, ArrayList<ServiceComment> comments) {
        this.serviceContactID = serviceContactID;
        this.imageUrl = imageUrl;
        this.name = name;
        this.locationObj = locationObj;
        this.phoneNumber = phoneNumber;
        this.rate = rate;
        this.votes = votes;
        Comments = comments;
    }

    public String getServiceContactID() {
        return serviceContactID;
    }

    public void setServiceContactID(String serviceContactID) {
        this.serviceContactID = serviceContactID;
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

    public LocationObj getLocationObj() {
        return locationObj;
    }

    public void setLocationObj(LocationObj locationObj) {
        this.locationObj = locationObj;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public ArrayList<ServiceComment> getComments() {
        return Comments;
    }

    public void setComments(ArrayList<ServiceComment> comments) {
        Comments = comments;
    }

    public double getVotes() {
        return votes;
    }

    public void setVotes(double votes) {
        this.votes = votes;
    }
}
