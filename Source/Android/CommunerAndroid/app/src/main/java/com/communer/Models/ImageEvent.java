package com.communer.Models;

public class ImageEvent {

    private String imageUrl;
    private String galleryTitle;
    private String galleryID;
    private MediaData media;

    public ImageEvent(String imageUrl, String galleryTitle, String galleryID, MediaData media) {
        this.imageUrl = imageUrl;
        this.galleryTitle = galleryTitle;
        this.galleryID = galleryID;
        this.media = media;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }


    public MediaData getMedia() {
        return media;
    }

    public void setMedia(MediaData media) {
        this.media = media;
    }

    public String getGalleryTitle() {
        return galleryTitle;
    }

    public void setGalleryTitle(String galleryTitle) {
        this.galleryTitle = galleryTitle;
    }

    public String getGalleryID() {
        return galleryID;
    }

    public void setGalleryID(String galleryID) {
        this.galleryID = galleryID;
    }
}
