package com.example.fimae.models;

import com.example.fimae.activities.PostMode;
import com.example.fimae.adapters.PostAdapter;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ServerTimestamp;


import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class Post {
    private String postId;
    private List<String> postImages;
    private String content;
    private String publisher;
    private PostMode postMode;
    private Map<String, Boolean> likes;
    private int numberOfComments;
    private Map<String, Boolean> saves;
    @ServerTimestamp
    private Date timeCreated;
    private boolean isDeleted;
    private Date timeEdited;
    public  Post(){}

    public Post(String postId, List<String> postImages, String content, String publisher, PostMode postMode, Map<String, Boolean> likes, int numberOfComments, Map<String, Boolean> saves, Date timeCreated, boolean isDeleted, Date timeEdited) {
        this.postId = postId;
        this.postImages = postImages;
        this.content = content;
        this.publisher = publisher;
        this.postMode = postMode;
        this.likes = likes;
        this.numberOfComments = numberOfComments;
        this.saves = saves;
        this.timeCreated = timeCreated;
        this.isDeleted = isDeleted;
        this.timeEdited = timeEdited;
    }

    public Date getTimeEdited() {
        return timeEdited;
    }

    public void setTimeEdited(Date timeEdited) {
        this.timeEdited = timeEdited;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public List<String> getPostImages() {
        return postImages;
    }

    public void setPostImages(List<String> postImages) {
        this.postImages = postImages;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public PostMode getPostMode() {
        return postMode;
    }

    public void setPostMode(PostMode postMode) {
        this.postMode = postMode;
    }

    public Map<String, Boolean> getLikes() {
        return likes;
    }

    public void setLikes(Map<String, Boolean> likes) {
        this.likes = likes;
    }

    public int getNumberOfComments() {
        return numberOfComments;
    }

    public void setNumberOfComments(int numberOfComments) {
        this.numberOfComments = numberOfComments;
    }

    public Map<String, Boolean> getSaves() {
        return saves;
    }

    public void setSaves(Map<String, Boolean> saves) {
        this.saves = saves;
    }

    public Date getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(Date timeCreated) {
        this.timeCreated = timeCreated;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public String getNumberTrue(){
        int count = 0;
        for (Map.Entry<String, Boolean> entry : likes.entrySet()) {
            if (entry.getValue()) {
                count++;
            }
        }
        return String.valueOf(count);
    }
}
