package com.example.fimae.models.dating;


import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.ArrayList;
import java.util.Date;

public class DatingProfile {
    private String id;
    private @ServerTimestamp Date timeCreated;
    private String uid;
    private LatLng location;

    private String geoHash;

    private int distance;
    private int minAge;
    private int maxAge;
    private int minHeight;
    private int maxHeight;
    private GenderOptions genderOptions;
    private RelationshipType relationshipType;
    private EducationalLevel educationalLevel;
    private boolean isEnable;
    private ArrayList<String> images;
    private ArrayList<String> likedUsers;
    private ArrayList<String> dislikedUsers;

    private String age;

    private boolean gender;

    private String name;

    private String locationName;

    private double distanceFromYou;

    public DatingProfile() {
        likedUsers = new ArrayList<>();
        dislikedUsers = new ArrayList<>();
        isEnable = true;
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

    public String getGeoHash() {
        if(geoHash == null)
        {
            String hash = GeoFireUtils
                    .getGeoHashForLocation(new GeoLocation(location.getLatitude(), location.getLongitude()));
            setGeoHash(hash);
        }
        return geoHash;
    }

    public void setGeoHash(String geoHash) {
        this.geoHash = geoHash;
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

    public GenderOptions getGenderOptions() {
        return genderOptions;
    }

    public void setGenderOptions(GenderOptions genderOptions) {
        this.genderOptions = genderOptions;
    }
    @Exclude
    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }
    @Exclude
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    @Exclude
    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    @Exclude
    public double getDistanceFromYou() {
        return distanceFromYou;
    }

    @Exclude
    public boolean isGender() {
        return gender;
    }

    public void setGender(boolean gender) {
        this.gender = gender;
    }

    public void setDistanceFromYou(double distanceFromYou) {
        this.distanceFromYou = distanceFromYou;
    }

    public void addUserLike(String value)
    {
        if(likedUsers == null)
        {
            likedUsers = new ArrayList<>();
        }
        if(!likedUsers.contains(value))
        {
            likedUsers.add(value);
        }
    }
    public void addUserDislike(String value)
    {
        if(dislikedUsers == null)
        {
            dislikedUsers = new ArrayList<>();
        }
        if(!dislikedUsers.contains(value))
        {
            dislikedUsers.add(value);
        }
    }
}
