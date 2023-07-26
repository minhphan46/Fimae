package com.example.fimae.repository;

import android.location.Geocoder;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.fimae.models.Fimaers;
import com.example.fimae.models.dating.DatingProfile;
import com.example.fimae.models.dating.DatingProfileDefaultValue;
import com.example.fimae.models.dating.EducationalLevel;
import com.example.fimae.models.dating.GenderOptions;
import com.example.fimae.models.dating.LatLng;
import com.example.fimae.models.dating.Match;
import com.example.fimae.models.dating.RelationshipType;
import com.example.fimae.service.FirebaseService;
import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQueryBounds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;

public class DatingRepository {
    public static final String datingProfilesCollection = "dating-profiles";
    public static final String matchesCollection = "match";
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

    public Task<Void> updateMatch(String id, HashMap<String, Boolean> userRead)
    {
        return matches.document(id).update("userRead", userRead);
    }

    public Task<DatingProfile> createDatingProfile(LatLng location, ArrayList<Uri> images) {
        TaskCompletionSource<DatingProfile> taskCompletionSource = new TaskCompletionSource<>();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DocumentReference documentReference = datingProfiles.document(uid);
        DatingProfile profile = new DatingProfile();
        profile.setId(uid);
        profile.setUid(FirebaseAuth.getInstance().getCurrentUser().getUid());
        profile.setLocation(location);
        profile.setDistance(DatingProfileDefaultValue.distance);
        profile.setMinAge(DatingProfileDefaultValue.minAge);
        profile.setMaxAge(DatingProfileDefaultValue.maxAge);
        profile.setMinHeight(DatingProfileDefaultValue.minHeight);
        profile.setMaxHeight(DatingProfileDefaultValue.maxHeight);
        profile.setGenderOptions(DatingProfileDefaultValue.gender);
        profile.setRelationshipType(DatingProfileDefaultValue.relationshipType);
        profile.setEducationalLevel(DatingProfileDefaultValue.educationalLevel);
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

    public Task<DatingProfile> getDatingProfileByUid(String uid){
        TaskCompletionSource<DatingProfile> taskCompletionSource = new TaskCompletionSource<>();
        datingProfiles.document(uid).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                DatingProfile profile = task.getResult().toObject(DatingProfile.class);
                taskCompletionSource.setResult(profile);
            }else{
                taskCompletionSource.setException(task.getException());
            }
        });
        return taskCompletionSource.getTask();
    }

    public Task<ArrayList<Uri>> updateDatingProfileImages(ArrayList<Uri> remoteImageUrls, ArrayList<Uri> localImageUrls) {
        TaskCompletionSource<ArrayList<Uri>> taskCompletionSource = new TaskCompletionSource<>();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String path = datingProfileImagesLocation + "/" + uid;
        if(localImageUrls.size() == 0){
            datingProfiles.document(FirebaseAuth.getInstance().getUid()).update("images", remoteImageUrls).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    taskCompletionSource.setResult(remoteImageUrls);
                } else {
                    taskCompletionSource.setException(task.getException());
                }
            });
            return taskCompletionSource.getTask();
        }
        FirebaseService.getInstance().uploadTaskFiles(path, localImageUrls).whenComplete(new BiConsumer<List<String>, Throwable>() {
            @Override
            public void accept(List<String> strings, Throwable throwable) {
                if (throwable != null) {
                    taskCompletionSource.setException((Exception) throwable);
                } else {
                    ArrayList<Uri> finalImages = new ArrayList<>();
                    finalImages.addAll(remoteImageUrls);
                    for (String url : strings) {
                        finalImages.add(Uri.parse(url));
                    }
                    datingProfiles.document(FirebaseAuth.getInstance().getUid()).update("images", finalImages).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            taskCompletionSource.setResult(finalImages);
                        } else {
                            taskCompletionSource.setException(task.getException());
                        }
                    });
                }
            }
        });
        return taskCompletionSource.getTask();
    }

    private Task<Void> updateProfileField(String field, Object value) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DocumentReference documentReference = datingProfiles.document(uid);
        return documentReference.update(field, value);
    }
    public Task<Void> updateGenderOptions(GenderOptions genderOptions) {
        return updateProfileField("genderOptions", genderOptions);
    }



    public Task<Void> updateRelationshipType(RelationshipType relationshipType) {
        return updateProfileField("relationshipType", relationshipType);
    }

    public Task<Void> updateEducationalLevel(EducationalLevel educationalLevel) {
        return updateProfileField("educationalLevel", educationalLevel);
    }



    public Task<Void> updateDistance(int distance) {
        return updateProfileField("distance", distance);
    }

    public Task<Void> updateLocation(LatLng location) {
        //return updateProfileField("location", location);
        String hash = GeoFireUtils
                .getGeoHashForLocation(new GeoLocation(location.getLatitude(), location.getLongitude()));
        WriteBatch batch = firestore.batch();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DocumentReference documentReference = datingProfiles.document(uid);
        batch.update(documentReference, "location", location);
        batch.update(documentReference, "geoHash", hash);
        return batch.commit();
    }

    public Task<Void> updateUserLike(ArrayList<String> userLike)
    {
        return updateProfileField("likedUsers", userLike);
    }

    public Task<Void> updateUserDislike(ArrayList<String> userDislike)
    {
        return updateProfileField("dislikedUsers", userDislike);
    }

    public Task<Void> updateEnable(boolean isEnable) {
        return updateProfileField("isEnable", isEnable);
    }

    public Task<Void> updateMinAgeAndMaxAge(int minAge, int maxAge) {
        WriteBatch batch = firestore.batch();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DocumentReference documentReference = datingProfiles.document(uid);
        batch.update(documentReference, "minAge", minAge);
        batch.update(documentReference, "maxAge", maxAge);
        return batch.commit();
    }
    public Task<Void> updateMinHeightAndMaxHeight(int minHeight, int maxHeight) {
        WriteBatch batch = firestore.batch();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DocumentReference documentReference = datingProfiles.document(uid);
        batch.update(documentReference, "minHeight", minHeight);
        batch.update(documentReference, "maxHeight", maxHeight);
        return batch.commit();
    }


}
