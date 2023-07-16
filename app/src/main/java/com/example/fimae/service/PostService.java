package com.example.fimae.service;

import java.util.Map;

public class PostService {
    public static PostService getInstance(){
        if(postService == null){
            postService = new PostService();
        }
        return postService;
    }
    static PostService postService;

    public int getNumberOfLikes(Map<String, Boolean> likes){
        int count = 0;
        for(Map.Entry<String, Boolean> entry: likes.entrySet()){
            if(entry.getValue()){
                count++;
            }
        }
        return count;
    }
}
