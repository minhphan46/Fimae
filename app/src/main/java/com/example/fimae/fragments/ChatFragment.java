package com.example.fimae.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.fimae.R;
import com.example.fimae.activities.OnChatActivity;
import com.example.fimae.adapters.UserHomeViewAdapter;
import com.example.fimae.models.Conversation;
import com.example.fimae.models.Fimaers;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.*;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ChatFragment extends Fragment {
    private FirebaseFirestore firestore;
    private CollectionReference fimaeUserRef;
    private CollectionReference conversationRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        firestore = FirebaseFirestore.getInstance();
        fimaeUserRef = firestore.collection("fimaers");
        conversationRef = firestore.collection("conversations");
        RecyclerView recyclerView = view.findViewById(R.id.list_user);
        UserHomeViewAdapter userHomeViewAdapter = new UserHomeViewAdapter();
        ArrayList<Fimaers> fimaers = new ArrayList<Fimaers>();
        userHomeViewAdapter.setData(fimaers, user -> {
            Intent intent = new Intent(getContext(), OnChatActivity.class);
            startActivity(intent);
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getContext());
        recyclerView.setAdapter(userHomeViewAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);
        fimaeUserRef.addSnapshotListener((value, error) -> {
            if (error != null) {
                // Xử lý lỗi
                return;
            }
            fimaers.clear();
            // Lặp qua các tài liệu (tin nhắn) và thêm vào danh sách
            for (QueryDocumentSnapshot document : value) {
                System.out.println(document.toString());
                Fimaers message = document.toObject(Fimaers.class);
                fimaers.add(message);

            }

            // Cập nhật giao diện người dùng (RecyclerView)
            userHomeViewAdapter.notifyDataSetChanged();
        });
        userHomeViewAdapter.setData(fimaers, user -> {
            ArrayList<String> participants = new ArrayList<String>() {{
                add(FirebaseAuth.getInstance().getCurrentUser().getUid());
                add(user.getUid());
            }};
            conversationRef.whereArrayContainsAny("memberIds", participants).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                    if (task.getResult().getDocuments().size() >= 1 && task.getResult().getDocuments().get(0).exists()) {

                        Conversation conversation = task.getResult().getDocuments().get(0).toObject(Conversation.class);
                        String id = task.getResult().getDocuments().get(0).getId();
                        Intent intent = new Intent(getContext(), OnChatActivity.class);
                        System.out.println("=========" + id);
                        intent.putExtra("conversationId", id);
                        startActivity(intent);
                    } else {
                        HashMap<String, Object> conversationReq = new HashMap<>();
                        conversationReq.put("created_at", new Date());
                        conversationReq.put("type", Conversation.FRIEND_CHAT);
                        conversationReq.put("name", "Chat");
                        conversationReq.put("memberIds", participants);
                        DocumentReference newConver = conversationRef.document();
                        newConver.set(conversationReq).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<Void> task) {
                                Intent intent = new Intent(getContext(), OnChatActivity.class);
                                System.out.println("!!!!!!=========" + newConver.getId());
                                intent.putExtra("conversationId", newConver.getId());
                                startActivity(intent);
                            }
                        });
                    }
                }
            });
        });
        return view;
    }


}
