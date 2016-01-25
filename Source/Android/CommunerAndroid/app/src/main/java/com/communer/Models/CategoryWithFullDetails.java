package com.communer.Models;

import java.util.ArrayList;


public class CategoryWithFullDetails {

    private String title;
    private ArrayList<ServiceContact> serviceContacts;

    public CategoryWithFullDetails(String title, ArrayList<ServiceContact> serviceContacts) {
        this.title = title;
        this.serviceContacts = serviceContacts;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<ServiceContact> getServiceContacts() {
        return serviceContacts;
    }

    public void setServiceContacts(ArrayList<ServiceContact> serviceContacts) {
        this.serviceContacts = serviceContacts;
    }
}
