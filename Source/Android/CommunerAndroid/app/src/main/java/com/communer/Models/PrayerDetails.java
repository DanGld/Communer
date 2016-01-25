package com.communer.Models;

import java.util.ArrayList;

public class PrayerDetails {

    private Long date;
    private String parashaName;
    private ArrayList<SmallPrayer> shabatPrayers;

    private String holidayStart;
    private String holidayEnd;
    private ArrayList<SmallPrayer> smallPrayers;

    private ArrayList<String> additions;

    public PrayerDetails(Long date, String parashaName, ArrayList<SmallPrayer> shabatPrayers, String holidayStart, String holidayEnd, ArrayList<SmallPrayer> smallPrayers, ArrayList<String> additions) {
        this.date = date;
        this.parashaName = parashaName;
        this.shabatPrayers = shabatPrayers;
        this.holidayStart = holidayStart;
        this.holidayEnd = holidayEnd;
        this.smallPrayers = smallPrayers;
        this.additions = additions;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public String getParashaName() {
        return parashaName;
    }

    public void setParashaName(String parashaName) {
        this.parashaName = parashaName;
    }

    public ArrayList<SmallPrayer> getShabatPrayers() {
        return shabatPrayers;
    }

    public void setShabatPrayers(ArrayList<SmallPrayer> shabatPrayers) {
        this.shabatPrayers = shabatPrayers;
    }

    public ArrayList<SmallPrayer> getSmallPrayers() {
        return smallPrayers;
    }

    public void setSmallPrayers(ArrayList<SmallPrayer> smallPrayers) {
        this.smallPrayers = smallPrayers;
    }

    public ArrayList<String> getAdditions() {
        return additions;
    }

    public void setAdditions(ArrayList<String> additions) {
        this.additions = additions;
    }

    public String getHolidayStart() {
        return holidayStart;
    }

    public void setHolidayStart(String holidayStart) {
        this.holidayStart = holidayStart;
    }

    public String getHolidayEnd() {
        return holidayEnd;
    }

    public void setHolidayEnd(String holidayEnd) {
        this.holidayEnd = holidayEnd;
    }
}
