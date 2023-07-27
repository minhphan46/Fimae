package com.example.fimae.repository;

import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.fimae.models.Fimaers;
import com.example.fimae.models.dating.DatingProfile;
import com.example.fimae.models.dating.GenderOptions;
import com.example.fimae.models.dating.LatLng;
import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQueryBounds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FetchDatingProfileRepo {

    private static FetchDatingProfileRepo instance;

    public static FetchDatingProfileRepo getInstance() {
        if (instance == null) {
            instance = new FetchDatingProfileRepo();
        }
        return instance;
    }
    FirebaseFirestore firestore;
    private FetchDatingProfileRepo() {
        firestore = FirebaseFirestore.getInstance();
    }
    HashMap<String, Fimaers> userList = new HashMap<>();
    HashMap<String,DatingProfile> datingProfiles = new HashMap<>();

    public void getMatchingSnapshot(GeoLocation center, double radiusInM, DatingProfile currentProfile, FetchDatingProfileRepo.GetProfileCallback callback )
    {
        Log.e("TAG", "onComplete: ");

        List<GeoQueryBounds> bounds = GeoFireUtils.getGeoHashQueryBounds(center, radiusInM);
        final List<Task<QuerySnapshot>> tasks = new ArrayList<>();
        for (GeoQueryBounds b : bounds) {
            Query q = firestore.collection("dating-profiles")
                    .orderBy("geoHash")
                    .startAt(b.startHash)
                    .endAt(b.endHash);

            tasks.add(q.get());
        }
        Log.e("TAG", "onComplete: ");

// Collect all the query results together into a single list
        Tasks.whenAllComplete(tasks)
                .addOnCompleteListener(new OnCompleteListener<List<Task<?>>>() {
                    @Override
                    public void onComplete(@NonNull Task<List<Task<?>>> t) {
                        List<DocumentSnapshot> matchingDocs = new ArrayList<>();

                        for (Task<QuerySnapshot> task : tasks) {
                            QuerySnapshot snap = task.getResult();
                            for (DocumentSnapshot doc : snap.getDocuments()) {
                                Log.e("TAG", "onComplete: ");
                                LatLng latLng = doc.get("location", LatLng.class);
                                String uid = doc.getString("uid");
                                double lat = latLng.getLatitude();
                                double lng = latLng.getLongitude();

                                // We have to filter out a few false positives due to GeoHash
                                // accuracy, but most will match
                                GeoLocation docLocation = new GeoLocation(lat, lng);
                                double distanceInM = GeoFireUtils.getDistanceBetween(docLocation, center);
                                Log.e("TAG", "onComplete: ");

                                if (distanceInM <= radiusInM && !currentProfile.getUid().equals(uid) ) {
                                    matchingDocs.add(doc);
                                }
                            }
                        }
                        ArrayList<String> userIds = new ArrayList<>();
                        if(userList.get(currentProfile.getUid()) == null)
                        {
                            userIds.add(currentProfile.getUid());
                            datingProfiles.put(currentProfile.getUid(), currentProfile);
                        }
                        for(DocumentSnapshot doc : matchingDocs)
                        {
                            Log.e("TAG", "onComplete: 1");
                            DatingProfile datingProfile = doc.toObject(DatingProfile.class);
                            if(datingProfiles.get(datingProfile.getUid()) == null)
                            {
                                datingProfiles.put(datingProfile.getUid(),datingProfile);
                            }
                            if(userList.get(datingProfile.getUid()) == null)
                            {
                                userIds.add(datingProfile.getUid());
                            }
                        }
                        if(!userIds.isEmpty())
                        {
                            FimaerRepository.getInstance().getFimaersByIds(userIds)
                                    .addOnCompleteListener(new OnCompleteListener<ArrayList<Fimaers>>() {
                                        @Override
                                        public void onComplete(@NonNull Task<ArrayList<Fimaers>> task) {
                                            if(task.isSuccessful())
                                            {
                                                Log.e("TAG", "onComplete: 2");
                                                for(Fimaers user : task.getResult())
                                                {
                                                    userList.put(user.getUid(), user);
                                                    datingProfiles.get(user.getUid()).setAge(user.getAge());
                                                    datingProfiles.get(user.getUid()).setName(user.getName());
                                                    datingProfiles.get(user.getUid()).setGender(user.isGender());
                                                    LatLng latLng = datingProfiles.get(user.getUid()).getLocation();
                                                    double lat = latLng.getLatitude();
                                                    double lng = latLng.getLongitude();

                                                    GeoLocation docLocation = new GeoLocation(lat, lng);
                                                    double distanceInM = GeoFireUtils.getDistanceBetween(docLocation, center);
                                                    datingProfiles.get(user.getUid()).setDistanceFromYou(distanceInM);
                                                }
                                                callback.OnGetProfileComplete(filter(datingProfiles,currentProfile),currentProfile);
                                            }
                                        }

                                    });
                        }
                        else
                        {
                            DatingProfile cProfile = datingProfiles.get(currentProfile.getUid());
                            callback.OnGetProfileComplete(filter(datingProfiles,cProfile),cProfile);
                        }


                        // matchingDocs contains the results
                        // ...
                    }
                });
    }

    private HashMap<String,DatingProfile> filter(HashMap<String,DatingProfile> datingProfiles, DatingProfile currentProfile)
    {
        HashMap<String, DatingProfile> result = new HashMap<>();
        for (DatingProfile profile : datingProfiles.values())
        {
            try {
                int age = Integer.parseInt(currentProfile.getAge());
                int profileAge = Integer.parseInt(profile.getAge());
                if(age >= profile.getMinAge() && age <= profile.getMaxAge()
                        && profileAge >= currentProfile.getMinAge() && profileAge <= currentProfile.getMaxAge())
                {
                    GenderOptions gender = currentProfile.isGender() ? GenderOptions.MALE : GenderOptions.FEMALE;
                    GenderOptions profileGender = profile.isGender() ? GenderOptions.MALE : GenderOptions.FEMALE;

                    if((currentProfile.getGenderOptions() == GenderOptions.UNIMPORTANT)
                            && (profile.getGenderOptions() == GenderOptions.UNIMPORTANT || profile.getGenderOptions() == gender ))
                    {
                        result.put(profile.getUid(), profile);
                    }
                    else if(currentProfile.getGenderOptions() == profileGender
                            && (profile.getGenderOptions() == GenderOptions.UNIMPORTANT || profile.getGenderOptions() == gender))
                    {
                        result.put(profile.getUid(), profile);
                    }
                }
            } catch (NumberFormatException e) {
            }

        }
        result.remove(currentProfile.getUid());
        return result;
    }

    public Task<DatingProfile> getProfileById(String id)
    {
        TaskCompletionSource<DatingProfile> taskCompletionSource = new TaskCompletionSource<>();
        if(datingProfiles.containsKey(id))
        {
            taskCompletionSource.setResult(datingProfiles.get(id));
        }else
        {
            return DatingRepository.getInstance().getDatingProfileByUid(id);
        }
        return taskCompletionSource.getTask();
    }

    public interface GetProfileCallback
    {
        void OnGetProfileComplete(HashMap<String,DatingProfile> matchingList, DatingProfile currentProfil);
    }
}
