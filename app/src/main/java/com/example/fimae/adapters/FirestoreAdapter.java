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
    protected ArrayList<DocumentSnapshot> snapshots = new ArrayList<>();
    private static final String TAG = "FirestoreAdapter";
    public abstract void OnSuccessQueryListener(ArrayList<DocumentSnapshot> queryDocumentSnapshots);
    public void OnSuccessQueryListener(ArrayList<DocumentSnapshot> queryDocumentSnapshots, ArrayList<DocumentChange> documentChanges){

    }

    public FirestoreAdapter(Query query) {
        this.query = query;
        startListening();
    }

    public void startListening() {
        registration = query.addSnapshotListener((value, error) -> {
            if(value!= null){
                OnSuccessQueryListener((ArrayList<DocumentSnapshot>) value.getDocuments());
                ArrayList<DocumentChange> documentChanges =new ArrayList<>();
                for(DocumentChange change: value.getDocumentChanges()){
                    documentChanges.add(change);
                }
                OnSuccessQueryListener((ArrayList<DocumentSnapshot>) value.getDocuments(),  documentChanges);
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
