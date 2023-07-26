package com.example.fimae.fragments;


import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.fimae.DatingAddImages;
import com.example.fimae.R;
import com.example.fimae.models.dating.DatingProfile;
import com.example.fimae.repository.DatingRepository;
import com.example.fimae.repository.FetchDatingProfileRepo;
import com.example.fimae.repository.FimaerRepository;
import com.example.fimae.utils.Utils;
import com.example.fimae.models.dating.Profile;
import com.example.fimae.models.dating.TinderCard;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mindorks.placeholderview.SwipeDecor;
import com.mindorks.placeholderview.SwipePlaceHolderView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SwipeViewFragment extends Fragment {


    private View rootLayout;
    private FloatingActionButton fabBack, fabLike, fabSkip, fabSuperLike, fabBoost;

    private SwipePlaceHolderView mSwipeView;
    private Context mContext;

    public SwipeViewFragment() {
        // Required empty public constructor
    }

    String uid = FimaerRepository.getInstance().getCurrentUserUid();
    boolean hasProfile = false;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootLayout = inflater.inflate(R.layout.fragment_swipe_view, container, false);


        return rootLayout;
    }

    @Override
    public void onResume() {
        super.onResume();
        DatingRepository.getInstance().getDatingProfileByUid(uid).addOnSuccessListener(datingProfile -> {
            if (datingProfile != null) {
                hasProfile = true;
            }

        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mSwipeView = view.findViewById(R.id.swipeView);
        fabBack = view.findViewById(R.id.fabBack);
        fabLike = view.findViewById(R.id.fabLike);
        fabSkip = view.findViewById(R.id.fabSkip);
        fabSuperLike = view.findViewById(R.id.fabSuperLike);
        fabBoost = view.findViewById(R.id.fabBoost);


        mContext = getActivity();
        if(hasProfile)
        {
            getData();
        }
        int bottomMargin = Utils.dpToPx(100);
        int topMargin = Utils.dpToPx(80);
        Point windowSize = Utils.getDisplaySize(getActivity().getWindowManager());
        mSwipeView.getBuilder()
                .setDisplayViewCount(3)
                .setIsUndoEnabled(true)
                .setSwipeDecor(new SwipeDecor()
                        .setViewWidth(windowSize.x)
                        .setViewHeight(windowSize.y - bottomMargin - topMargin)
                        .setViewGravity(Gravity.TOP)
                        .setPaddingTop(20)
                        .setRelativeScale(0.01f)
                        .setSwipeInMsgLayoutId(R.layout.tinder_swipe_in_msg_view)
                        .setSwipeOutMsgLayoutId(R.layout.tinder_swipe_out_msg_view));

        DatingRepository.getInstance().getDatingProfileByUid(uid).addOnSuccessListener(datingProfile -> {
            if (datingProfile == null) {
                hasProfile = false;
                Intent intent = new Intent(getContext(), DatingAddImages.class);
                intent.putExtra("uid", uid);
                intent.putExtra("isCreate", true);
                startActivity(intent);
            }
            else {
                hasProfile = true;
                getData();
            }
        });
        for(Profile profile : Utils.loadProfiles(getActivity())){
        }

        fabSkip.setOnClickListener(v -> {
            animateFab(fabSkip);
            mSwipeView.doSwipe(false);
        });

        fabLike.setOnClickListener(v -> {
            animateFab(fabLike);
            mSwipeView.doSwipe(true);
        });

        fabBoost.setOnClickListener(v -> animateFab(fabBoost));
        fabSuperLike.setOnClickListener(v -> animateFab(fabSuperLike));
        fabBack.setOnClickListener(v -> {
            mSwipeView.undoLastSwipe();
            animateFab(fabBack);
        });
    }

    private void getData()
    {
        DatingRepository.getInstance().getDatingProfileByUid(uid)
                .addOnCompleteListener(new OnCompleteListener<DatingProfile>() {
                    @Override
                    public void onComplete(@NonNull Task<DatingProfile> task) {
                        if(task.isSuccessful())
                        {
                            Log.e("TAG", "onComplete: ");
                            DatingProfile datingProfile = task.getResult();
                            GeoLocation center = new GeoLocation
                                    (datingProfile.getLocation().getLatitude(),datingProfile.getLocation().getLongitude());
                            double radius = datingProfile.getDistance() * 1000;
                            FetchDatingProfileRepo.getInstance().getMatchingSnapshot(center, radius, datingProfile, new FetchDatingProfileRepo.GetProfileCallback() {
                                @Override
                                public void OnGetProfileComplete(HashMap<String, DatingProfile> matchingList) {
                                    for (DatingProfile profile : matchingList.values())
                                    {
                                        mSwipeView.addView(new TinderCard(getContext(), profile, mSwipeView));
                                    }
                                }
                            });
                        }
                    }
                });
    }

    private void animateFab(final FloatingActionButton fab){
        fab.animate().scaleX(0.7f).setDuration(100).withEndAction(() -> fab.animate().scaleX(1f).scaleY(1f));
    }

}
