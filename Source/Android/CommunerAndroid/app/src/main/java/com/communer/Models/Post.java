package com.communer.Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ���� on 06/08/2015.
 */
public class Post implements Parcelable {

    private String postID;
    private String title;
    private String imageUrl;
    private CommunityMember member;
    private Long CDate;
    private String content;
    private ArrayList<CommentItem> comments;

    public Post(String postID, String title, String imageUrl, CommunityMember member, Long CDate, String content, ArrayList<CommentItem> comments) {
        this.postID = postID;
        this.title = title;
        this.imageUrl = imageUrl;
        this.member = member;
        this.CDate = CDate;
        this.content = content;
        this.comments = comments;
    }

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public CommunityMember getMember() {
        return member;
    }

    public void setMember(CommunityMember member) {
        this.member = member;
    }

    public Long getCDate() {
        return CDate;
    }

    public void setCDate(Long CDate) {
        this.CDate = CDate;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ArrayList<CommentItem> getComments() {
        return comments;
    }

    public void setComments(ArrayList<CommentItem> comments) {
        this.comments = comments;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.postID);
        dest.writeString(this.title);
        dest.writeString(this.imageUrl);
        dest.writeParcelable(this.member, 0);
        dest.writeValue(this.CDate);
        dest.writeString(this.content);
        dest.writeList(this.comments);
    }

    protected Post(Parcel in) {
        this.postID = in.readString();
        this.title = in.readString();
        this.imageUrl = in.readString();
        this.member = in.readParcelable(CommunityMember.class.getClassLoader());
        this.CDate = (Long) in.readValue(Long.class.getClassLoader());
        this.content = in.readString();
        this.comments = new ArrayList<CommentItem>();
        in.readList(this.comments, List.class.getClassLoader());
    }

    public static final Parcelable.Creator<Post> CREATOR = new Parcelable.Creator<Post>() {
        public Post createFromParcel(Parcel source) {
            return new Post(source);
        }

        public Post[] newArray(int size) {
            return new Post[size];
        }
    };
}
