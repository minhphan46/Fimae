package com.example.fimae.models.dating;


import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.ServerTimestamp;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Date;

public class DatingProfile {
    private String id;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("url")
    @Expose
    private String imageUrl;

    @SerializedName("age")
    @Expose
    private Integer age;

    @SerializedName("location")
    @Expose
    private String locationName;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    private transient @ServerTimestamp Date timeCreated;
    private transient String uid;
    private transient String description;
    private transient LatLng location;
    private transient int distance;
    private transient int minAge;
    private transient int maxAge;
    private transient int minHeight;
    private transient int maxHeight;
    private transient RelationshipType relationshipType;
    private transient EducationalLevel educationalLevel;
    private transient boolean isEnable;
    private transient boolean isVerified;
    private transient ArrayList<String> genres;
    private transient ArrayList<String> images;
    private transient ArrayList<String> likedUsers;
    private transient ArrayList<String> dislikedUsers;

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
