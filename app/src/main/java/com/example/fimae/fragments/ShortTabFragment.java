package com.example.fimae.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.fimae.R;
import com.example.fimae.activities.AddShortActivity;
import com.example.fimae.activities.DisabledUserActivity;
import com.example.fimae.activities.PostActivity;
import com.example.fimae.activities.ShortVideoActivity;
import com.example.fimae.adapters.ShortAdapter.ShortsReviewAdapter;
import com.example.fimae.adapters.SpacingItemDecoration;
import com.example.fimae.models.DisableUser;
import com.example.fimae.models.shorts.ShortMedia;
import com.example.fimae.repository.ShortsRepository;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import java.util.Date;
import java.util.Objects;

public class ShortTabFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public ShortTabFragment() {
        // Required empty public constructor
    }

    public static ShortTabFragment newInstance(String param1, String param2) {
        ShortTabFragment fragment = new ShortTabFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        shortAdapter.stopListening();
    }
    private void listenDisable(){
    }


    ShortsReviewAdapter shortAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_short_tab, container, false);

        RecyclerView storyRecyclerView = view.findViewById(R.id.recycler_view_shorts_tab);
        LinearLayoutManager storyLinearLayoutManager = new LinearLayoutManager(this.getContext());
        storyLinearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        storyRecyclerView.setLayoutManager(storyLinearLayoutManager);
        SpacingItemDecoration itemDecoration = new SpacingItemDecoration(16, 16, 10, 0);
        storyRecyclerView.addItemDecoration(itemDecoration);
        Query shortQuery = ShortsRepository.getInstance().getShortQuery();
        shortAdapter = new ShortsReviewAdapter(
                shortQuery,
                new ShortsReviewAdapter.IClickCardListener() {
                    @Override
                    public void addShortClicked() {
                        FirebaseFirestore.getInstance().collection("user_disable")
                                .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())+"SHORT")
                                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        if(documentSnapshot != null){
                                            DisableUser disableUser = documentSnapshot.toObject(DisableUser.class);
                                            if (disableUser != null && disableUser.getTimeEnd().after(new Date()) && disableUser.getType() != null && disableUser.getType().equals("SHORT")) {
                                                Intent intent = new Intent(getContext(), DisabledUserActivity.class);
                                                intent.putExtra( "disableId", disableUser.getUserId());
                                                intent.putExtra("type", "SHORT");
                                                startActivity(intent);
                                            }
                                            else {
                                                Intent intent = new Intent(getContext(), AddShortActivity.class);
                                                startActivity(intent);
                                            }
                                            }
                                        else{
                                            Intent intent = new Intent(getContext(), AddShortActivity.class);
                                            startActivity(intent);

                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Intent intent = new Intent(getContext(), AddShortActivity.class);
                                        startActivity(intent);
                                    }
                                });
                    }
                    @Override
                    public void onClickUser(ShortMedia video) {
                        Intent intent = new Intent(getContext(), ShortVideoActivity.class);
                        intent.putExtra("idVideo", video.getId());  // send id video
                        intent.putExtra("isProfile", false);
                        startActivity(intent);
                    }
                }
        );
        storyRecyclerView.setAdapter(shortAdapter);

        return view;
    }
}