package com.example.fimae.fragments;

import static androidx.databinding.DataBindingUtil.setContentView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fimae.R;
import com.example.fimae.activities.CallActivity;
import com.example.fimae.activities.ConnectActivity;
import com.example.fimae.activities.MainActivity;
import com.example.fimae.activities.WaitingActivity;
import com.example.fimae.adapters.UserHomeViewAdapter;
import com.example.fimae.models.UserInfo;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HomeFragment extends Fragment  {

    private View mView;
    private RecyclerView mRcvUsers;
    private MainActivity mainActivity;
    private UserHomeViewAdapter userAdapter;
    private List<UserInfo> mUsers;

    private LinearLayout mBtnChat;
    private LinearLayout mBtnCallVoice;
    private LinearLayout mBtnCallVideo;
    private ImageButton mBtnNoti;
    private ImageButton mBtnSetting;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_home, container, false);
        mBtnChat = (LinearLayout) mView.findViewById(R.id.btn_chat_home);
        mBtnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast toast = Toast.makeText(v.getContext(), "Chat", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
        mBtnCallVoice = (LinearLayout) mView.findViewById(R.id.btn_call_home);
        mBtnCallVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToCallScreen(getContext());
            }
        });
        mBtnCallVideo = (LinearLayout) mView.findViewById(R.id.btn_call_video_home);
        mBtnCallVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToCallScreen(getContext());
            }
        });
        mBtnNoti = mView.findViewById(R.id.btn_noti_home);
        mBtnNoti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast toast = Toast.makeText(v.getContext(), "Noti", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
        mBtnSetting = mView.findViewById(R.id.btn_setting_home);
        mBtnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast toast = Toast.makeText(v.getContext(), "Setting", Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        // recycleView: List users
        mRcvUsers = mView.findViewById(R.id.recycler_users);
        mainActivity = (MainActivity) getActivity();
        mUsers = Arrays.asList(UserInfo.dummy);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mainActivity);
        mRcvUsers.setLayoutManager(linearLayoutManager);

        userAdapter = new UserHomeViewAdapter();
        userAdapter.setData(mUsers, new UserHomeViewAdapter.IClickCardUserListener() {
            @Override
            public void onClickUser(UserInfo user) {

            }
        });
        mRcvUsers.setAdapter(userAdapter);
        return mView;
    }

    void navigateToConnectScreen(Context context){
        Intent intent = new Intent(context, ConnectActivity.class);
        context.startActivity(intent);
    }
    void navigateToCallScreen(Context context){
        Intent intent = new Intent(context, WaitingActivity.class);
        context.startActivity(intent);
    }
}
