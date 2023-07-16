package com.example.fimae.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fimae.R;
import com.example.fimae.models.Message;
import com.example.fimae.repository.ChatRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MessageAdapter extends  FirestoreAdapter{
    Context context;
    final int SENDER_VIEW_HOLDER = 0;
    final int RECEIVER_VIEW_HOLDER = 1;
    public MessageAdapter(Query query, Context context) {
        super(query);
        this.context = context;
        startListening();
    }



    public int getMessageType(int position){
        Message message = getSnapshot(position).toObject(Message.class);
        return  (Objects.equals(message.getIdSender(), FirebaseAuth.getInstance().getUid())) ? SENDER_VIEW_HOLDER : RECEIVER_VIEW_HOLDER;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int type = getMessageType(viewType);
        if (type == SENDER_VIEW_HOLDER) {
            View view = LayoutInflater.from(context).inflate(R.layout.sender_message, parent, false);
            return new OutgoingViewholder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.receiver_message, parent, false);
            return new IncomingViewholder(view);
        }
    }
    private int calculateGridColumns(int urlsSize) {
        if (urlsSize == 1) {
            return 1;
        } else if (urlsSize % 2 == 0 && urlsSize <= 4) {
            return 2;
        } else {
            return 3;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = getSnapshot(position).toObject(Message.class);

        if (holder instanceof OutgoingViewholder) {
            OutgoingViewholder outgoingViewholder = (OutgoingViewholder) holder;
            if (Message.TEXT.equals(message.getType())) {
                outgoingViewholder.outgoingMsg.setText(message.getContent().toString());
                outgoingViewholder.recyclerView.setVisibility(View.GONE);
            } else if (Message.MEDIA.equals(message.getType())) {
                ArrayList<String> urls = (ArrayList<String>) message.getContent();
                outgoingViewholder.recyclerView.setAdapter(new ImageMessageAdapter(urls));
                int col = calculateGridColumns(urls.size());
                GridLayoutManager gridLayoutManager = new GridLayoutManager(holder.itemView.getContext(), col);
                outgoingViewholder.recyclerView.setLayoutManager(gridLayoutManager);
                outgoingViewholder.outgoingMsg.setVisibility(View.GONE);
            }
        } else if (holder instanceof IncomingViewholder) {
            IncomingViewholder incomingViewholder = (IncomingViewholder) holder;
            incomingViewholder.incomingMsg.setText(message.getContent().toString());
            if (Message.TEXT.equals(message.getType())) {
                incomingViewholder.incomingMsg.setText(message.getContent().toString());
                incomingViewholder.recyclerView.setVisibility(View.GONE);
            } else if (Message.MEDIA.equals(message.getType())) {
                ArrayList<String> urls = (ArrayList<String>) message.getContent();
                incomingViewholder.recyclerView.setAdapter(new ImageMessageAdapter(urls));
                int col = calculateGridColumns(urls.size());
                GridLayoutManager gridLayoutManager = new GridLayoutManager(holder.itemView.getContext(), col);
                incomingViewholder.recyclerView.setLayoutManager(gridLayoutManager);
                incomingViewholder.incomingMsg.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void OnSuccessQueryListener(ArrayList queryDocumentSnapshots) {
        snapshots = queryDocumentSnapshots;
        if(!snapshots.isEmpty()){
            DocumentSnapshot lastVisible = (DocumentSnapshot) snapshots.get(snapshots.size() - 1);
            Message lastMessage = lastVisible.toObject(Message.class);
            ChatRepository.getInstance().updateReadLastMessageAt(lastMessage.getConversationID(), lastMessage.getSentAt());
        }
        notifyDataSetChanged();
    }

    public static class OutgoingViewholder extends RecyclerView.ViewHolder {
        TextView outgoingMsg;
        RecyclerView recyclerView;

        public OutgoingViewholder(@NonNull View itemView) {
            super(itemView);
            outgoingMsg = itemView.findViewById(R.id.outgoing_msg);
            recyclerView = itemView.findViewById(R.id.list_images);
        }
    }

    public static class IncomingViewholder extends RecyclerView.ViewHolder {
        TextView incomingMsg;
        RecyclerView recyclerView;
        public IncomingViewholder(@NonNull View itemView) {
            super(itemView);
            incomingMsg = itemView.findViewById(R.id.incoming_msg);
            recyclerView = itemView.findViewById(R.id.list_images);
        }
    }
}
