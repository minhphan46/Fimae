package com.example.fimae.models;

import java.util.Date;
import java.util.Map;

public abstract class CommentBase {
    protected Date timeCreated;
    protected Map<String, Boolean> likes;
    protected String publisher;
    private String id;
    private String content;
    private Date timeEdited;
    public CommentBase(Date timeCreated, Map<String, Boolean> likes, String publisher, String id, String content, Date timeEdited) {
        this.timeCreated = timeCreated;
        this.likes = likes;
        this.publisher = publisher;
        this.id = id;
        this.content = content;
        this.timeEdited = timeEdited;
    }

    public CommentBase() {

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
