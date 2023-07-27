package com.example.fimae.models;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class Comment {
    @ServerTimestamp
    protected Date timeCreated;
    protected Map<String, Boolean> likes;
    protected String publisher;
    private String id;
    private String content;
    private String parentId;
    private List<String> children;
    private String postId;
//    @ServerTimestamp
    private Date timeEdited;

    public Comment(Date timeCreated, Map<String, Boolean> likes, String publisher, String id, String content, String parentId, List<String> children, String postId, Date timeEdited) {
        this.timeCreated = timeCreated;
        this.likes = likes;
        this.publisher = publisher;
        this.id = id;
        this.content = content;
        this.parentId = parentId;
        this.children = children;
        this.postId = postId;
        this.timeEdited = timeEdited;
    }

    public Comment() {

    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public List<String> getChildren() {
        return children;
    }

    public void setChildren(List<String> children) {
        this.children = children;
    }

    public Date getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(Date timeCreated) {
        this.timeCreated = timeCreated;
    }

    public Map<String, Boolean> getLikes() {
        return likes;
    }

    public void setLikes(Map<String, Boolean> likes) {
        this.likes = likes;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getTimeEdited() {
        return timeEdited;
    }

    public void setTimeEdited(Date timeEdited) {
        this.timeEdited = timeEdited;
    }
}
