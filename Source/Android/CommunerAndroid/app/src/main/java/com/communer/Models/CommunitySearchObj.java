package com.communer.Models;

/**
 * Created by יובל on 13/10/2015.
 */
public class CommunitySearchObj {

    String id;
    String name;
    String imageURL;
    LocationObj location;

    String phone;
    String email;
    String website;
    String type;

    public CommunitySearchObj(String id, String name, String imageURL, LocationObj location, String phone, String email, String website, String type) {
        this.id = id;
        this.name = name;
        this.imageURL = imageURL;
        this.location = location;
        this.phone = phone;
        this.email = email;
        this.website = website;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public LocationObj getLocation() {
        return location;
    }

    public void setLocation(LocationObj location) {
        this.location = location;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
