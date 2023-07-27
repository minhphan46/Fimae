package com.example.fimae.models;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Relationships {
    String followerId;
    String followingId;
    @ServerTimestamp
    Date timeCreated;
    public Relationships(){

    }
    public Relationships(String followerId, String followingId, Date timeCreated) {
        this.followerId = followerId;
        this.followingId = followingId;
        this.timeCreated = timeCreated;
    }

    public String getFollowerId() {
        return followerId;
    }

    public void setFollowerId(String followerId) {
        this.followerId = followerId;
    }

    public String getFollowingId() {
        return followingId;
    }

    public void setFollowingId(String followingId) {
        this.followingId = followingId;
    }

    public Date getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(Date timeCreated) {
        this.timeCreated = timeCreated;
    }
}
