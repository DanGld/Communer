package com.communer.Models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ���� on 06/08/2015.
 */
public class CommentItem implements Parcelable {

    private String postID;
    private String imageUrl;
    private Long date;
    private String content;
    private CommunityMember mmbrCommented;

    public CommentItem(String postID, String imageUrl, Long date, String content, CommunityMember mmbrCommented) {
        this.postID = postID;
        this.imageUrl = imageUrl;
        this.date = date;
        this.content = content;
        this.mmbrCommented = mmbrCommented;
    }

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public CommunityMember getMmbrCommented() {
        return mmbrCommented;
    }

    public void setMmbrCommented(CommunityMember mmbrCommented) {
        this.mmbrCommented = mmbrCommented;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.postID);
        dest.writeString(this.imageUrl);
        dest.writeValue(this.date);
        dest.writeString(this.content);
        dest.writeParcelable(this.mmbrCommented, 0);
    }

    protected CommentItem(Parcel in) {
        this.postID = in.readString();
        this.imageUrl = in.readString();
        this.date = (Long) in.readValue(Long.class.getClassLoader());
        this.content = in.readString();
        this.mmbrCommented = in.readParcelable(CommunityMember.class.getClassLoader());
    }

    public static final Parcelable.Creator<CommentItem> CREATOR = new Parcelable.Creator<CommentItem>() {
        public CommentItem createFromParcel(Parcel source) {
            return new CommentItem(source);
        }

        public CommentItem[] newArray(int size) {
            return new CommentItem[size];
        }
    };
}
