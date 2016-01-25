package com.communer.Models;

public class JoinCommunityItem {

    private String communityID;
    private boolean asGuest;

    public JoinCommunityItem(String communityID, boolean asGuest) {
        this.communityID = communityID;
        this.asGuest = asGuest;
    }

    public String getCommunityID() {
        return communityID;
    }

    public void setCommunityID(String communityID) {
        this.communityID = communityID;
    }

    public boolean isAsGuest() {
        return asGuest;
    }

    public void setAsGuest(boolean asGuest) {
        this.asGuest = asGuest;
    }
}
