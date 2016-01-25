package com.communer.Models;

import java.util.ArrayList;

/**
 * Created by ���� on 05/08/2015.
 */
public class CalendarEventItem {

    private String id;
    private long eventTime;
    private String eventTimeString;
    private long eventStartTime;
    private long eventEndTime;
    private String eventTitle;
    private String eventLocation;
    private String type;
    private LocationObj locObj;
    private String parasha;
    private String buyLink;
    private boolean isFree;
    ArrayList<CommunityContact> participants;
    private long prayerMillis;
    private String attendingStatus;
    private String dailyEventDate;

    public CalendarEventItem(String id, long eventStartTime, long eventEndTime, String eventTitle, String type, LocationObj locObj, ArrayList<CommunityContact> eventParticipants, String buyLink, boolean isFree, String attendingStatus, String dailyEventID) {
        this.id = id;
        this.eventStartTime = eventStartTime;
        this.eventEndTime = eventEndTime;
        this.eventTitle = eventTitle;
        this.type = type;
        this.locObj = locObj;
        this.participants = eventParticipants;
        this.buyLink = buyLink;
        this.isFree = isFree;
        this.attendingStatus = attendingStatus;
        this.dailyEventDate = dailyEventID;
    }

    public CalendarEventItem(String eventTime, String eventTitle, String eventLocation, String type, String parasha, long prayerMillis) {
        this.eventTimeString = eventTime;
        this.eventTitle = eventTitle;
        this.eventLocation = eventLocation;
        this.type = type;
        this.parasha = parasha;
        this.prayerMillis = prayerMillis;
    }

    public String getParasha() {
        return parasha;
    }

    public void setParasha(String parasha) {
        this.parasha = parasha;
    }

    public long getEventTime() {
        return eventTime;
    }

    public void setEventTime(long eventTime) {
        this.eventTime = eventTime;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public String getEventLocation() {
        return eventLocation;
    }

    public void setEventLocation(String eventLocation) {
        this.eventLocation = eventLocation;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocationObj getLocObj() {
        return locObj;
    }

    public void setLocObj(LocationObj locObj) {
        this.locObj = locObj;
    }

    public long getEventStartTime() {
        return eventStartTime;
    }

    public void setEventStartTime(long eventStartTime) {
        this.eventStartTime = eventStartTime;
    }

    public long getEventEndTime() {
        return eventEndTime;
    }

    public void setEventEndTime(long eventEndTime) {
        this.eventEndTime = eventEndTime;
    }

    public String getEventTimeString() {
        return eventTimeString;
    }

    public void setEventTimeString(String eventTimeString) {
        this.eventTimeString = eventTimeString;
    }

    public ArrayList<CommunityContact> getParticipants() {
        return participants;
    }

    public void setParticipants(ArrayList<CommunityContact> participants) {
        this.participants = participants;
    }

    public String getBuyLink() {
        return buyLink;
    }

    public void setBuyLink(String buyLink) {
        this.buyLink = buyLink;
    }

    public boolean isFree() {
        return isFree;
    }

    public void setIsFree(boolean isFree) {
        this.isFree = isFree;
    }

    public long getPrayerMillis() {
        return prayerMillis;
    }

    public void setPrayerMillis(long prayerMillis) {
        this.prayerMillis = prayerMillis;
    }

    public String getAttendingStatus() {
        return attendingStatus;
    }

    public void setAttendingStatus(String attendingStatus) {
        this.attendingStatus = attendingStatus;
    }

    public String getDailyEventDate() {
        return dailyEventDate;
    }

    public void setDailyEventDate(String dailyEventID) {
        this.dailyEventDate = dailyEventID;
    }
}
