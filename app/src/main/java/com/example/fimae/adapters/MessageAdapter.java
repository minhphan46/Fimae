package com.example.fimae.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import com.example.fimae.R;
import com.example.fimae.models.Message;

import java.util.List;

public class MessageAdapter extends ArrayAdapter<Message> {


    public MessageAdapter(Context context, int resource, List<Message> messages) {
        super(context, resource, messages);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            if(position%2==0)
                convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.sender_message, parent, false);
            else
                convertView = LayoutInflater.from(getContext())
                        .inflate(R.layout.receiver_message, parent, false);
        }

        return convertView;
    }
}
