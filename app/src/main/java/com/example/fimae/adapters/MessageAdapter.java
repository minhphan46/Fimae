package com.example.fimae.adapters;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fimae.components.MessageView;
import com.example.fimae.models.Fimaers;
import com.example.fimae.models.Message;
import com.example.fimae.repository.ChatRepository;
import com.example.fimae.repository.FimaerRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
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
        MessageView messageView = new MessageView(context);
        return new IncomingViewholder(messageView);
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

    HashMap<String, Fimaers> fimaersHashMap = new HashMap<>();
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = getMessage(position);
        MessageView messageView = (MessageView) holder.itemView;
        messageView.setMessage(message);
        Fimaers fimaers = fimaersHashMap.get(message.getIdSender());
        if (fimaers == null) {
            FimaerRepository.getInstance().getFimaerById(message.getIdSender()).addOnSuccessListener(fimaers1 -> {
                fimaersHashMap.put(message.getIdSender(), fimaers1);
                messageView.setFimaers(fimaers1);
            });
        } else {
            messageView.setFimaers( fimaers);
        }



//        if (holder instanceof OutgoingViewholder) {
//            OutgoingViewholder outgoingViewholder = (OutgoingViewholder) holder;
//            if (Message.TEXT.equals(message.getType())) {
//                outgoingViewholder.outgoingMsg.setText(message.getContent().toString());
//                outgoingViewholder.recyclerView.setVisibility(View.GONE);
//            } else if (Message.MEDIA.equals(message.getType())) {
//                ArrayList<String> urls = (ArrayList<String>) message.getContent();
//                ImageMessageAdapter imageMessageAdapter = new ImageMessageAdapter(urls);
//                imageMessageAdapter.setOnClickMediaItem(new ImageMessageAdapter.IClickMediaItem() {
//                    @Override
//                    public void onClick(String url, int position) {
//                        Intent intent = new Intent(context, MediaSliderActivity.class);
//                        intent.putExtra("urls", urls);
//                        intent.putExtra("currentIndex", position);
//                        context.startActivity(intent);
//                    }
//                });
//
//                outgoingViewholder.recyclerView.setAdapter(imageMessageAdapter);
//                int col = calculateGridColumns(urls.size());
//                GridLayoutManager gridLayoutManager = new GridLayoutManager(holder.itemView.getContext(), col);
//                outgoingViewholder.recyclerView.setLayoutManager(gridLayoutManager);
//                outgoingViewholder.outgoingMsg.setVisibility(View.GONE);
//            }
//        } else if (holder instanceof IncomingViewholder) {
//            IncomingViewholder incomingViewholder = (IncomingViewholder) holder;
//            incomingViewholder.incomingMsg.setText(message.getContent().toString());
//            if (Message.TEXT.equals(message.getType())) {
//                incomingViewholder.incomingMsg.setText(message.getContent().toString());
//                incomingViewholder.recyclerView.setVisibility(View.GONE);
//            } else if (Message.MEDIA.equals(message.getType())) {
//                ArrayList<String> urls = (ArrayList<String>) message.getContent();
//                ImageMessageAdapter imageMessageAdapter = new ImageMessageAdapter(urls);
//                imageMessageAdapter.setOnClickMediaItem(new ImageMessageAdapter.IClickMediaItem() {
//                    @Override
//                    public void onClick(String url, int position) {
//                        Intent intent = new Intent(context, MediaSliderActivity.class);
//                        intent.putExtra("urls", urls);
//                        intent.putExtra("currentIndex", position);
//                        context.startActivity(intent);
//                    }
//                });
//                incomingViewholder.recyclerView.setAdapter(imageMessageAdapter);
//                int col = calculateGridColumns(urls.size());
//                GridLayoutManager gridLayoutManager = new GridLayoutManager(holder.itemView.getContext(), col);
//                incomingViewholder.recyclerView.setLayoutManager(gridLayoutManager);
//                incomingViewholder.incomingMsg.setVisibility(View.GONE);
//            }
//        } else if (holder instanceof PostLinkViewHolder) {
//            PostLinkViewHolder postLinkViewHolder = (PostLinkViewHolder) holder;
//            if (getMessageType(position) == SENDER_POST_VIEW_HOLDER) {
//                ((PostLinkViewHolder) holder).linearLayout.setGravity(Gravity.END);
//            } else {
//                ((PostLinkViewHolder) holder).linearLayout.setGravity(Gravity.START);
//                ((PostLinkViewHolder) holder).title.setTextColor(Color.BLACK);
//                ((PostLinkViewHolder) holder).labelButton.setTextColor(Color.BLACK);
//                ((PostLinkViewHolder) holder).constraintLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.gray_400));
//            }
//
//            assert message != null;
//            PostRepository.getInstance().getPostById(message.getContent().toString()).addOnSuccessListener(post -> {
//                if (post.getPostImages() != null && !post.getPostImages().isEmpty()) {
//                    Glide.with(context).load(post.getPostImages().get(0)).into(postLinkViewHolder.imageView);
//                } else {
//                    postLinkViewHolder.imageView.setVisibility(View.GONE);
//                }
//                String content = post.getContent();
//                postLinkViewHolder.title.setText(content);
//            });
//            postLinkViewHolder.itemView.setOnClickListener(view -> {
//                Intent intent = new Intent(context, DetailPostActivity.class);
//                intent.putExtra("id", message.getContent().toString());
//                context.startActivity(intent);
//            });
//        }
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


    public static class IncomingViewholder extends RecyclerView.ViewHolder {
        MessageView messageView;
        public IncomingViewholder(@NonNull View itemView) {
            super(itemView);
            messageView = (MessageView) itemView;
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
