package com.example.fimae.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.fimae.R;
import com.example.fimae.models.Fimaers;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Field;
import java.util.List;

public class UserAdapter extends ArrayAdapter<Fimaers> {

    public UserAdapter(Context context, int resource, List<Fimaers> users) {
        super(context, resource, users);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.user_chat, parent, false);
        }

        Fimaers user = getItem(position);

        ImageView avatarImageView = convertView.findViewById(R.id.avatar_image_view);
        TextView nameTextView = convertView.findViewById(R.id.name_text_view);
        TextView ageTextView = convertView.findViewById(R.id.age_text_view);
        Picasso.get().load(user.getAvatarUrl()).placeholder(R.drawable.ic_default_avatar).into(avatarImageView);
        nameTextView.setText(user.getLastName());
        ageTextView.setText(String.valueOf(user.calculateAge()));
        return convertView;
    }
}
