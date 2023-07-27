package com.example.fimae.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.fimae.R;
import com.example.fimae.adapters.UserHomeViewAdapter;
import com.example.fimae.models.Fimaers;
import com.example.fimae.repository.ChatRepository;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

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
        recyclerViewRes = findViewById(R.id.recycler_view_result);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        adapter = new UserHomeViewAdapter(this);
        searchView.setOnQueryTextListener(this);
        recyclerViewRes.setLayoutManager(manager);
        adapter.getFilter().filter("");
        recyclerViewRes.setAdapter(adapter);
        fimaeUserRef.get().addOnCompleteListener(task -> adapter.setData(task.getResult().toObjects(Fimaers.class), new UserHomeViewAdapter.IClickCardUserListener() {
            @Override
            public void onClickUser(Fimaers user) {
                Intent intent = new Intent(SearchUserActivity.this, ProfileActivity.class);
                intent.putExtra("uid", user.getUid());
                startActivity(intent);
            }
        }));
        searchView.setOnClickListener(v -> searchView.setIconified(false));
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