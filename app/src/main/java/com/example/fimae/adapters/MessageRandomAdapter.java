package com.example.fimae.adapters;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fimae.models.Message;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;


public class MessageRandomAdapter extends FirestoreAdapter<MessageRandomAdapter.MessageViewHolder>{
    ArrayList<Message> messages;
    @Override
    public void OnSuccessQueryListener(ArrayList<DocumentSnapshot> queryDocumentSnapshots, ArrayList<DocumentChange> documentChanges) {
        if (messages == null) {
            messages = new ArrayList<>();
        }
        messages.clear();
        for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
            Message message = snapshot.toObject(Message.class);
            messages.add(message);
        }
        notifyDataSetChanged();
    }

    public MessageRandomAdapter(Query query) {
        super(query);
    }



    class MessageViewHolder extends RecyclerView.ViewHolder{

        public MessageViewHolder(@NonNull ViewGroup parent, int viewType) {
            super(parent);
        }
    }
    @NonNull
    @Override
    public MessageRandomAdapter.MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull MessageRandomAdapter.MessageViewHolder holder, int position) {

    }
}
