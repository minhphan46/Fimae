package com.example.fimae.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Toast;
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
import com.google.firebase.Timestamp;
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
            System.out.println("***********AAAAAAAA het***********");
            ArrayList<String> participants = new ArrayList<String>(){{
                add(user.getUid());
                add(FirebaseAuth.getInstance().getUid());
            }};
            participants.sort(Comparator.naturalOrder());
            conversationRef.where(Filter.equalTo("participantIDs", participants)).limit(1).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                    Intent intent = new Intent(getContext(), OnChatActivity.class);

                    if(task.isSuccessful()){
                        System.out.println(task.getResult().getDocuments());
                        if(!task.getResult().isEmpty()){
                            Conversation conversation = new Conversation();
                            conversation = task.getResult().getDocuments().get(0).toObject(Conversation.class);
                            assert conversation != null;
                            conversation.setId(task.getResult().getDocuments().get(0).getId());
                            intent.putExtra("conversationID", conversation.getId());
                            startActivity(intent);
                        } else {
                            System.out.println("***********NONE***********");
                            Conversation conversation = new Conversation();
                            conversation.setCreatedAt(Timestamp.now());
                            conversation.setType(Conversation.FRIEND_CHAT);
                            conversation.setParticipantIDs(participants);
                            HashMap<String, Object> createConversation = new HashMap<>();
                            createConversation.put("createdAt", conversation.getCreatedAt());
                            createConversation.put("type", conversation.getType());
                            createConversation.put("participantIDs", conversation.getParticipantIDs() );
                            DocumentReference newConDoc = conversationRef.document();
                            newConDoc.set(createConversation).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull @NotNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        conversation.setId(newConDoc.getId());
                                        intent.putExtra("conversationID", conversation.getId());
                                        startActivity(intent);
                                    } else {
                                        Toast.makeText(getContext(),"Lỗi: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    } else {
                        Toast.makeText(getContext(),"Lỗi: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });


        });
        return view;

    }


}
