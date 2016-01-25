package com.communer.Models;

import java.util.ArrayList;

public class CommunityObj {

    private String communityID;
    private String communityName;
    private String communityImageUrl;
    private LocationObj location;
    private String memberType;

    private CommunityObj roofOrg;
    private boolean isSynagogue;
    private ArrayList<CommunityMember> members;
    private ArrayList<CommunityContact> communityContacts;

    private String fax;
    private String email;
    private String website;

    private ArrayList<Post> privateQA;
    private ArrayList<Post> publicQA;
    private ArrayList<Post> communityPosts;

    private ArrayList<CommunityActivityOrFacility> facilities;
    private boolean hebrewSupport;

    private ArrayList<ImageEvent> galleries;
    private ArrayList<SecurityEvent> securityEvents;
    private ArrayList<GuideCategory> guideCategories;

    private ArrayList<EventOrMessage> announcements;
    private ArrayList<DailyEvents> dailyEvents;

    private ArrayList<String> visibleModules;
    private ArrayList<String> widgets;

    private String pusherChannel;
    private String pushWooshTag;

    private int unreadMessagesCount;

    public CommunityObj(String communityID, String communityName, String communityImageUrl, LocationObj location, String memberType, CommunityObj roofOrg, boolean isSynagogue, ArrayList<CommunityMember> members, ArrayList<CommunityContact> communityContacts, String fax, String email, String website, ArrayList<Post> privateQA, ArrayList<Post> publicQA, ArrayList<Post> communityPosts, ArrayList<CommunityActivityOrFacility> facilities, boolean hebrewSupport, ArrayList<ImageEvent> galleries, ArrayList<SecurityEvent> securityEvents, ArrayList<GuideCategory> guideCategories, ArrayList<EventOrMessage> announcements, ArrayList<DailyEvents> dailyEvents, ArrayList<String> visibleModules, ArrayList<String> widgets, String pusherChannel, String pushWooshTag, int unreadMessagesCount) {
        this.communityID = communityID;
        this.communityName = communityName;
        this.communityImageUrl = communityImageUrl;
        this.location = location;
        this.memberType = memberType;
        this.roofOrg = roofOrg;
        this.isSynagogue = isSynagogue;
        this.members = members;
        this.communityContacts = communityContacts;
        this.fax = fax;
        this.email = email;
        this.website = website;
        this.privateQA = privateQA;
        this.publicQA = publicQA;
        this.communityPosts = communityPosts;
        this.facilities = facilities;
        this.hebrewSupport = hebrewSupport;
        this.galleries = galleries;
        this.securityEvents = securityEvents;
        this.guideCategories = guideCategories;
        this.announcements = announcements;
        this.dailyEvents = dailyEvents;
        this.visibleModules = visibleModules;
        this.widgets = widgets;
        this.pusherChannel = pusherChannel;
        this.pushWooshTag = pushWooshTag;
        this.unreadMessagesCount = unreadMessagesCount;
    }

    public String getCommunityID() {
        return communityID;
    }

    public void setCommunityID(String communityID) {
        this.communityID = communityID;
    }

    public String getCommunityName() {
        return communityName;
    }

    public void setCommunityName(String communityName) {
        this.communityName = communityName;
    }

    public String getCommunityImageUrl() {
        return communityImageUrl;
    }

    public void setCommunityImageUrl(String communityImageUrl) {
        this.communityImageUrl = communityImageUrl;
    }

    public LocationObj getLocation() {
        return location;
    }

    public void setLocation(LocationObj location) {
        this.location = location;
    }

    public String getMemberType() {
        return memberType;
    }

    public void setMemberType(String mmbrType) {
        this.memberType = mmbrType;
    }

    public CommunityObj getRoofOrg() {
        return roofOrg;
    }

    public void setRoofOrg(CommunityObj roofOrg) {
        this.roofOrg = roofOrg;
    }

    public boolean isSynagogue() {
        return isSynagogue;
    }

    public void setIsSynagogue(boolean isSynagogue) {
        this.isSynagogue = isSynagogue;
    }

    public ArrayList<CommunityMember> getMembers() {
        return members;
    }

    public void setMembers(ArrayList<CommunityMember> members) {
        this.members = members;
    }

    public ArrayList<CommunityContact> getCommunityContacts() {
        return communityContacts;
    }

    public void setCommunityContacts(ArrayList<CommunityContact> communityContacts) {
        this.communityContacts = communityContacts;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public ArrayList<Post> getPrivateQA() {
        return privateQA;
    }

    public void setPrivateQA(ArrayList<Post> privateQA) {
        this.privateQA = privateQA;
    }

    public ArrayList<Post> getPublicQA() {
        return publicQA;
    }

    public void setPublicQA(ArrayList<Post> publicQA) {
        this.publicQA = publicQA;
    }

    public ArrayList<Post> getCommunityPosts() {
        return communityPosts;
    }

    public void setCommunityPosts(ArrayList<Post> communityPosts) {
        this.communityPosts = communityPosts;
    }

    public ArrayList<CommunityActivityOrFacility> getFacilities() {
        return facilities;
    }

    public void setFacilities(ArrayList<CommunityActivityOrFacility> facilities) {
        this.facilities = facilities;
    }

    public boolean isHebrewSupport() {
        return hebrewSupport;
    }

    public void setHebrewSupport(boolean hebrewSupport) {
        this.hebrewSupport = hebrewSupport;
    }

    public ArrayList<ImageEvent> getGalleries() {
        return galleries;
    }

    public void setGalleries(ArrayList<ImageEvent> galleries) {
        this.galleries = galleries;
    }

    public ArrayList<SecurityEvent> getSecurityEvents() {
        return securityEvents;
    }

    public void setSecurityEvents(ArrayList<SecurityEvent> securityEvents) {
        this.securityEvents = securityEvents;
    }

    public ArrayList<GuideCategory> getGuideCategories() {
        return guideCategories;
    }

    public void setGuideCategories(ArrayList<GuideCategory> guideCategories) {
        this.guideCategories = guideCategories;
    }

    public ArrayList<DailyEvents> getDailyEvents() {
        return dailyEvents;
    }

    public void setDailyEvents(ArrayList<DailyEvents> dailyEvents) {
        this.dailyEvents = dailyEvents;
    }

    public ArrayList<EventOrMessage> getAnnouncements() {
        return announcements;
    }

    public void setAnnouncements(ArrayList<EventOrMessage> announcements) {
        this.announcements = announcements;
    }

    public ArrayList<String> getVisibleModules() {
        return visibleModules;
    }

    public void setVisibleModules(ArrayList<String> visibleModules) {
        this.visibleModules = visibleModules;
    }

    public ArrayList<String> getWidgets() {
        return widgets;
    }

    public void setWidgets(ArrayList<String> widgets) {
        this.widgets = widgets;
    }

    public String getPusherChannel() {
        return pusherChannel;
    }

    public void setPusherChannel(String pusherChannel) {
        this.pusherChannel = pusherChannel;
    }

    public String getPushWooshTag() {
        return pushWooshTag;
    }

    public void setPushWooshTag(String pushWooshTag) {
        this.pushWooshTag = pushWooshTag;
    }

    public int getUnreadMessagesCount() {
        return unreadMessagesCount;
    }

    public void setUnreadMessagesCount(int unreadMessagesCount) {
        this.unreadMessagesCount = unreadMessagesCount;
    }
}
