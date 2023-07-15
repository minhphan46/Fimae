package com.example.fimae.adapters;

import android.util.Log;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fimae.models.Message;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public abstract class FirestoreAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
    private Query query;
    private ListenerRegistration registration;
    private ArrayList<DocumentSnapshot> snapshots = new ArrayList<>();
    private static final String TAG = "FirestoreAdapter";

    public FirestoreAdapter(Query query) {
        this.query = query;
        startListening();
    }

    public void startListening() {
        registration = query.addSnapshotListener((value, error) -> {
            if(value!= null){
                snapshots.clear();
                snapshots.addAll(value.getDocuments());
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public void stopListening() {
        if (registration != null) {
            registration.remove();
            registration = null;
        }

        snapshots.clear();
        notifyDataSetChanged();
    }

    public void setQuery(Query query) {
        // Stop listening
        stopListening();

        // Clear existing data
        snapshots.clear();
        notifyDataSetChanged();

        // Listen to new query
        this.query = query;
        startListening();
    }

    protected DocumentSnapshot getSnapshot(int index) {
        return snapshots.get(index);
    }

    @Override
    public int getItemCount() {
        return snapshots.size();
    }

}
