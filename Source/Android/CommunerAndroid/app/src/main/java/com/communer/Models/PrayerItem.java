package com.communer.Models;

import java.util.ArrayList;
import java.util.Date;

public class PrayerItem {

    private ArrayList<String> times;
    private String name;
    private String type;
    private Date date;

    public PrayerItem(ArrayList<String> times, String name, String type, Date date) {
        this.times = times;
        this.name = name;
        this.type = type;
        this.date = date;
    }

    public ArrayList<String> getTimes() {
        return times;
    }

    public void setTimes(ArrayList<String> times) {
        this.times = times;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
