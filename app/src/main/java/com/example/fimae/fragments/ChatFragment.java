package com.example.fimae.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fimae.R;
import com.example.fimae.activities.DatingSettings;
import com.example.fimae.activities.OnChatActivity;
import com.example.fimae.activities.SearchUserActivity;
import com.example.fimae.activities.StoryActivity;
import com.example.fimae.adapters.ConversationAdapter;
import com.example.fimae.adapters.SpacingItemDecoration;
import com.example.fimae.adapters.StoryAdapter.StoryAdapter;
import com.example.fimae.adapters.StoryAdapter.StoryAdapterItem;
import com.example.fimae.models.Conversation;
import com.example.fimae.models.Fimaers;
import com.example.fimae.models.story.Story;
import com.example.fimae.repository.ChatRepository;
import com.example.fimae.repository.StoryRepository;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.*;

public class ChatFragment extends Fragment {
    private FirebaseFirestore firestore;
    private CollectionReference fimaeUserRef;
    private CollectionReference conversationRef;
    ConversationAdapter adapter;
    private LinearLayout searchbar;

    @Override
    public void onOptionsMenuClosed(@NonNull Menu menu) {
        super.onOptionsMenuClosed(menu);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Toast.makeText(getContext(), "Add friend", Toast.LENGTH_SHORT).show();
                switch (item.getItemId()) {
                    case R.id.action_add_friend:
                        Intent intent = new Intent(getContext(), DatingSettings.class);
                        intent.putExtra("uid", FirebaseAuth.getInstance().getUid());
                        startActivity(intent);
                        return true;
                    default:
                        return false;
                }
            }
        });
        RecyclerView recyclerView = view.findViewById(R.id.list_user);
        searchbar = view.findViewById(R.id.search_bar);
        Query query = ChatRepository.getDefaultChatInstance().getConversationQuery();
        adapter = new ConversationAdapter(query, new ConversationAdapter.IClickConversationListener() {
            @Override
            public void onClickConversation(Conversation conversation, Fimaers fimaers) {
                Intent intent = new Intent(getContext(), OnChatActivity.class);
                intent.putExtra("conversationID", conversation.getId());
                intent.putExtra("fimaer", fimaers);
                startActivity(intent);
            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);

        searchbar.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), SearchUserActivity.class);
            startActivity(intent);
        });
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (adapter != null) {
            adapter.stopListening();
        }
    }
}
