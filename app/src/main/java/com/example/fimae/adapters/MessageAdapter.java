package com.example.fimae.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fimae.R;
import com.example.fimae.activities.DetailPostActivity;
import com.example.fimae.activities.MediaSliderActivity;
import com.example.fimae.models.Message;
import com.example.fimae.repository.ChatRepository;
import com.example.fimae.repository.PostRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Objects;

public class MessageAdapter extends FirestoreAdapter {
    Context context;
    final int SENDER_VIEW_HOLDER = 0;
    final int RECEIVER_VIEW_HOLDER = 1;
    final int SENDER_POST_VIEW_HOLDER = 3;
    final int RECEIVER_POST_VIEW_HOLDER = 4;

    public MessageAdapter(Query query, Context context) {
        super(query);
        this.context = context;
        startListening();
    }

    private ArrayList<Message> messages;

    Message getMessage(int position) {
        if (messages == null)
            return null;
        return messages.get(position);
    }

    public int getMessageType(int position) {
        Message message = getMessage(position);
        Log.i("Tag", message.getType());
        String x = message.getType();
        if (message.getType().equals(Message.POST_LINK)) {
            if (!Objects.equals(message.getIdSender(), FirebaseAuth.getInstance().getUid())) {
                return RECEIVER_POST_VIEW_HOLDER;
            }
            return SENDER_POST_VIEW_HOLDER;
        }
        return (Objects.equals(message.getIdSender(), FirebaseAuth.getInstance().getUid())) ? SENDER_VIEW_HOLDER : RECEIVER_VIEW_HOLDER;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int type = getMessageType(viewType);
        if (type == SENDER_VIEW_HOLDER) {
            View view = LayoutInflater.from(context).inflate(R.layout.sender_message, parent, false);
            return new OutgoingViewholder(view);
        } else if (type == RECEIVER_VIEW_HOLDER) {
            View view = LayoutInflater.from(context).inflate(R.layout.receiver_message, parent, false);
            return new IncomingViewholder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.post_link_sender, parent, false);
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
        Message message = getMessage(position);

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
        } else if (holder instanceof PostLinkViewHolder) {
            PostLinkViewHolder postLinkViewHolder = (PostLinkViewHolder) holder;
            if (getMessageType(position) == SENDER_POST_VIEW_HOLDER) {
                ((PostLinkViewHolder) holder).linearLayout.setGravity(Gravity.END);
            } else {
                ((PostLinkViewHolder) holder).linearLayout.setGravity(Gravity.START);
                ((PostLinkViewHolder) holder).title.setTextColor(Color.BLACK);
                ((PostLinkViewHolder) holder).labelButton.setTextColor(Color.BLACK);
                ((PostLinkViewHolder) holder).constraintLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.gray_400));
            }

            assert message != null;
            PostRepository.getInstance().getPostById(message.getContent().toString()).addOnSuccessListener(post -> {
                if (post.getPostImages() != null && !post.getPostImages().isEmpty()) {
                    Glide.with(context).load(post.getPostImages().get(0)).into(postLinkViewHolder.imageView);
                } else {
                    postLinkViewHolder.imageView.setVisibility(View.GONE);
                }
                String content = post.getContent();
                postLinkViewHolder.title.setText(content);
            });
            postLinkViewHolder.itemView.setOnClickListener(view -> {
                Intent intent = new Intent(context, DetailPostActivity.class);
                intent.putExtra("id", message.getContent().toString());
                context.startActivity(intent);
            });
        }
    }



    @Override
    public void OnSuccessQueryListener(ArrayList queryDocumentSnapshots, ArrayList arrayList) {
        if (messages == null) {
            messages = new ArrayList<>();
        }
        messages.clear();
        for (Object document : queryDocumentSnapshots) {
            Message message = ((DocumentSnapshot) document).toObject(Message.class);
            messages.add(message);
        }
        if (!messages.isEmpty()) {
            Message lastMessage = messages.get(messages.size() - 1);
            ChatRepository.getInstance().updateReadLastMessageAt(lastMessage.getConversationID(), lastMessage.getSentAt());
        }
        notifyDataSetChanged();
        return;
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

    public static class PostLinkViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView title;
        TextView labelButton;
        CardView cardView;
        LinearLayout linearLayout;
        ConstraintLayout constraintLayout;

        public PostLinkViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.post_image);
            title = itemView.findViewById(R.id.post_message_title);
            cardView = itemView.findViewById(R.id.post_link_card);
            linearLayout = itemView.findViewById(R.id.post_message_linear_layout);
            labelButton = itemView.findViewById(R.id.label_post_view);
            constraintLayout = itemView.findViewById(R.id.post_message_layout);
        }

    }

    @Override
    public int getItemCount() {
        if (messages == null) {
            return 0;
        }
        return messages.size();
    }

}
