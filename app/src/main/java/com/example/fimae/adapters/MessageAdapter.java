package com.example.fimae.adapters;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.fimae.R;
import com.example.fimae.models.Message;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;

public class MessageAdapter extends RecyclerView.Adapter {

    Context context;
    ArrayList<Message> msgData;

    final int SENDER_VIEW_HOLDER = 0;
    final int RECEIVER_VIEW_HOLDER = 1;

    public MessageAdapter(Context context, ArrayList<Message> msgData) {


        this.msgData = msgData;
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    private int getItemType(int position) {
        return (Objects.equals(msgData.get(position).getIdSender(), FirebaseAuth.getInstance().getUid())) ? SENDER_VIEW_HOLDER : RECEIVER_VIEW_HOLDER;

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        int type = getItemType(viewType);
        if (type == SENDER_VIEW_HOLDER) {
            View view = LayoutInflater.from(context).inflate(R.layout.sender_message, parent, false);
            return new OutgoingViewholder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.receiver_message, parent, false);
            return new IncomingViewholder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = msgData.get(position);

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
    public int getItemCount() {
        return msgData.size();
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
