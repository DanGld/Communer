package com.communer.Models;

import java.util.Date;

public class PostCommentedItem {

    private String postID;
    private String content;
    private Date imageData;

    public PostCommentedItem(String postID, String content, Date imageData) {
        this.postID = postID;
        this.content = content;
        this.imageData = imageData;
    }

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getImageData() {
        return imageData;
    }

    public void setImageData(Date imageData) {
        this.imageData = imageData;
    }
}
