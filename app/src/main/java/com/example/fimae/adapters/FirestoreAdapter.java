package com.example.fimae.adapters;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

public abstract class FirestoreAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
    private Query query;
    private ListenerRegistration registration;
    protected ArrayList<DocumentSnapshot> snapshots;
    private static final String TAG = "FirestoreAdapter";

    public abstract void OnSuccessQueryListener(ArrayList<DocumentSnapshot> queryDocumentSnapshots, ArrayList<DocumentChange> documentChanges);

    public FirestoreAdapter(Query query) {
        this.query = query;
        startListening();
    }

    public void startListening() {
        registration = query.addSnapshotListener((value, error) -> {
            if(value!= null){
                snapshots = (ArrayList<DocumentSnapshot>) value.getDocuments();
                ArrayList<DocumentChange> documentChanges = new ArrayList<>(value.getDocumentChanges());
                OnSuccessQueryListener(snapshots,  documentChanges);
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

        if (snapshots != null) {
            snapshots.clear();
            notifyDataSetChanged();
        }
    }

    public void setQuery(Query query) {
        // Stop listening
        stopListening();

        if (snapshots != null) {
            snapshots.clear();
            notifyDataSetChanged();
        }

        // Listen to new query
        this.query = query;
        startListening();
    }

    protected DocumentSnapshot getSnapshot(int index) {
        if (snapshots != null && snapshots.size() > index) {
            return snapshots.get(index);
        }
        return null;
    }

    @Override
    public int getItemCount() {
        if (snapshots != null) {
            return snapshots.size();
        }
        return 0;
    }

}
