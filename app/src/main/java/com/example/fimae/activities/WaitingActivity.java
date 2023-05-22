package com.example.fimae.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fimae.R;
import com.example.fimae.adapters.SliderAdapter;
import com.example.fimae.models.UserInfo;
import com.example.fimae.repository.ConnectRepo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.stringee.StringeeClient;
import com.stringee.call.StringeeCall;
import com.stringee.call.StringeeCall2;
import com.stringee.exception.StringeeError;
import com.stringee.listener.StringeeConnectionListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WaitingActivity extends AppCompatActivity {

    private FrameLayout mBtnSpeedUp;
    // appbar ======================================================
    private ImageButton mBtnZoomOut;
    private ImageButton mBtnClose;
    private TextView mTvTitle;
    // slider ======================================================
    private ViewPager2 viewPager2;
    private List<UserInfo> userInfos;
    private Handler sliderHandler = new Handler();
    private int timeDelay = 2000;

    // goi dien ====================================================
    private String remoteUserName;
    private TextView mTvStatusConnect;
    public static StringeeClient client;
    DatabaseReference databaseReference;
    private String type; // type of call
    // luu cuoc goi den = map
    // key = callID
    public static Map<String, StringeeCall> callMap = new HashMap<>();
    // video
    public static Map<String, StringeeCall2> call2Map = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting);
        // receive data
        Intent intent = getIntent();
        type = intent.getStringExtra("type"); // two option: voice - video
        // appbar ========================================================
        mBtnZoomOut = findViewById(R.id.btn_zoom_out_waiting);
        mBtnZoomOut.setOnClickListener(v -> {
            // zoom out
        });

        mBtnClose = findViewById(R.id.btn_close_waiting);
        mBtnClose.setOnClickListener(v -> {
            finish();
        });

        mTvTitle = findViewById(R.id.tv_title_waiting);
        if(type.equals("chat")){
            mTvTitle.setText("Nhắn tin");
        }
        else if(type.equals("voice")){
            mTvTitle.setText("Gọi điện");
        }
        else if(type.equals("video")){
            mTvTitle.setText("Gọi video");
        }
        // Slider ================================================================
        initViewPager2();
        setTransformer();

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                sliderHandler.removeCallbacks(sliderRunable);
                sliderHandler.postDelayed(sliderRunable, timeDelay); // slide duration 2 seconds
            }
        });

        // call function: connect to user ======================================================
        mTvStatusConnect = findViewById(R.id.tv_status_connect);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        if(type.equals("chat")){

        }
        else if(type.equals("voice")){
            fetchData(ConnectRepo.table_call_voice_name);
        }
        else if(type.equals("video")){
            fetchData(ConnectRepo.table_call_video_name);
        }

        if(ConnectRepo.getInstance().getUserLocal() != null){
            initStringeeConnection();
        }
        // btn speed up ======================================================================================
        mBtnSpeedUp = findViewById(R.id.btn_speed_up);
        mBtnSpeedUp.setOnClickListener(v -> {
            if(remoteUserName != null){
                if(type.equals("voice")){
                    ConnectRepo.getInstance().deleteUserOnl(ConnectRepo.getInstance().getUserRemote(), ConnectRepo.table_call_voice_name);
                    navigateToCallVoiceScreen();
                }
                else {
                    ConnectRepo.getInstance().deleteUserOnl(ConnectRepo.getInstance().getUserRemote(),ConnectRepo.table_call_video_name);
                    navigateToCallVideoScreen();
                }
            }
        });
    }

    // connect to ==============================================================================
    private void fetchData(String tableName) {
        databaseReference.child(tableName).addListenerForSingleValueEvent(
            new ValueEventListener(){
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    // get list all user onl
                    ArrayList<UserInfo> listUsersOnline = new ArrayList<>();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        UserInfo user = dataSnapshot.getValue(UserInfo.class);
                        if(user != null) {
                            Log.d("TAG", user.toString());
                            listUsersOnline.add(user);
                        }
                        else break;
                    }
                    // assign to list local
                    ConnectRepo.getInstance().listUsersOnline = listUsersOnline;
                    ConnectRepo.getInstance().checkUser(tableName);
                    connectToCall();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // handle error
                }
            }
        );
    }

    private void connectToCall() {
        if(ConnectRepo.getInstance().getUserRemote() == null){
            showToast("Hàng đợi rỗng");
        }
        else {
            remoteUserName = ConnectRepo.getInstance().getUserRemote().getName();
            showToast("Remote user: " + remoteUserName);
        }
    }

    private void showToast(String value) {
        Toast.makeText(this, value, Toast.LENGTH_LONG).show();
    }

    // slider =====================================================================
    private void initViewPager2(){
        viewPager2 = findViewById(R.id.view_image_slider);

        userInfos = new ArrayList<UserInfo>(Arrays.asList(UserInfo.dummy));
        viewPager2.setAdapter(new SliderAdapter(userInfos, viewPager2));

        viewPager2.setClipToPadding(false);
        viewPager2.setClipChildren(false);
        viewPager2.setOffscreenPageLimit(3);
        viewPager2.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
    }

    private void setTransformer(){
        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(10));
        compositePageTransformer.addTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                float r = 1 - Math.abs(position);
                float size = 0.85f + r * 0.15f;
                page.setScaleX(size);
                page.setScaleY(size);
            }
        });

        viewPager2.setPageTransformer(compositePageTransformer);
    }

    private Runnable sliderRunable = new Runnable() {
        @Override
        public void run() {
            viewPager2.setCurrentItem(viewPager2.getCurrentItem() + 1);
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        sliderHandler.removeCallbacks(sliderRunable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sliderHandler.postDelayed(sliderRunable, timeDelay);
    }

    // init call ==============================================================================
    private void initStringeeConnection(){
        client = new StringeeClient(this);
        client.setConnectionListener(new StringeeConnectionListener() {
            @Override
            public void onConnectionConnected(StringeeClient stringeeClient, boolean b) {
                runOnUiThread(()->{
                    mTvStatusConnect.setText("Bạn là " + stringeeClient.getUserId());
                });
            }

            @Override
            public void onConnectionDisconnected(StringeeClient stringeeClient, boolean b) {
                runOnUiThread(()->{
                    mTvStatusConnect.setText("Mất kết nối");
                });
            }

            @Override
            public void onIncomingCall(StringeeCall stringeeCall) {
                runOnUiThread(()->{
                    callMap.put(stringeeCall.getCallId(), stringeeCall);
                    Intent intent = new Intent(WaitingActivity.this, CallActivity.class);
                    intent.putExtra("callId", stringeeCall.getCallId());
                    intent.putExtra("isIncomingCall", true);
                    startActivity(intent);
                });
            }

            @Override
            public void onIncomingCall2(StringeeCall2 stringeeCall2) {
                runOnUiThread(()->{
                    call2Map.put(stringeeCall2.getCallId(), stringeeCall2);
                    Intent intent = new Intent(WaitingActivity.this, CallVideoActivity.class);
                    intent.putExtra("callId", stringeeCall2.getCallId());
                    intent.putExtra("isIncomingCall", true);
                    startActivity(intent);
                });
            }

            @Override
            public void onConnectionError(StringeeClient stringeeClient, StringeeError stringeeError) {
                runOnUiThread(()->{
                    mTvStatusConnect.setText("Kết nối bị lỗi: " + stringeeError.getMessage());
                });
            }

            @Override
            public void onRequestNewToken(StringeeClient stringeeClient) {

            }

            @Override
            public void onCustomMessage(String s, JSONObject jsonObject) {

            }

            @Override
            public void onTopicMessage(String s, JSONObject jsonObject) {

            }
        });
        client.connect(ConnectRepo.getInstance().getUserLocal().getToken());
    }

    private void navigateToCallVoiceScreen() {
        Intent intent = new Intent(this, CallActivity.class);
        intent.putExtra("to", remoteUserName);
        intent.putExtra("isIncomingCall", false);
        startActivity(intent);
    }

    private void navigateToCallVideoScreen() {
        Intent intent = new Intent(this, CallVideoActivity.class);
        intent.putExtra("to", remoteUserName);
        intent.putExtra("isIncomingCall", false);
        startActivity(intent);
    }
}