package com.example.fimae.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fimae.R;
import com.example.fimae.models.UserInfo;
import com.squareup.picasso.Picasso;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserHomeViewAdapter extends  RecyclerView.Adapter<UserHomeViewAdapter.ViewHolder>{

    private List<UserInfo> mUsers;
    private IClickCardUserListener iClickCardUserListener;

    public interface IClickCardUserListener {
        void onClickUser(UserInfo user);
    }

    public void setData( List<UserInfo> mUsers, IClickCardUserListener inIClickCardUserListener) {
        this.mUsers = mUsers;
        this.iClickCardUserListener = inIClickCardUserListener;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
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
        ViewHolder viewHolder = new ViewHolder(heroView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserInfo user = mUsers.get(position);
        if(user == null){
            return;
        }

        Picasso.get().load(user.getAvatarUrl()).placeholder(R.drawable.ic_default_avatar).into(holder.mAvatarView);
        holder.mTextName.setText(user.getName());
        holder.mTextDes.setText(user.getDescription());
        holder.mTextAge.setText(String.valueOf(user.getAge()));
        holder.mLayoutGenderAge.setBackgroundResource(user.isMale() ? R.drawable.shape_gender_border_blue : R.drawable.shape_gender_border_pink);
        holder.mIconGender.setImageResource(user.isMale() ? R.drawable.ic_male : R.drawable.ic_female);

        holder.mLayoutCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iClickCardUserListener.onClickUser(user);
            }
        });
    }

    @Override
    public int getItemCount() {
        if(mUsers != null){
            return mUsers.size();
        }
        return 0;
    }


}
