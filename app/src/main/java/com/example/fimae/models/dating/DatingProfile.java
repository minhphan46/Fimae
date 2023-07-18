package com.example.fimae.models.dating;


import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.ArrayList;
import java.util.Date;

public class DatingProfile {
    private String id;
    private @ServerTimestamp Date timeCreated;
    private String uid;
    private String description;
    private LatLng location;
    private int distance;
    private int minAge;
    private int maxAge;
    private int minHeight;
    private int maxHeight;
    private RelationshipType relationshipType;
    private EducationalLevel educationalLevel;
    private boolean isEnable;
    private boolean isVerified;
    private ArrayList<String> genres;
    private ArrayList<String> images;
    private ArrayList<String> likedUsers;
    private ArrayList<String> dislikedUsers;

    public DatingProfile() {
        genres = new ArrayList<>();
        likedUsers = new ArrayList<>();
        dislikedUsers = new ArrayList<>();
        isEnable = true;
        isVerified = false;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(Date timeCreated) {
        this.timeCreated = timeCreated;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getMinAge() {
        return minAge;
    }

    public void setMinAge(int minAge) {
        this.minAge = minAge;
    }

    public int getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(int maxAge) {
        this.maxAge = maxAge;
    }

    public int getMinHeight() {
        return minHeight;
    }

    public void setMinHeight(int minHeight) {
        this.minHeight = minHeight;
    }

    public int getMaxHeight() {
        return maxHeight;
    }

    public void setMaxHeight(int maxHeight) {
        this.maxHeight = maxHeight;
    }

    public RelationshipType getRelationshipType() {
        return relationshipType;
    }

    public void setRelationshipType(RelationshipType relationshipType) {
        this.relationshipType = relationshipType;
    }

    public EducationalLevel getEducationalLevel() {
        return educationalLevel;
    }

    public void setEducationalLevel(EducationalLevel educationalLevel) {
        this.educationalLevel = educationalLevel;
    }

    public boolean isEnable() {
        return isEnable;
    }

    public void setEnable(boolean enable) {
        isEnable = enable;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean verified) {
        isVerified = verified;
    }

    public ArrayList<String> getGenres() {
        return genres;
    }

    public void setGenres(ArrayList<String> genres) {
        this.genres = genres;
    }

    public ArrayList<String> getImages() {
        return images;
    }

    public void setImages(ArrayList<String> images) {
        this.images = images;
    }

    public ArrayList<String> getLikedUsers() {
        return likedUsers;
    }

    public void setLikedUsers(ArrayList<String> likedUsers) {
        this.likedUsers = likedUsers;
    }

    public ArrayList<String> getDislikedUsers() {
        return dislikedUsers;
    }

    public void setDislikedUsers(ArrayList<String> dislikedUsers) {
        this.dislikedUsers = dislikedUsers;
    }
}
