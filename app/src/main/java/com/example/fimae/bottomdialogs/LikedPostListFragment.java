package com.example.fimae.bottomdialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.fimae.R;
import com.example.fimae.activities.PostMode;
import com.example.fimae.adapters.LikedAdapeter;
import com.example.fimae.databinding.BottomsheetLikedPeopleBinding;
import com.example.fimae.models.Fimaers;
import com.example.fimae.models.Post;
import com.example.fimae.models.Seed;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LikedPostListFragment extends BottomSheetDialogFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private BottomsheetLikedPeopleBinding binding;
    private Post post;
    private List<Fimaers> userInfoList = new ArrayList<>();
    String number;
    private Map<String, Boolean> userInfo;
    LikedAdapeter adapeter;
    private CollectionReference fimaersRef = FirebaseFirestore.getInstance().collection("fimaers");
    public LikedPostListFragment(Map<String, Boolean> userList, String number, Post post) {
        this.userInfo = userList;
        this.number = number;
        this.post = post;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        return super.onCreateDialog(savedInstanceState);
    }

    private static LikedPostListFragment instance;
    public static LikedPostListFragment getInstance(Map<String, Boolean> fimaers, String number, Post post) {

            instance = new LikedPostListFragment(fimaers, number, post);
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding = BottomsheetLikedPeopleBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        userInfoList.clear();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        binding.userList.setLayoutManager(linearLayoutManager);
         adapeter = new LikedAdapeter(getContext(), userInfoList);
        binding.userList.setAdapter(adapeter);
        for(Map.Entry<String, Boolean> entry: userInfo.entrySet()){
            if(entry.getValue())
            {
                fimaersRef.document(entry.getKey()).get().addOnSuccessListener(documentSnapshot -> {
                    userInfoList.add(documentSnapshot.toObject(Fimaers.class));
                    if(adapeter != null) adapeter.notifyDataSetChanged();
                });
            }
        }
        binding.title.setText("Lượt thích và cảm xúc: " +String.valueOf(number));

    }
}