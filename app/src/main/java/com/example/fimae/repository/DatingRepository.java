package com.example.fimae.repository;

import android.net.Uri;

import com.example.fimae.models.dating.DatingProfile;
import com.example.fimae.models.dating.EducationalLevel;
import com.example.fimae.models.dating.RelationshipType;
import com.example.fimae.service.FirebaseService;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class DatingRepository {
    public static final String datingProfilesCollection = "dating-profiles";
    public static final String matchesCollection = "matches";
    public static final String datingProfileImagesLocation = "dating-profile-images";
    FirebaseFirestore firestore;
    CollectionReference datingProfiles;
    CollectionReference matches;
    StorageReference datingProfileImages;
    private static DatingRepository instance;

    public static DatingRepository getInstance() {
        if (instance == null) {
            instance = new DatingRepository();
        }
        return instance;
    }

    private DatingRepository() {
        firestore = FirebaseFirestore.getInstance();
        datingProfiles = firestore.collection(datingProfilesCollection);
        matches = firestore.collection(matchesCollection);
        datingProfileImages = FirebaseStorage.getInstance().getReference(datingProfileImagesLocation);
    }

    public Task<DatingProfile> createDatingProfile(String description, LatLng location, int distance, int minAge, int maxAge, int minHeight, int maxHeight, RelationshipType relationshipType, EducationalLevel educationalLevel, ArrayList<Uri> images, ArrayList<String> genres) {
        TaskCompletionSource<DatingProfile> taskCompletionSource = new TaskCompletionSource<>();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DocumentReference documentReference = datingProfiles.document(uid);
        DatingProfile profile = new DatingProfile();
        profile.setId(uid);
        profile.setUid(FirebaseAuth.getInstance().getCurrentUser().getUid());
        profile.setDescription(description);
        profile.setLocation(location);
        profile.setDistance(distance);
        profile.setMinAge(minAge);
        profile.setMaxAge(maxAge);
        profile.setMinHeight(minHeight);
        profile.setMaxHeight(maxHeight);
        profile.setRelationshipType(relationshipType);
        profile.setEducationalLevel(educationalLevel);
        profile.setGenres(genres);
        String path = datingProfileImagesLocation + "/" + profile.getId();
        FirebaseService.getInstance().uploadTaskFiles(path, images).whenComplete(new BiConsumer<List<String>, Throwable>() {
            @Override
            public void accept(List<String> strings, Throwable throwable) {
                if (throwable != null) {
                    taskCompletionSource.setException((Exception) throwable);
                } else {
                    profile.setImages((ArrayList<String>) strings);
                    documentReference.set(profile).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            taskCompletionSource.setResult(profile);
                        } else {
                            taskCompletionSource.setException(task.getException());
                        }
                    });
                }
            }
        });
        return taskCompletionSource.getTask();
    }
}
