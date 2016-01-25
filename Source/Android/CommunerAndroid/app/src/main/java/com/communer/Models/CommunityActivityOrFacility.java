package com.communer.Models;

import java.util.ArrayList;

public class CommunityActivityOrFacility {

    private String bigImageUrl;
    private String name;
    private String description;
    private String data;
    private LocationObj locationObj;
    private ArrayList<EventOrMessage> events;
    private ArrayList<String> images;
    private CommunityContact communityContact;
    private String id;

    public CommunityActivityOrFacility(String bigImageUrl, String name, String description, String data, LocationObj locationObj, ArrayList<EventOrMessage> events, ArrayList<String> images, CommunityContact communityContact, String id) {
        this.bigImageUrl = bigImageUrl;
        this.name = name;
        this.description = description;
        this.data = data;
        this.locationObj = locationObj;
        this.events = events;
        this.images = images;
        this.communityContact = communityContact;
        this.id = id;
    }

    public String getBigImageUrl() {
        return bigImageUrl;
    }

    public void setBigImageUrl(String bigImageUrl) {
        this.bigImageUrl = bigImageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public LocationObj getLocationObj() {
        return locationObj;
    }

    public void setLocationObj(LocationObj locationObj) {
        this.locationObj = locationObj;
    }

    public ArrayList<EventOrMessage> getEvents() {
        return events;
    }

    public void setEvents(ArrayList<EventOrMessage> events) {
        this.events = events;
    }

    public CommunityContact getCommunityContact() {
        return communityContact;
    }

    public void setCommunityContact(CommunityContact communityContact) {
        this.communityContact = communityContact;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<String> getImages() {
        return images;
    }

    public void setImages(ArrayList<String> images) {
        this.images = images;
    }
}
