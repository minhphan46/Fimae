package com.example.fimae.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.fimae.R;
import com.example.fimae.activities.OnChatActivity;
import com.example.fimae.adapters.UserHomeViewAdapter;
import com.example.fimae.models.FimaeUser;

import java.util.Arrays;

public class ChatFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.list_user);
        UserHomeViewAdapter userHomeViewAdapter = new UserHomeViewAdapter();
        userHomeViewAdapter.setData(Arrays.asList(FimaeUser.dummy), new UserHomeViewAdapter.IClickCardUserListener() {
            @Override
            public void onClickUser(FimaeUser user) {
                Intent intent = new Intent(getContext(), OnChatActivity.class);
                startActivity(intent);
            }
        });
        TextView textView = view.findViewById(R.id.search_title);
        textView.setText("Okok");
        System.out.println("-----------------------------------");
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getContext());
        recyclerView.setAdapter(userHomeViewAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);
        return view;
    }
}
