package com.example.fimae.models;

import com.google.firebase.Timestamp;

import java.sql.Time;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class Comment extends CommentBase {
    private String postId;

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public Comment(Date timeCreated, Map<String, Boolean> likes, String publisher, String id, String content, String postId, Date timeEdited) {
        super(timeCreated, likes, publisher, id, content, timeEdited);
        this.postId = postId;
    }

    public Comment() {
    }
}

