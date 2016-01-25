package com.communer.Models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by יובל on 01/11/2015.
 */
public class GalleryImage implements Parcelable {

    private String id, url;

    public GalleryImage(String id, String url) {
        this.id = id;
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.url);
    }

    protected GalleryImage(Parcel in) {
        this.id = in.readString();
        this.url = in.readString();
    }

    public static final Parcelable.Creator<GalleryImage> CREATOR = new Parcelable.Creator<GalleryImage>() {
        public GalleryImage createFromParcel(Parcel source) {
            return new GalleryImage(source);
        }

        public GalleryImage[] newArray(int size) {
            return new GalleryImage[size];
        }
    };
}
