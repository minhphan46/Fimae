package com.example.fimae.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fimae.R;
import com.example.fimae.activities.OnChatActivity;
import com.example.fimae.activities.SearchUserActivity;
import com.example.fimae.adapters.ConversationAdapter;
import com.example.fimae.models.Conversation;
import com.example.fimae.models.Fimaers;
import com.example.fimae.repository.ChatRepository;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class ChatFragment extends Fragment {
    private FirebaseFirestore firestore;
    private CollectionReference fimaeUserRef;
    private CollectionReference conversationRef;
    ConversationAdapter adapter;
    RecyclerView recyclerView;
    private LinearLayout searchbar;

    void initListener() {
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
        recyclerView.setAdapter(adapter);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        recyclerView = view.findViewById(R.id.list_user);
        searchbar = view.findViewById(R.id.search_bar);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        searchbar.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), SearchUserActivity.class);
            startActivity(intent);
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapter == null) {
            initListener();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (adapter != null) {
            adapter.stopListening();
        }
    }
}
