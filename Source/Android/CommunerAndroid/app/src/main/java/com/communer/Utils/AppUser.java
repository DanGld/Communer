package com.communer.Utils;

import com.communer.Models.CalendarEventItem;
import com.communer.Models.CommunityMember;
import com.communer.Models.CommunityObj;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Map;

/**
 * Created by ���� on 06/08/2015.
 */
public class AppUser{

    private static AppUser mInstance = null;

    private static String userFirstName = "";
    private static String userLastName = "";
    private static String userGender = "";

    private static String userStatus = "";
    private static Long userBday = Long.valueOf(0);
    private static String userEmail = "";
    private static String userImageUrl = "";
    private static String supportMail = "";
    private static boolean isRabai;

    private static CommunityObj currentCommunity;
    private static ArrayList<CommunityObj> userCommunities;
    private static String userHash = "";

    private static Map<CalendarDay,ArrayList<CalendarEventItem>> eventsMap;
    private static HashSet<CalendarDay> eventsDays;

    private static CommunityMember userAsMember;

//    private static String BaseUrl = "http://107.178.211.246/selfow/server/1696164739/";
//    private static String BaseUrl = "http://93.188.166.107/selfow/server/1696164739/";
    private static String BaseUrl = "http://smartcom224.ddns.net/selfow/server/1696164739/";

    private AppUser(){
    }

    public static AppUser getInstance(){
        if(mInstance == null){
            mInstance = new AppUser();
        }
        return mInstance;
    }

    public static String getUserFirstName() {
        return userFirstName;
    }

    public static void setUserFirstName(String userFirstName) {
        AppUser.userFirstName = userFirstName;
    }

    public static String getUserLastName() {
        return userLastName;
    }

    public static void setUserLastName(String userLastName) {
        AppUser.userLastName = userLastName;
    }

    public static String getUserGender() {
        return userGender;
    }

    public static void setUserGender(String userGender) {
        AppUser.userGender = userGender;
    }

    public static String getUserStatus() {
        return userStatus;
    }

    public static void setUserStatus(String userStatus) {
        AppUser.userStatus = userStatus;
    }

    public static Long getUserBday() {
        return userBday;
    }

    public static void setUserBday(Long userBday) {
        AppUser.userBday = userBday;
    }

    public static String getUserEmail() {
        return userEmail;
    }

    public static void setUserEmail(String userEmail) {
        AppUser.userEmail = userEmail;
    }

    public static String getUserImageUrl() {
        return userImageUrl;
    }

    public static void setUserImageUrl(String userImageUrl) {
        AppUser.userImageUrl = userImageUrl;
    }

    public static ArrayList<CommunityObj> getUserCommunities() {
        return userCommunities;
    }

    public static void setUserCommunities(ArrayList<CommunityObj> userCommunities) {
        AppUser.userCommunities = userCommunities;
    }

    public static CommunityObj getCurrentCommunity() {
        return currentCommunity;
    }

    public static void setCurrentCommunity(CommunityObj currentCommunity) {
        AppUser.currentCommunity = currentCommunity;
    }

    public static String getUserHash() {
        return userHash;
    }

    public static void setUserHash(String userHash) {
        AppUser.userHash = userHash;
    }

    public static CommunityMember getUserAsMember() {
        return userAsMember;
    }

    public static void setUserAsMember(CommunityMember userAsMember) {
        AppUser.userAsMember = userAsMember;
    }

    public static boolean isRabai() {
        return isRabai;
    }

    public static void setIsRabai(boolean isRabai) {
        AppUser.isRabai = isRabai;
    }

    public static String getDate(long milliSeconds, String dateFormat) {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    public static String getSupportMail() {
        return supportMail;
    }

    public static void setSupportMail(String supportMail) {
        AppUser.supportMail = supportMail;
    }

    public static String getBaseUrl() {
        return BaseUrl;
    }

    public static void setBaseUrl(String baseUrl) {
        BaseUrl = baseUrl;
    }

    public static Map<CalendarDay, ArrayList<CalendarEventItem>> getEventsMap() {
        return eventsMap;
    }

    public static void setEventsMap(Map<CalendarDay, ArrayList<CalendarEventItem>> eventsMap) {
        AppUser.eventsMap = eventsMap;
    }

    public static HashSet<CalendarDay> getEventsDays() {
        return eventsDays;
    }

    public static void setEventsDays(HashSet<CalendarDay> eventsDays) {
        AppUser.eventsDays = eventsDays;
    }
}
