package com.example.fimae.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fimae.R;
import com.example.fimae.models.Conversation;
import com.example.fimae.models.Fimaers;
import com.example.fimae.models.Message;
import com.example.fimae.repository.FimaerRepository;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

import java.util.concurrent.ExecutionException;

import de.hdodenhof.circleimageview.CircleImageView;

public class ConversationAdapter extends FirestoreAdapter<ConversationAdapter.ViewHolder> {
    public interface IClickConversationListener {
        void onClickConversation(Conversation conversation);
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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mLayoutCard = itemView.findViewById(R.id.item_user_layout_card);
            mAvatarView = itemView.findViewById(R.id.item_user_avatar_view);
            mTextName = itemView.findViewById(R.id.item_user_tv_name);
            mTextDes = itemView.findViewById(R.id.item_user_tv_description);
            mTextAge = itemView.findViewById(R.id.item_user_tv_age);
            mLayoutGenderAge = itemView.findViewById(R.id.item_user_layout_gender_age);
            mIconGender = itemView.findViewById(R.id.item_user_ic_gender);
        }
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View heroView = inflater.inflate(R.layout.item_user, parent, false);
        return new ViewHolder(heroView);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Conversation conversation = getSnapshot(position).toObject(Conversation.class);
        assert conversation != null;
        if (conversation.getType().equals(Conversation.FRIEND_CHAT)) {
            String uid = "";
            for (String id : conversation.getParticipantIds()) {
                if (!id.equals(FirebaseAuth.getInstance().getUid())) {
                    uid = id;
                    break;
                }
            }
            FimaerRepository.getInstance().getFimaerById(uid).addOnSuccessListener(new OnSuccessListener<Fimaers>() {
                @Override
                public void onSuccess(Fimaers user) {
                    Picasso.get().load(user.getAvatarUrl()).placeholder(R.drawable.ic_default_avatar).into(holder.mAvatarView);
                    holder.mTextName.setText(user.getFirstName());
                    if(conversation.getLastMessage() != null){
                        conversation.getLastMessage().get().addOnCompleteListener(task -> {
                            Message message = task.getResult().toObject(Message.class);
                            assert message != null;
                            holder.mTextDes.setText(message.getContent().toString());
                        });
                    }
                    holder.mTextAge.setText(String.valueOf(user.calculateAge()));
                    holder.mLayoutGenderAge.setBackgroundResource(user.isGender() ? R.drawable.shape_gender_border_blue : R.drawable.shape_gender_border_pink);
                    holder.mIconGender.setImageResource(user.isGender() ? R.drawable.ic_male : R.drawable.ic_female);
                }
            });

        }
        holder.mLayoutCard.setOnClickListener(v -> iClickConversationListener.onClickConversation(conversation));
    }

}
