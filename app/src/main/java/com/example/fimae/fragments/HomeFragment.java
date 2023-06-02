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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fimae.R;
import com.example.fimae.activities.ConnectActivity;
import com.example.fimae.activities.HomeActivity;
import com.example.fimae.activities.WaitingActivity;
import com.example.fimae.adapters.UserHomeViewAdapter;
import com.example.fimae.models.Fimaers;
import com.example.fimae.repository.ConnectRepo;

import java.util.List;

public class HomeFragment extends Fragment  {

    // when click call button
    // navigate to waiting screen ( with par to distinguish screen type)
    // delay 5s
    // if dont have user remote => wait
    // if you have user
    // navigate to call screen

    private View mView;
    private RecyclerView mRcvUsers;
    private HomeActivity homeActivity;
    private UserHomeViewAdapter userAdapter;
    private List<Fimaers> mUsers;

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
                navigateToWaitingScreen(getContext(), "chat");
            }
        });
        mBtnCallVoice = (LinearLayout) mView.findViewById(R.id.btn_call_home);
        mBtnCallVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToWaitingScreen(getContext(), "voice");
            }
        });
        mBtnCallVideo = (LinearLayout) mView.findViewById(R.id.btn_call_video_home);
        mBtnCallVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToWaitingScreen(getContext(), "video");
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
        homeActivity = (HomeActivity) getActivity();
        mUsers = Fimaers.dummy;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(homeActivity);
        mRcvUsers.setLayoutManager(linearLayoutManager);

        userAdapter = new UserHomeViewAdapter();
        userAdapter.setData(mUsers, new UserHomeViewAdapter.IClickCardUserListener() {
            @Override
            public void onClickUser(Fimaers user) {
                ConnectRepo.getInstance().setUserLocal(user);
                showToast("You are " + user.getFirstName());
            }
        });
        mRcvUsers.setAdapter(userAdapter);
        return mView;
    }

    private void showToast(String value) {
        Toast.makeText(this.getContext(), value, Toast.LENGTH_LONG).show();
    }

    private void navigateToConnectScreen(Context context){
        Intent intent = new Intent(context, ConnectActivity.class);
        context.startActivity(intent);
    }

    private void navigateToWaitingScreen(Context context, String typeCall){
        Intent intent = new Intent(context, WaitingActivity.class);
        intent.putExtra("type", typeCall);
        context.startActivity(intent);
    }
}
