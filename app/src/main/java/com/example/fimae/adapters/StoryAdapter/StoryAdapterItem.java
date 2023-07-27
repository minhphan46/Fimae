package com.example.fimae.adapters.StoryAdapter;

import androidx.annotation.NonNull;

import com.example.fimae.models.Fimaers;
import com.example.fimae.models.story.Story;
import com.example.fimae.repository.FimaerRepository;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class StoryAdapterItem implements Serializable {
    String uid;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    Fimaers fimaer;
    ArrayList<Story> stories;
    public StoryAdapterItem(String uid) {
        this.uid = uid;
        stories = new ArrayList<>();
        FimaerRepository.getInstance().getFimaerById(uid).addOnCompleteListener(new OnCompleteListener<Fimaers>() {
            @Override
            public void onComplete(@NonNull Task<Fimaers> task) {
                if(task.isSuccessful()){
                    fimaer = task.getResult();
                }
            }
        });
    }
    public StoryAdapterItem(Fimaers fimaer, ArrayList<Story> stories) {
        this.uid = fimaer.getUid();
        this.fimaer = fimaer;
        this.stories = stories;
    }

    public Fimaers getFimaer() {
        return fimaer;
    }

    public void setFimaer(Fimaers fimaer) {
        this.fimaer = fimaer;
    }

    public ArrayList<Story> getStories() {
        return stories;
    }

    public void setStories(ArrayList<Story> stories) {
        this.stories = stories;
    }
    public Story getFirstUnSeenStory(){
        String uid = FimaerRepository.getInstance().getCurrentUserUid();
        for (Story story: stories) {
            if(!story.getViewers().contains(uid)){
                return story;
            }
        }
        return stories.get(0);
    }
}
