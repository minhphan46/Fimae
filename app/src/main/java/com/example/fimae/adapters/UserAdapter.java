package com.example.fimae.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.fimae.R;
import com.example.fimae.models.UserInfo;
import com.squareup.picasso.Picasso;

import java.util.List;

public class UserAdapter extends ArrayAdapter<UserInfo> {

    public UserAdapter(Context context, int resource, List<UserInfo> users) {
        super(context, resource, users);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.user_chat, parent, false);
        }

        UserInfo user = getItem(position);

        ImageView avatarImageView = convertView.findViewById(R.id.avatar_image_view);
        TextView nameTextView = convertView.findViewById(R.id.name_text_view);
        TextView ageTextView = convertView.findViewById(R.id.age_text_view);
        Picasso.get().load(user.getAvatarUrl()).placeholder(R.drawable.ic_default_avatar).into(avatarImageView);

        LinearLayout linearLayout = convertView.findViewById(R.id.gender_age_icon);
        linearLayout.setBackgroundColor(R.color.green);
//        linearLayout.setBackgroundTintList(ColorStateList.valueOf(R.color.green));
        nameTextView.setText(user.getName());
        ageTextView.setText(String.valueOf(user.getAge()));
        //genderTextView.setText("Gender: " + (user.isMale() ? "Male" : "Female"));

        return convertView;
    }
}
