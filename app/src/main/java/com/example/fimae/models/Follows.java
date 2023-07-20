package com.example.fimae.models;

public class Follows {
    private String follower;
    private String following;
    private String id;

    public Follows(String follower, String following, String id) {
        this.follower = follower;
        this.following = following;
        this.id = id;
    }
    public Follows(){

    }
    public String getFollower() {
        return follower;
    }

    public void setFollower(String follower) {
        this.follower = follower;
    }

    public String getFollowing() {
        return following;
    }

    public void setFollowing(String following) {
        this.following = following;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
