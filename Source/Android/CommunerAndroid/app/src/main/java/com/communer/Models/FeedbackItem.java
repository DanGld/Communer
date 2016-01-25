package com.communer.Models;

public class FeedbackItem {

    private String topic;
    private String title;
    private String description;

    public FeedbackItem(String topic, String title, String description) {
        this.topic = topic;
        this.title = title;
        this.description = description;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
