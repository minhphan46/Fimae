package com.example.fimae.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fimae.R;
import com.example.fimae.activities.MediaSliderActivity;
import com.example.fimae.activities.PostContentActivity;
import com.example.fimae.models.Message;
import com.example.fimae.models.Post;
import com.example.fimae.repository.ChatRepository;
import com.example.fimae.repository.PostRepository;
import com.google.android.gms.tasks.OnSuccessListener;
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
    final int SENDER_POST_VIEW_HOLDER = 3;
    public MessageAdapter(Query query, Context context) {
        super(query);
        this.context = context;
        startListening();
    }



    public int getMessageType(int position){
        Message message = getSnapshot(position).toObject(Message.class);
        if(message.getType() == Message.POST_LINK){
            return  SENDER_POST_VIEW_HOLDER;
        }
        return  (Objects.equals(message.getIdSender(), FirebaseAuth.getInstance().getUid())) ? SENDER_VIEW_HOLDER : RECEIVER_VIEW_HOLDER;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int type = getMessageType(viewType);
        if (type == SENDER_VIEW_HOLDER) {
            View view = LayoutInflater.from(context).inflate(R.layout.sender_message, parent, false);
            return new OutgoingViewholder(view);
        } else if(type == RECEIVER_VIEW_HOLDER) {
            View view = LayoutInflater.from(context).inflate(R.layout.receiver_message, parent, false);
            return new IncomingViewholder(view);
        }
        else {
            View view = LayoutInflater.from(context).inflate(R.layout.post_link, parent, false);
            return new PostLinkViewHolder(view);

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
                ImageMessageAdapter imageMessageAdapter = new ImageMessageAdapter(urls);
                imageMessageAdapter.setOnClickMediaItem(new ImageMessageAdapter.IClickMediaItem() {
                    @Override
                    public void onClick(String url, int position) {
                        Intent intent = new Intent(context, MediaSliderActivity.class);
                        intent.putExtra("urls", urls);
                        intent.putExtra("currentIndex", position);
                        context.startActivity(intent);
                    }
                });

                outgoingViewholder.recyclerView.setAdapter(imageMessageAdapter);
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
                ImageMessageAdapter imageMessageAdapter = new ImageMessageAdapter(urls);
                imageMessageAdapter.setOnClickMediaItem(new ImageMessageAdapter.IClickMediaItem() {
                    @Override
                    public void onClick(String url, int position) {
                        Intent intent = new Intent(context, MediaSliderActivity.class);
                        intent.putExtra("urls", urls);
                        intent.putExtra("currentIndex", position);
                        context.startActivity(intent);
                    }
                });
                incomingViewholder.recyclerView.setAdapter(imageMessageAdapter);
                int col = calculateGridColumns(urls.size());
                GridLayoutManager gridLayoutManager = new GridLayoutManager(holder.itemView.getContext(), col);
                incomingViewholder.recyclerView.setLayoutManager(gridLayoutManager);
                incomingViewholder.incomingMsg.setVisibility(View.GONE);
            }
        }
        else if(holder instanceof PostLinkViewHolder){
            PostLinkViewHolder postLinkViewHolder = (PostLinkViewHolder) holder;
            assert message != null;
            PostRepository.getInstance().getPostById(message.getContent().toString()).addOnSuccessListener(post -> {
                if(post.getPostImages() != null && !post.getPostImages().isEmpty()){
                    Glide.with(context).load(post.getPostImages().get(0)).into(postLinkViewHolder.imageView);
                    String content = message.getContent().toString();
                    if(content.length() > 20){
                       content = content.substring(20);
                    }
                    postLinkViewHolder.url.setText(content);
                }
            });
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
    public static class PostLinkViewHolder extends RecyclerView.ViewHolder{
        SquareImageView imageView;
        TextView url;
        public PostLinkViewHolder(@NonNull View itemView){
            super(itemView);
            imageView = itemView.findViewById(R.id.post_image);
            url= itemView.findViewById(R.id.post_link_img);
        }
    }
}
