package com.example.fimae.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fimae.R;
import com.example.fimae.StoryActivity;
import com.example.fimae.activities.OnChatActivity;
import com.example.fimae.activities.SearchUserActivity;
import com.example.fimae.adapters.ConversationAdapter;
import com.example.fimae.adapters.SpacingItemDecoration;
import com.example.fimae.adapters.StoryAdapter.StoryAdapter;
import com.example.fimae.adapters.StoryAdapter.StoryAdapterItem;
import com.example.fimae.bottomdialogs.PickImageBottomSheetFragment;
import com.example.fimae.models.BottomSheetItem;
import com.example.fimae.models.Fimaers;
import com.example.fimae.models.story.Story;
import com.example.fimae.repository.ChatRepository;
import com.example.fimae.repository.StoryRepository;
import com.example.fimae.utils.FileUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.io.File;
import java.util.*;

public class ChatFragment extends Fragment {
    private FirebaseFirestore firestore;
    private CollectionReference fimaeUserRef;
    private CollectionReference conversationRef;

    private LinearLayout searchbar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.list_user);
        searchbar = view.findViewById(R.id.search_bar);
        Query query = ChatRepository.getInstance().getConversationQuery();
        ConversationAdapter adapter = new ConversationAdapter(query, conversation -> {
            Intent intent = new Intent(getContext(), OnChatActivity.class);
            intent.putExtra("conversationID", conversation.getId());
            startActivity(intent);
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);

        searchbar.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), SearchUserActivity.class);
            startActivity(intent);
        });
        RecyclerView storyRecyclerView = view.findViewById(R.id.recycler_view_story);
        LinearLayoutManager storyLinearLayoutManager = new LinearLayoutManager(this.getContext());
        storyLinearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        storyRecyclerView.setLayoutManager(storyLinearLayoutManager);
        SpacingItemDecoration itemDecoration = new SpacingItemDecoration(16, 16, 8, 8);
        storyRecyclerView.addItemDecoration(itemDecoration);
        Query storyQuery = StoryRepository.getInstance().getStoryQuery();
        StoryAdapter storyAdapter = new StoryAdapter(storyQuery);
        storyRecyclerView.setAdapter(storyAdapter);
        storyAdapter.setStoryListener(new StoryAdapter.StoryListener() {
            @Override
            public void addStoryClicked() {

                MediaListDialogFragment mediaListDialogFragment = new MediaListDialogFragment();
                mediaListDialogFragment.setOnMediaSelectedListener(new MediaListDialogFragment.OnMediaSelectedListener() {
                    @Override
                    public void OnMediaSelected(boolean isSelected, ArrayList<String> data) {
                        if(isSelected){
                            StoryRepository.getInstance().createStory(Uri.parse(data.get(0))).addOnCompleteListener(new OnCompleteListener<Story>() {
                                @Override
                                public void onComplete(@NonNull Task<Story> task) {
                                    if(task.isSuccessful()) {
                                        Story story = task.getResult();
                                    } else {
                                        Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                                        task.getException().printStackTrace();
                                    }
                                }
                            });
                        }
                    }
                });
                mediaListDialogFragment.show(getChildFragmentManager(), "mediaList");


//                PickImageBottomSheetFragment pickImageBottomSheetFragment = new PickImageBottomSheetFragment();
//                pickImageBottomSheetFragment.show(getChildFragmentManager(), "pickImage");
//                pickImageBottomSheetFragment.setCallBack(new PickImageBottomSheetFragment.PickImageCallBack() {
//                    @Override
//                    public void pickImageComplete(Uri uri) {
//                        Uri fileUri = Uri.parse(FileUtils.getFilePathFromContentUri(getContext(), uri));
//                        File file = new File(fileUri.getPath());
//                        if(!file.exists()) {
//                            Toast.makeText(getContext(), "File not found", Toast.LENGTH_SHORT).show();
//                            return;
//                        }
//                        long maxSize = 50000000;
//                        if(file.length() > maxSize) {
//                            Toast.makeText(getContext(), "Kích thước ảnh hoặc video lớn hơn 50mb", Toast.LENGTH_SHORT).show();
//                            return;
//                        }
//                        StoryRepository.getInstance().createStory(fileUri).addOnCompleteListener(new OnCompleteListener<Story>() {
//                            @Override
//                            public void onComplete(@NonNull Task<Story> task) {
//                                if(task.isSuccessful()) {
//                                    Story story = task.getResult();
//                                } else {
//                                    Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
//                                    task.getException().printStackTrace();
//                                }
//                            }
//                        });
//                    }
//                });
            }

            @Override
            public void onStoryClicked(StoryAdapterItem storyAdapterItem) {

                Intent intent = new Intent(getContext(), StoryActivity.class);
                intent.putExtra("storyAdapterItems", storyAdapter.getStoryAdapterItems());
                startActivity(intent);
            }
        });
        return view;
    }


}
