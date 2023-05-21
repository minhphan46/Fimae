package com.example.fimae.models;

import java.util.List;

public class Comment {

    private String comment;
    private String publisher;
    private String postId;
    private String commentId;
    private String idParent;
    private List<Comment> subComment;
    public Comment() {
    }

    public Comment(String comment, String publisher, String postId, String commentId, String idParent, List<Comment> subComment) {
        this.comment = comment;
        this.publisher = publisher;
        this.postId = postId;
        this.commentId = commentId;
        this.idParent = idParent;
        this.subComment = subComment;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getIdParent() {
        return idParent;
    }

    public void setIdParent(String idParent) {
        this.idParent = idParent;
    }

    public List<Comment> getSubComment() {
        return subComment;
    }

    public void setSubComment(List<Comment> subComment) {
        this.subComment = subComment;
    }
}

