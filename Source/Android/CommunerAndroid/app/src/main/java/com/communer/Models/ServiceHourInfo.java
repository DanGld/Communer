package com.communer.Models;

public class ServiceHourInfo {

    private String title;
    private String hour;

    public ServiceHourInfo(String title, String hour) {
        this.title = title;
        this.hour = hour;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }
}
