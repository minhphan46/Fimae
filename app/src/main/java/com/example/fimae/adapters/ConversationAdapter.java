package com.example.fimae.adapters;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fimae.R;
import com.example.fimae.models.Conversation;
import com.example.fimae.models.Fimaers;
import com.example.fimae.models.Message;
import com.example.fimae.repository.ChatRepository;
import com.example.fimae.repository.FimaerRepository;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ConversationAdapter extends FirestoreAdapter<ConversationAdapter.ViewHolder> {


    HashMap<String, Fimaers> fimaersHashMap = new HashMap<>();
    ArrayList<Conversation> conversations;

    @Override
    public void OnSuccessQueryListener(ArrayList<DocumentSnapshot> queryDocumentSnapshots, ArrayList<DocumentChange> documentChanges) {
        if (conversations == null) {
            conversations = new ArrayList<>();
            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                Conversation conversation = documentSnapshot.toObject(Conversation.class);
                conversations.add(conversation);
            }
            ArrayList<String> ids = new ArrayList<>();
            for (Conversation conversation : conversations) {
                if (conversation.getOtherParticipantId() != null) {
                    ids.add(conversation.getOtherParticipantId());
                }
            }
            FimaerRepository.getInstance().getFimaersByIds(ids).addOnCompleteListener(new OnCompleteListener<ArrayList<Fimaers>>() {
                @Override
                public void onComplete(@NonNull Task<ArrayList<Fimaers>> task) {
                    if (task.isSuccessful()) {
                        ArrayList<Fimaers> fimaers = task.getResult();
                        for (Fimaers fimaer : fimaers) {
                            fimaersHashMap.put(fimaer.getUid(), fimaer);
                        }
                    }
                    notifyDataSetChanged();
                }
            });
            return;
        }
        conversations.clear();
        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
            Conversation conversation = documentSnapshot.toObject(Conversation.class);
            conversations.add(conversation);
        }
    }

    public interface IClickConversationListener {
        void onClickConversation(Conversation conversation, Fimaers fimaers);
    }

    private IClickConversationListener iClickConversationListener;

    public ConversationAdapter(Query query, IClickConversationListener iClickConversationListener) {
        super(query);
        this.iClickConversationListener = iClickConversationListener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout mLayoutCard;
        private CircleImageView mAvatarView;
        private TextView mTextName;
        private TextView mTextDes;
        private TextView mTextAge;
        private LinearLayout mLayoutGenderAge;
        private ImageView mIconGender;
        private ImageView onlineStatus;
        private TextView offlineStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mLayoutCard = itemView.findViewById(R.id.item_user_layout_card);
            mAvatarView = itemView.findViewById(R.id.item_user_avatar_view);
            mTextName = itemView.findViewById(R.id.item_user_tv_name);
            mTextDes = itemView.findViewById(R.id.item_user_tv_description);
            mTextAge = itemView.findViewById(R.id.item_user_tv_age);
            mLayoutGenderAge = itemView.findViewById(R.id.item_user_layout_gender_age);
            mIconGender = itemView.findViewById(R.id.item_user_ic_gender);

            onlineStatus = itemView.findViewById(R.id.imv_status_indicator);
            offlineStatus = itemView.findViewById(R.id.tv_status);
        }
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View heroView = inflater.inflate(R.layout.item_user, parent, false);
        return new ViewHolder(heroView);
    }

    private Task<Fimaers> getFimaer(String id){
        TaskCompletionSource<Fimaers> taskCompletionSource = new TaskCompletionSource<>();
        Fimaers fimaers = fimaersHashMap.get(id);
        if (fimaers != null) {
            taskCompletionSource.setResult(fimaers);
        } else {
            FimaerRepository.getInstance().getFimaerById(id).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Fimaers fimaer = task.getResult();
                    fimaersHashMap.put(id, fimaer);
                    taskCompletionSource.setResult(fimaer);
                } else {
                    taskCompletionSource.setException(task.getException());
                }
            });
        }
        return taskCompletionSource.getTask();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Conversation conversation = conversations.get(position);
        assert conversation != null;
        if (conversation.getType().equals(Conversation.FRIEND_CHAT)) {
            ArrayList<String> ids = new ArrayList<>(conversation.getParticipantIds());
            ids.remove(FirebaseAuth.getInstance().getUid());
            String uid = ids.get(0);
            if (uid.equals("")) {
                return;
            }
            getFimaer(uid).addOnSuccessListener(user -> {
                if (user == null) {
                    holder.mLayoutCard.setVisibility(View.GONE);
                    return;
                }

                //Convert to Glide
                if (user.getAvatarUrl() == null || user.getAvatarUrl().equals("")) {
                    holder.mAvatarView.setImageResource(R.drawable.ic_default_avatar);
                } else {
                    Glide.with(holder.mAvatarView.getContext()).load(user.getAvatarUrl()).placeholder(R.drawable.ic_default_avatar).into(holder.mAvatarView);
                }
                holder.mTextName.setText(user.getFirstName() + " " + user.getLastName());
                if (conversation.getLastMessage() != null) {
                    conversation.getLastMessage().get().addOnCompleteListener(task -> {
                        HashMap map = (HashMap) task.getResult().getData();
                        Message message = task.getResult().toObject(Message.class);
                        if (message == null) {
                            holder.mTextDes.setText("Bắt đầu cuộc trò chuyện");
                        } else {
                            String start;
                            if (message.getIdSender() == FirebaseAuth.getInstance().getUid()) {
                                start = "Bạn: ";
                            } else {
                                start = user.getFirstName() + ": ";
                            }
                            if (Objects.equals(message.getType(), Message.TEXT)) {
                                String content = message.getContent().toString();
                                if (Objects.equals(message.getIdSender(), FirebaseAuth.getInstance().getUid())) {
                                    content = "Bạn: " + content;
                                }
                                holder.mTextDes.setText(content);
                            } else if (Objects.equals(message.getType(), Message.MEDIA)) {
                                String content = "Đã gửi " + ((ArrayList) message.getContent()).size() + " hình ảnh";
                                if (Objects.equals(message.getIdSender(), FirebaseAuth.getInstance().getUid())) {
                                    content = "Bạn: " + content;
                                }
                                holder.mTextDes.setText(content);
                            } else if (Objects.equals(message.getType(), Message.POST_LINK)) {
                                String content = "Đã gửi một bài viết";
                                if (Objects.equals(message.getIdSender(), FirebaseAuth.getInstance().getUid())) {
                                    content = "Bạn: " + content;
                                }
                                holder.mTextDes.setText(content);
                            }

                        }

                        ChatRepository.getDefaultChatInstance().getParticipantInConversation(conversation.getId(), FirebaseAuth.getInstance().getUid()).addOnSuccessListener(participant -> {
                            if (participant == null || message == null) {
                                holder.mTextDes.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                            } else if (participant.getReadLastMessageAt() != null && participant.getReadLastMessageAt().after(message.getSentAt())) {
                                //set holder.mTextDes fontweight to bold
                                holder.mTextDes.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                            } else {
                                holder.mTextDes.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                            }
                        });
                    });
                }
                holder.mTextAge.setText(String.valueOf(user.calculateAge()));
                holder.mLayoutGenderAge.setBackgroundResource(user.isGender() ? R.drawable.shape_gender_border_blue : R.drawable.shape_gender_border_pink);
                holder.mIconGender.setImageResource(user.isGender() ? R.drawable.ic_male : R.drawable.ic_female);
                if (user.isOnline()) {
                    holder.onlineStatus.setVisibility(View.VISIBLE);
                    holder.offlineStatus.setVisibility(View.GONE);
                } else if (user.getLastActiveMinuteAgo() <= 60) {
                    holder.onlineStatus.setVisibility(View.GONE);
                    holder.offlineStatus.setVisibility(View.VISIBLE);
                    holder.offlineStatus.setText(user.getLastActiveMinuteAgo() + "m");
                } else {
                    holder.onlineStatus.setVisibility(View.GONE);
                    holder.offlineStatus.setVisibility(View.GONE);
                }
                holder.mLayoutCard.setOnClickListener(v -> iClickConversationListener.onClickConversation(conversation, user));
            });
        }

    }

}
