package com.example.fimae.models.story;

import com.example.fimae.R;
import com.example.fimae.activities.HomeActivity;
import com.example.fimae.activities.PostMode;
import com.google.firebase.firestore.ServerTimestamp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class Story implements Serializable {
    private String id;
    private String uid;
    private StoryType type;
    private String url;
    private @ServerTimestamp Date timeCreated;
    private PostMode postMode;
    private boolean isSaved;
    private ArrayList<String> viewers;
    private ArrayList<String> userLikes;



    public Story() {
        isSaved = false;
        viewers = new ArrayList<>();
        userLikes = new ArrayList<>();
    }

    public Story(String id, String uid, StoryType type, String url, Date timeCreated, PostMode postMode, boolean isSaved, ArrayList<String> viewers, ArrayList<String> userLikes) {
        this.id = id;
        this.uid = uid;
        this.type = type;
        this.url = url;
        this.timeCreated = timeCreated;
        this.postMode = postMode;
        this.isSaved = isSaved;
        this.viewers = viewers;
        this.userLikes = userLikes;
    }
    public static ArrayList<Story> getFakeData(){
        ArrayList<Story> stories = new ArrayList<>();
        stories.add(new Story("3", "3", StoryType.VIDEO, "android.resource://" + HomeActivity.PACKAGE_NAME + "/" + R.raw.video4, new Date(), PostMode.PUBLIC, false, new ArrayList<>(), new ArrayList<>()));
        stories.add(new Story("1", "1", StoryType.IMAGE, "https://picsum.photos/200/300?random=1", new Date(), PostMode.PUBLIC, false, new ArrayList<>(), new ArrayList<>()));
        stories.add(new Story("2", "2", StoryType.IMAGE, "https://picsum.photos/200/300?random=2", new Date(), PostMode.PUBLIC, false, new ArrayList<>(), new ArrayList<>()));
        stories.add(new Story("3", "3", StoryType.VIDEO, "android.resource://" + HomeActivity.PACKAGE_NAME + "/" + R.raw.video4, new Date(), PostMode.PUBLIC, false, new ArrayList<>(), new ArrayList<>()));
        stories.add(new Story("4", "4", StoryType.IMAGE, "https://picsum.photos/200/300?random=4", new Date(), PostMode.PUBLIC, false, new ArrayList<>(), new ArrayList<>()));
        stories.add(new Story("5", "5", StoryType.IMAGE, "https://picsum.photos/200/300?random=5", new Date(), PostMode.PUBLIC, false, new ArrayList<>(), new ArrayList<>()));
        stories.add(new Story("6", "6", StoryType.IMAGE, "https://picsum.photos/200/300?random=6", new Date(), PostMode.PUBLIC, false, new ArrayList<>(), new ArrayList<>()));
        stories.add(new Story("7", "7", StoryType.IMAGE, "https://picsum.photos/200/300?random=7", new Date(), PostMode.PUBLIC, false, new ArrayList<>(), new ArrayList<>()));
        return stories;
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

    public StoryType getType() {
        return type;
    }

    public void setType(StoryType type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Date getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(Date timeCreated) {
        this.timeCreated = timeCreated;
    }

    public PostMode getPostMode() {
        return postMode;
    }

    public void setPostMode(PostMode postMode) {
        this.postMode = postMode;
    }

    public boolean isSaved() {
        return isSaved;
    }

    public void setSaved(boolean saved) {
        isSaved = saved;
    }

    public ArrayList<String> getViewers() {
        return viewers;
    }

    public void setViewers(ArrayList<String> viewers) {
        this.viewers = viewers;
    }

    public ArrayList<String> getUserLikes() {
        return userLikes;
    }

    public void setUserLikes(ArrayList<String> userLikes) {
        this.userLikes = userLikes;
    }
}
