package com.example.fimae.fragments;


import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.fimae.DatingAddImages;
import com.example.fimae.R;
import com.example.fimae.activities.DatingSettings;
import com.example.fimae.activities.OnChatActivity;
import com.example.fimae.activities.ProfileActivity;
import com.example.fimae.dialogs.DatingMatchDialog;
import com.example.fimae.models.Post;
import com.example.fimae.models.dating.DatingProfile;
import com.example.fimae.models.dating.Match;
import com.example.fimae.repository.DatingRepository;
import com.example.fimae.repository.FetchDatingProfileRepo;
import com.example.fimae.repository.FimaerRepository;
import com.example.fimae.repository.PostRepository;
import com.example.fimae.utils.Utils;
import com.example.fimae.models.dating.Profile;
import com.example.fimae.models.dating.TinderCard;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.mindorks.placeholderview.SwipeDecor;
import com.mindorks.placeholderview.SwipePlaceHolderView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SwipeViewFragment extends Fragment {


    private View rootLayout;
    private FloatingActionButton fabBack, fabLike, fabSkip, fabSuperLike, fabBoost;

    private SwipePlaceHolderView mSwipeView;

    private ListenerRegistration matchListener;

    private Context mContext;

    DatingProfile currentDating;

    public SwipeViewFragment() {
        // Required empty public constructor
    }

    String uid = FimaerRepository.getInstance().getCurrentUserUid();
    boolean hasProfile = false;
    boolean hasListen = false;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootLayout = inflater.inflate(R.layout.fragment_swipe_view, container, false);

        Toolbar toolbar = rootLayout.findViewById(R.id.toolbar);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_add_friend:
                        if(!hasProfile)
                        {
                            Intent intent = new Intent(getContext(), DatingAddImages.class);
                            intent.putExtra("uid", uid);
                            intent.putExtra("isCreate", true);
                            startActivity(intent);
                        }
                        else
                        {
                            Intent intent = new Intent(getContext(), DatingSettings.class);
                            intent.putExtra("uid", FirebaseAuth.getInstance().getUid());
                            startActivity(intent);
                        }
                        return true;
                    default:
                        return false;
                }
            }
        });
        return rootLayout;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(currentDating == null)
        {
            DatingRepository.getInstance().getDatingProfileByUid(uid).addOnSuccessListener(datingProfile -> {
                if (datingProfile == null) {
                    hasProfile = false;
                }
                else {
                    currentDating = datingProfile;
                    hasProfile = true;
                    getData();
                }
            });
        }
        if(!hasListen)
        {
            hasListen = true;
            initListener();
        }
        else
        {
            if(hasProfile)
                getData();
        }
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
                                public void OnGetProfileComplete(HashMap<String, DatingProfile> matchingList, DatingProfile currentProfile) {
                                    for (DatingProfile profile : matchingList.values())
                                    {
                                        mSwipeView.addView(new TinderCard(getContext(), profile, mSwipeView, currentProfile));
                                    }
                                }
                            });
                        }
                    }
                });
    }
    private void initListener()
    {
        CollectionReference matchRef = FirebaseFirestore.getInstance().collection("match");
        matchListener = matchRef.addSnapshotListener((value, error) -> {
            if (error != null) {
                return;
            }
            for (DocumentChange dc : value.getDocumentChanges()) {
                Match match = dc.getDocument().toObject(Match.class);
                switch (dc.getType()) {
                    case ADDED:
                        if(match.getUserRead() == null || match.getUserRead().get(uid) == null || match.getUserRead().get(uid) == false)
                        {
                            List<Task<DatingProfile>> tasks = new ArrayList<>();
                            tasks.add(FetchDatingProfileRepo.getInstance().getProfileById(match.getMatchedUsers().get(0)));
                            tasks.add(FetchDatingProfileRepo.getInstance().getProfileById(match.getMatchedUsers().get(1)));
                            Tasks.whenAllComplete(tasks)
                                    .addOnCompleteListener(new OnCompleteListener<List<Task<?>>>() {
                                        @Override
                                        public void onComplete(@NonNull Task<List<Task<?>>> t) {
                                            DatingProfile your = null;
                                            DatingProfile other = null;
                                            for(Task<DatingProfile> task : tasks)
                                            {
                                                if(task.getResult().getUid().equals(FimaerRepository.getInstance().getCurrentUserUid()))
                                                {
                                                    your = task.getResult();
                                                }
                                                else
                                                {
                                                    other = task.getResult();
                                                }
                                            }
                                            DatingProfile finalOther = other;
                                            DatingProfile finalYour = your;
                                            DatingMatchDialog.builder()
                                                    .setMyImageUrl(your.getImages().get(0))
                                                    .setOtherImageUrl(other.getImages().get(0))
                                                    .setOtherName(other.getName()).setListener(new DatingMatchDialog.ButtonListener() {
                                                        @Override
                                                        public void onAcceptClick() {
                                                            PostRepository.getInstance().goToChatWithUser(finalOther.getUid(), getContext());
                                                            match.addUserRead(finalYour.getUid(), true);
                                                            DatingRepository.getInstance().updateMatch(match.getId(),match.getUserRead());
                                                        }

                                                        @Override
                                                        public void onDeclineClick() {
                                                            match.addUserRead(finalYour.getUid(), true);
                                                            DatingRepository.getInstance().updateMatch(match.getId(),match.getUserRead());
                                                        }
                                                    }).build().show(getActivity().getSupportFragmentManager(),"dating_match_dialog");
                                        }
                                    });
                        }
                        break;
                    case MODIFIED:
                        break;
                    case REMOVED:
                        break;
                }
            }
        });
    }
    private void animateFab(final FloatingActionButton fab){
        fab.animate().scaleX(0.7f).setDuration(100).withEndAction(() -> fab.animate().scaleX(1f).scaleY(1f));
    }

    @Override
    public void onDestroy() {
        if(matchListener != null)
            matchListener.remove();
        super.onDestroy();
    }
}
