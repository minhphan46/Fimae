package com.example.fimae.models.shorts;

import com.example.fimae.activities.PostMode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.ArrayList;
import java.util.Date;

public class ShortMedia {
    private String id;
    private String publisher;
    private String description;
    private Object mediaPath;
    private ShortMediaType type;
    private PostMode postMode;
    @ServerTimestamp
    private Date timeCreated;
    private boolean allowComment;
    private int numOfComments;
    private ArrayList<String> usersLiked;

    public ShortMedia() {
    }
    public static ShortMedia createShortVideo(String id, String description, String videoPath, PostMode postMode, boolean allowComment) {
        ShortMedia shortMedia = new ShortMedia();
        shortMedia.setId(id);
        shortMedia.setPublisher(FirebaseAuth.getInstance().getUid());
        shortMedia.setDescription(description);
        shortMedia.setMediaPath(videoPath);
        shortMedia.setType(ShortMediaType.VIDEO);
        shortMedia.setPostMode(postMode);
        shortMedia.setAllowComment(allowComment);
        shortMedia.setUsersLiked(new ArrayList<>());
        return shortMedia;
    }
    public static ShortMedia createShortImages(String id, String description, ArrayList<String> imagesPath, PostMode postMode, boolean allowComment) {
        ShortMedia shortMedia = new ShortMedia();
        shortMedia.setId(id);
        shortMedia.setPublisher(FirebaseAuth.getInstance().getUid());
        shortMedia.setDescription(description);
        shortMedia.setMediaPath(imagesPath);
        shortMedia.setType(ShortMediaType.IMAGES);
        shortMedia.setPostMode(postMode);
        shortMedia.setAllowComment(allowComment);
        shortMedia.setUsersLiked(new ArrayList<>());
        return shortMedia;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Object getMediaPath() {
        return mediaPath;
    }

    public void setMediaPath(Object mediaPath) {
        this.mediaPath = mediaPath;
    }

    public ShortMediaType getType() {
        return type;
    }

    public void setType(ShortMediaType type) {
        this.type = type;
    }

    public PostMode getPostMode() {
        return postMode;
    }

    public void setPostMode(PostMode postMode) {
        this.postMode = postMode;
    }

    public Date getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(Date timeCreated) {
        this.timeCreated = timeCreated;
    }

    public boolean isAllowComment() {
        return allowComment;
    }

    public void setAllowComment(boolean allowComment) {
        this.allowComment = allowComment;
    }

    public int getNumOfComments() {
        return numOfComments;
    }

    public void setNumOfComments(int numOfComments) {
        this.numOfComments = numOfComments;
    }

    public ArrayList<String> getUsersLiked() {
        return usersLiked;
    }

    public void setUsersLiked(ArrayList<String> usersLiked) {
        this.usersLiked = usersLiked;
    }
}
