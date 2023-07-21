package com.example.fimae.repository;

import android.net.Uri;

import com.example.fimae.activities.PostMode;
import com.example.fimae.models.story.Story;
import com.example.fimae.models.story.StoryType;
import com.example.fimae.service.FirebaseService;
import com.example.fimae.utils.FileUtils;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class StoryRepository {
    private static StoryRepository instance;

    public static StoryRepository getInstance() {
        if (instance == null)
            instance = new StoryRepository();
        return instance;
    }
    private FirebaseFirestore firestore;
    private CollectionReference storyColRef;
    private StoryRepository() {
        firestore = FirebaseFirestore.getInstance();
        storyColRef = firestore.collection("stories");
    }
    public Task<Story> createStory(Uri uri) {
        TaskCompletionSource<Story> taskCompletionSource = new TaskCompletionSource<>();
        DocumentReference storyRef = storyColRef.document();
        Story story = new Story();
        story.setId(storyRef.getId());
        story.setUid(FirebaseAuth.getInstance().getUid());
        story.setPostMode(PostMode.PUBLIC);
        if(FileUtils.isImageFile(uri.toString()))
            story.setType(StoryType.IMAGE);
        else
            story.setType(StoryType.VIDEO);
        String path = "stories/" + story.getId();
        FirebaseService.getInstance().uploadFile(path, uri).addOnSuccessListener(taskSnapshot -> {
            taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(uri1 -> {
                story.setUrl(uri1.toString());
                storyRef.set(story).addOnSuccessListener(aVoid -> {
                    taskCompletionSource.setResult(story);
                }).addOnFailureListener(e -> {
                    taskCompletionSource.setException(e);
                });
            }).addOnFailureListener(e -> {
                taskCompletionSource.setException(e);
            });

        }).addOnFailureListener(e -> {
            taskCompletionSource.setException(e);
        });
        return taskCompletionSource.getTask();
    }

    public Query getStoryQuery() {
        return storyColRef.orderBy("timeCreated", Query.Direction.DESCENDING);
    }
}
