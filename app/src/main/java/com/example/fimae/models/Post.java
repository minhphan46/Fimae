package com.example.fimae.models;

import com.example.fimae.adapters.PostAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Post {
    private String postId;
    private List<String> postImages;
    private String content;
    private String publisher;

    public Post(String postId, List<String> postImages, String content, String publisher) {
        this.postId = postId;
        this.postImages = postImages;
        this.content = content;
        this.publisher = publisher;
    }

    public  Post(){}

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

}
