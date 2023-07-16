package com.example.fimae.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.fimae.R;
import com.example.fimae.adapters.UserHomeViewAdapter;
import com.example.fimae.models.Conversation;
import com.example.fimae.models.Fimaers;
import com.example.fimae.repository.ChatRepository;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class SearchUserActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    SearchView searchView;
    RecyclerView recyclerViewRes;
    private FirebaseFirestore firestore;
    private CollectionReference fimaeUserRef;
    private UserHomeViewAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firestore = FirebaseFirestore.getInstance();
        fimaeUserRef = firestore.collection("fimaers");
        setContentView(R.layout.activity_search_user);
        searchView = findViewById(R.id.searchView);
        recyclerViewRes =findViewById(R.id.recycler_view_result);
        LinearLayoutManager manager =new LinearLayoutManager(this);
         adapter = new UserHomeViewAdapter();
        searchView.setOnQueryTextListener(this);
        recyclerViewRes.setLayoutManager(manager);
        adapter.getFilter().filter("");
        recyclerViewRes.setAdapter(adapter);
        fimaeUserRef.get().addOnCompleteListener(task -> adapter.setData(task.getResult().toObjects(Fimaers.class), user -> {
            Log.d("SearchUserActivity", "onCreateOrGetConversationWith : " + user.getUid());
            ChatRepository.getInstance().getOrCreateFriendConversation(user.getUid()).addOnSuccessListener(conversation -> {
                if(task.isSuccessful()){
                    Intent intent = new Intent(SearchUserActivity.this, OnChatActivity.class);
                    intent.putExtra("conversationID", conversation.getId());
                    startActivity(intent);
                } else {
                    Toast.makeText(SearchUserActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            });

        }));
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        adapter.getFilter().filter(query);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        adapter.getFilter().filter(newText);
        return false;
    }
}