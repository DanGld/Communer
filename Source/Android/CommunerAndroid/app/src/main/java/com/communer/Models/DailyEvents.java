package com.communer.Models;

import java.util.ArrayList;


public class DailyEvents {

    private String date;
    private ArrayList<EventOrMessage> events;
    private ArrayList<CommunityActivityOrFacility> activities;
    private ArrayList<PrayerDetails> prayers;

    public DailyEvents(String date, ArrayList<EventOrMessage> events, ArrayList<CommunityActivityOrFacility> activities, ArrayList<PrayerDetails> prayers) {
        this.date = date;
        this.events = events;
        this.activities = activities;
        this.prayers = prayers;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public ArrayList<EventOrMessage> getEvents() {
        return events;
    }

    public void setEvents(ArrayList<EventOrMessage> events) {
        this.events = events;
    }

    public ArrayList<CommunityActivityOrFacility> getActivities() {
        return activities;
    }

    public void setActivities(ArrayList<CommunityActivityOrFacility> activities) {
        this.activities = activities;
    }

    public ArrayList<PrayerDetails> getPrayers() {
        return prayers;
    }

    public void setPrayers(ArrayList<PrayerDetails> prayers) {
        this.prayers = prayers;
    }
}
