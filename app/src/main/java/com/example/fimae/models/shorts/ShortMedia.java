package com.example.fimae.models.shorts;

import com.example.fimae.R;
import com.example.fimae.activities.HomeActivity;
import com.example.fimae.activities.PostMode;
import com.example.fimae.models.story.Story;
import com.example.fimae.models.story.StoryType;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.ServerTimestamp;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.Date;

public class ShortMedia {
    private String id;
    private String uid;
    private String description;
    private Object mediaUrl;
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
        shortMedia.setUid(FirebaseAuth.getInstance().getUid());
        shortMedia.setDescription(description);
        shortMedia.setMediaUrl(videoPath);
        shortMedia.setType(ShortMediaType.VIDEO);
        shortMedia.setPostMode(postMode);
        shortMedia.setAllowComment(allowComment);
        shortMedia.setUsersLiked(new ArrayList<>());
        return shortMedia;
    }
    public static ShortMedia createShortImages(String id, String description, ArrayList<String> imagesPath, PostMode postMode, boolean allowComment) {
        ShortMedia shortMedia = new ShortMedia();
        shortMedia.setId(id);
        shortMedia.setUid(FirebaseAuth.getInstance().getUid());
        shortMedia.setDescription(description);
        shortMedia.setMediaUrl(imagesPath);
        shortMedia.setType(ShortMediaType.IMAGES);
        shortMedia.setPostMode(postMode);
        shortMedia.setAllowComment(allowComment);
        shortMedia.setUsersLiked(new ArrayList<>());
        return shortMedia;
    }
    public static ArrayList<ShortMedia> getFakeData(){
        ArrayList<ShortMedia> shortMedias = new ArrayList<>();

        shortMedias.add(ShortMedia.createShortVideo("1", "Video \"Ngọt ngào cùng những chú mèo đáng yêu\" là một bộ sưu tập những cảnh quay đáng yêu và đầy hài hước về các chú mèo dễ thương", "android.resource://" + HomeActivity.PACKAGE_NAME + "/"+ R.raw.video1, PostMode.PUBLIC, true));
        shortMedias.add(ShortMedia.createShortVideo("2", "Video bắt đầu bằng những cảnh mèo con vui đùa, nhảy lên nhảy xuống với niềm vui tột độ trên chiếc giường nhỏ. ", "android.resource://" + HomeActivity.PACKAGE_NAME + "/"+ R.raw.video2, PostMode.PUBLIC, true));
        shortMedias.add(ShortMedia.createShortVideo("3", "Từ những cánh rừng rậm rạp, thác nước xiết dòng", "android.resource://" + HomeActivity.PACKAGE_NAME + "/"+ R.raw.video3, PostMode.PUBLIC, true));
        shortMedias.add(ShortMedia.createShortVideo("4", "test", "android.resource://" + HomeActivity.PACKAGE_NAME + "/"+ R.raw.video4, PostMode.PUBLIC, true));
        shortMedias.add(ShortMedia.createShortVideo("5", "test", "android.resource://" + HomeActivity.PACKAGE_NAME + "/"+ R.raw.video5, PostMode.PUBLIC, true));
        shortMedias.add(ShortMedia.createShortVideo("6", "test", "android.resource://" + HomeActivity.PACKAGE_NAME + "/"+ R.raw.video6, PostMode.PUBLIC, true));
        shortMedias.add(ShortMedia.createShortVideo("7", "test", "android.resource://" + HomeActivity.PACKAGE_NAME + "/"+ R.raw.video7, PostMode.PUBLIC, true));
        shortMedias.add(ShortMedia.createShortVideo("8", "test", "android.resource://" + HomeActivity.PACKAGE_NAME + "/"+ R.raw.video8, PostMode.PUBLIC, true));
        shortMedias.add(ShortMedia.createShortVideo("9", "test", "android.resource://" + HomeActivity.PACKAGE_NAME + "/"+ R.raw.video9, PostMode.PUBLIC, true));
        shortMedias.add(ShortMedia.createShortVideo("10", "test", "android.resource://" + HomeActivity.PACKAGE_NAME + "/"+ R.raw.video10, PostMode.PUBLIC, true));
        return shortMedias;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public Object getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(Object mediaUrl) {
        this.mediaUrl = mediaUrl;
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
