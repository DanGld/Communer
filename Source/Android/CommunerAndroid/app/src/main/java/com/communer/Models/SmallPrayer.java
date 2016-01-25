package com.communer.Models;

public class SmallPrayer {

    private String time;
    private String title;
    private String type;
    private String id;
    private long prayerInMillis;

    public SmallPrayer(String time, String title, String type, String id, long prayerMillis) {
        this.time = time;
        this.title = title;
        this.type = type;
        this.id = id;
        this.prayerInMillis = prayerMillis;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public long getPrayerInMillis() {
        return prayerInMillis;
    }

    public void setPrayerInMillis(long prayerInMillis) {
        this.prayerInMillis = prayerInMillis;
    }
}
