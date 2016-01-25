package com.communer.Models;

public class PostRateModel {

    private String serviceContactID;
    private double rate;
    private String description;

    public PostRateModel(String serviceContactID, double rate, String description) {
        this.serviceContactID = serviceContactID;
        this.rate = rate;
        this.description = description;
    }

    public String getServiceContactID() {
        return serviceContactID;
    }

    public void setServiceContactID(String serviceContactID) {
        this.serviceContactID = serviceContactID;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
