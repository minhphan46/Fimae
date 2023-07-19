package com.example.fimae.fragments;

import android.content.Intent;
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
import com.example.fimae.Story.StoryView;
import com.example.fimae.Story.callback.StoryClickListeners;
import com.example.fimae.activities.OnChatActivity;
import com.example.fimae.activities.SearchUserActivity;
import com.example.fimae.adapters.ConversationAdapter;
import com.example.fimae.adapters.SpacingItemDecoration;
import com.example.fimae.adapters.StoryAdapter;
import com.example.fimae.adapters.UserHomeViewAdapter;
import com.example.fimae.models.Conversation;
import com.example.fimae.models.Fimaers;
import com.example.fimae.models.Participant;
import com.example.fimae.models.story.Story;
import com.example.fimae.repository.ChatRepository;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.WriteBatch;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

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
        StoryAdapter storyAdapter = new StoryAdapter();
        storyRecyclerView.setAdapter(storyAdapter);
        storyAdapter.setStoryListener(new StoryAdapter.StoryListener() {
            @Override
            public void addStoryClicked() {

            }

            @Override
            public void onStoryClicked(int position) {
                ArrayList<Fimaers> headerInfoArrayList =Fimaers.dummy;
                new StoryView.Builder(getChildFragmentManager())
                        .setStoriesList(Story.getFakeData()) // MyStory's ArrayList
                        .setStoryDuration(5000) // Optional, default is 2000 Millis
                        .setHeadingInfoList(headerInfoArrayList) // StoryViewHeaderInfo's ArrayList
                        .setStoryClickListeners(new StoryClickListeners() {
                            @Override
                            public void onDescriptionClickListener(int position) {
                                // your action
                            }

                            @Override
                            public void onTitleIconClickListener(int position) {
                                // your action
                            }
                        }) // Optional Listeners
                        .build() // Must be called before show method
                        .show();
            }
        });
        return view;
    }


}
