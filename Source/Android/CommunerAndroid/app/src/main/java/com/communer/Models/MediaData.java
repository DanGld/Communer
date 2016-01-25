package com.communer.Models;

import java.util.ArrayList;

public class MediaData {

    private ArrayList<GalleryImage> images;
    private ArrayList<String> videos;

    public MediaData(ArrayList<GalleryImage> images, ArrayList<String> videos) {
        this.images = images;
        this.videos = videos;
    }

    public ArrayList<GalleryImage> getImages() {
        return images;
    }

    public void setImages(ArrayList<GalleryImage> images) {
        this.images = images;
    }

    public ArrayList<String> getVideos() {
        return videos;
    }

    public void setVideos(ArrayList<String> videos) {
        this.videos = videos;
    }
}
