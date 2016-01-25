package com.communer.Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by ���� on 06/08/2015.
 */
public class EventOrMessage implements Parcelable {

    private String eventID;
    private String name;
    private long sTime;
    private long eTime;

    private LocationObj locationObj;
    private ArrayList<CommunityContact> participants;

    private String content;
    private String imageUrl;
    private String readStatus;
    private boolean isFree;

    private String buyLink;
    private String attendingStatus;
    private ArrayList<CommentItem> comments;

    private String type;
    private boolean isActive;
    private String dailyEventDate;

    public EventOrMessage(String eventID, String name, long sTime, long eTime, LocationObj locationObj, ArrayList<CommunityContact> participants, String content, String imageUrl, String readStatus, boolean isFree, String buyLink, String attendingStatus, ArrayList<CommentItem> comments, String type, boolean isActive, String dailyEventDate) {
        this.eventID = eventID;
        this.name = name;
        this.sTime = sTime;
        this.eTime = eTime;
        this.locationObj = locationObj;
        this.participants = participants;
        this.content = content;
        this.imageUrl = imageUrl;
        this.readStatus = readStatus;
        this.isFree = isFree;
        this.buyLink = buyLink;
        this.attendingStatus = attendingStatus;
        this.comments = comments;
        this.type = type;
        this.isActive = isActive;
        this.dailyEventDate = dailyEventDate;
    }

    public EventOrMessage(String type) {
        this.type = type;
    }

    public String getEventID() {
        return eventID;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getsTime() {
        return sTime;
    }

    public void setsTime(long sTime) {
        this.sTime = sTime;
    }

    public long geteTime() {
        return eTime;
    }

    public void seteTime(long eTime) {
        this.eTime = eTime;
    }

    public LocationObj getLocationObj() {
        return locationObj;
    }

    public void setLocationObj(LocationObj locationObj) {
        this.locationObj = locationObj;
    }

    public ArrayList<CommunityContact> getParticipants() {
        return participants;
    }

    public void setParticipants(ArrayList<CommunityContact> participants) {
        this.participants = participants;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getReadStatus() {
        return readStatus;
    }

    public void setReadStatus(String readStatus) {
        this.readStatus = readStatus;
    }

    public boolean isFree() {
        return isFree;
    }

    public void setIsFree(boolean isFree) {
        this.isFree = isFree;
    }

    public String getBuyLink() {
        return buyLink;
    }

    public void setBuyLink(String buyLink) {
        this.buyLink = buyLink;
    }

    public String getAttendingStatus() {
        return attendingStatus;
    }

    public void setAttendingStatus(String attendingStatus) {
        this.attendingStatus = attendingStatus;
    }

    public ArrayList<CommentItem> getComments() {
        return comments;
    }

    public void setComments(ArrayList<CommentItem> comments) {
        this.comments = comments;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public String getDailyEventDate() {
        return dailyEventDate;
    }

    public void setDailyEventDate(String dailyEventDate) {
        this.dailyEventDate = dailyEventDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.eventID);
        dest.writeString(this.name);
        dest.writeLong(this.sTime);
        dest.writeLong(this.eTime);
        dest.writeParcelable(this.locationObj, 0);
        dest.writeSerializable(this.participants);
        dest.writeString(this.content);
        dest.writeString(this.imageUrl);
        dest.writeString(this.readStatus);
        dest.writeByte(isFree ? (byte) 1 : (byte) 0);
        dest.writeString(this.buyLink);
        dest.writeString(this.attendingStatus);
        dest.writeSerializable(this.comments);
        dest.writeString(this.type);
        dest.writeByte(isActive ? (byte) 1 : (byte) 0);
        dest.writeString(this.dailyEventDate);
    }

    private EventOrMessage(Parcel in) {
        this.eventID = in.readString();
        this.name = in.readString();
        this.sTime = in.readLong();
        this.eTime = in.readLong();
        this.locationObj = in.readParcelable(LocationObj.class.getClassLoader());
        this.participants = (ArrayList<CommunityContact>) in.readSerializable();
        this.content = in.readString();
        this.imageUrl = in.readString();
        this.readStatus = in.readString();
        this.isFree = in.readByte() != 0;
        this.buyLink = in.readString();
        this.attendingStatus = in.readString();
        this.comments = (ArrayList<CommentItem>) in.readSerializable();
        this.type = in.readString();
        this.isActive = in.readByte() != 0;
        this.dailyEventDate = in.readString();
    }

    public static final Creator<EventOrMessage> CREATOR = new Creator<EventOrMessage>() {
        public EventOrMessage createFromParcel(Parcel source) {
            return new EventOrMessage(source);
        }

        public EventOrMessage[] newArray(int size) {
            return new EventOrMessage[size];
        }
    };
}
