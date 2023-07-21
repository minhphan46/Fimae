package com.example.fimae.activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fimae.R;
import com.example.fimae.adapters.SliderAdapter;
import com.example.fimae.models.Calls;
import com.example.fimae.models.Fimaers;
import com.example.fimae.repository.ConnectRepo;
import com.example.fimae.repository.FimaerRepository;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.functions.FirebaseFunctionsException;
import com.stringee.StringeeClient;
import com.stringee.call.StringeeCall;
import com.stringee.call.StringeeCall2;
import com.stringee.exception.StringeeError;
import com.stringee.listener.StringeeConnectionListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WaitingActivity extends AppCompatActivity {

    private String type; // type of connect
    private String tableName;

    private String chatId;
    private int timeDelayToConnect = 3000;

    private FrameLayout mBtnSpeedUp;
    // appbar ======================================================
    private ImageButton mBtnZoomOut;
    private ImageButton mBtnClose;
    private TextView mTvTitle;

    private int REQUEST_CODE = 9999;
    // slider ======================================================
    private ViewPager2 viewPager2;
    private List<Fimaers> fimaers;
    private Handler sliderHandler = new Handler();
    private int timeDelaySlider = 2000;

    // goi dien ====================================================
    public static boolean isCalled = false;
    private String remoteUserId;
    private TextView mTvStatusConnect;
    public static StringeeClient client;
    DatabaseReference databaseReference;

    ConnectRepo connectRepo = ConnectRepo.getInstance();
    // luu cuoc goi den = map
    // key = callID
    public static Map<String, StringeeCall> callMap = new HashMap<>();
    // video
    public static Map<String, StringeeCall2> call2Map = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting);
        Log.e("Waiting", "create");

        // receive data
        Intent intent = getIntent();
        type = intent.getStringExtra("type"); // two option: voice - video
        if(type.equals("chat")){
            tableName = ConnectRepo.table_chat_name;
        }
        else if(type.equals("voice")){
            tableName = ConnectRepo.table_call_voice_name;
        }
        else if(type.equals("video")){
            tableName = ConnectRepo.table_call_video_name;
        }
        connectRepo.addUserOnl(connectRepo.getUserLocal(),tableName);
        // appbar ========================================================
        mBtnZoomOut = findViewById(R.id.btn_zoom_out_waiting);
        mBtnZoomOut.setOnClickListener(v -> {
            // zoom out
        });

        mBtnClose = findViewById(R.id.btn_close_waiting);
        mBtnClose.setOnClickListener(v -> {
            closeScreen();
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
                sliderHandler.postDelayed(sliderRunable, timeDelaySlider); // slide duration 2 seconds
            }
        });

        // call function: connect to user ======================================================
        mTvStatusConnect = findViewById(R.id.tv_status_connect);
        databaseReference = FirebaseDatabase.getInstance().getReference();

        fetchData(tableName);

        if(ConnectRepo.getInstance().getUserLocal() != null){
            initStringeeConnection();
        }
        // btn speed up ======================================================================================
        mBtnSpeedUp = findViewById(R.id.btn_speed_up);
        mBtnSpeedUp.setOnClickListener(v -> {
            connectToRemoteUser();
        });
    }

    // close screen ==============================================================================
    private void closeScreen() {
        // delete user onl then finish
        ConnectRepo.getInstance().setUserRemote(null);
        if(client != null) {
            client.disconnect();
            client = null;
        }
        finish();
    }

    @Override
    protected void onDestroy() {
        if(ConnectRepo.getInstance().getUserLocal() != null){
            ConnectRepo.getInstance().deleteUserOnl(ConnectRepo.getInstance().getUserLocal(),tableName);
        }
        super.onDestroy();
    }

    // connect to ==============================================================================
    private void handlerConnect() {
        // delay 3s
        // if remote user is not null => connect
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 3s
                connectToRemoteUser();
            }
        }, timeDelayToConnect);
    }

    private void connectToRemoteUser() {
        if(remoteUserId != null){
            //ConnectRepo.getInstance().deleteUserOnl(ConnectRepo.getInstance().getUserRemote(), tableName);
            if(isCalled) return;
            isCalled = true;
            if(type.equals("chat")){
                navigateToChatScreen();
            }
            else if(type.equals("voice")){
                navigateToCallVoiceScreen();
            }
            else if(type.equals("video")){
                navigateToCallVideoScreen();
            }
        }
    }

    private void fetchData(String tableName) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        firestore.collection(tableName).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error!=null)
                {
                    Log.e("WaitingActivity", "Error getting calls", error);
                    return;
                }
                for (QueryDocumentSnapshot document : value) {
                    Calls call = document.toObject(Calls.class);
                    ArrayList<String> ParticipantIDs = call.getParticipantIDs();
                    String uid = ParticipantIDs.get(0);
                    String localUid =ConnectRepo.getInstance().getUserLocal().getUid();
                    if(uid.equals(localUid) || (tableName == ConnectRepo.table_chat_name && ParticipantIDs.contains(localUid)))
                    {
                        remoteUserId = call.getParticipantIDs().get(1);
                        handlerConnect();
                        ConnectRepo.getInstance().setUserRemoteById(remoteUserId);
                        DocumentReference documentRef = document.getReference();
                        Log.e("Waiting", "goi " + remoteUserId);
                        if(tableName != ConnectRepo.table_chat_name)
                        {
                            Log.i("Waiting", "delete doc");
                            documentRef.delete()
                                    .addOnSuccessListener(aVoid -> {
                                        // Document successfully deleted
                                    })
                                    .addOnFailureListener(e -> {
                                        // An error occurred while deleting the document
                                    });
                        }
                        else
                        {
                            chatId = document.getId();
                        }
                        // Delete the document
                    }
                }
            }
        });
    }

    private void showQueueNumber() {
        if(ConnectRepo.getInstance().getUserRemote() == null){
            showToast("Hàng đợi rỗng");
        }
        else {
            showToast("Remote user: " + remoteUserId);
        }
    }

    private void showToast(String value) {
        Toast.makeText(this, value, Toast.LENGTH_LONG).show();
    }

    // slider =====================================================================
    private void initViewPager2(){
        viewPager2 = findViewById(R.id.view_image_slider);

        fimaers = new ArrayList<Fimaers>(Fimaers.dummy);
        viewPager2.setAdapter(new SliderAdapter(fimaers, viewPager2));

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
        sliderHandler.postDelayed(sliderRunable, timeDelaySlider);
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
                    // set user remote
                    remoteUserId = stringeeCall.getFrom();
                    ConnectRepo.getInstance().setUserRemoteById(remoteUserId);
                    // navigate to call screen
                    isCalled = true;
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
                    // set user remote
                    remoteUserId = stringeeCall2.getFrom();
                    ConnectRepo.getInstance().setUserRemoteById(remoteUserId);
                    // navigate to call video screen
                    isCalled = true;
                    Intent intent = new Intent(WaitingActivity.this, CallVideoActivity.class);
                    intent.putExtra("callId", stringeeCall2.getCallId());
                    intent.putExtra("isIncomingCall", true);
                    startActivity(intent);
                });
            }

            @Override
            public void onConnectionError(StringeeClient stringeeClient, StringeeError stringeeError) {
                runOnUiThread(()->{
                    Log.e("TAG", "onConnectionError: " + stringeeError.getCode() + stringeeError.getMessage());
                    if(stringeeError.getCode() == 2 || stringeeError.getCode() == 6  || stringeeError.getCode() == 10)
                    {
                        Log.e("TOKEN", "start geting: ");
                        mTvStatusConnect.setText("Kết nối bị lỗi: " + stringeeError.getMessage());
                        FimaerRepository.getInstance().refreshToken().addOnCompleteListener(new OnCompleteListener<String>() {
                            @Override
                            public void onComplete(@NonNull Task<String> task) {
                                if(task.isSuccessful())
                                {
                                    Log.e("TOKEN", "onComplete: " + task.getResult());
                                    stringeeClient.connect(task.getResult());
                                }
                                else
                                {
                                    Exception e = task.getException();
                                    if (e instanceof FirebaseFunctionsException) {
                                        FirebaseFunctionsException ffe = (FirebaseFunctionsException) e;
                                        FirebaseFunctionsException.Code code = ffe.getCode();
                                        Object details = ffe.getDetails();
                                        Log.e("TOKEN", "onComplete: " + details.toString() );
                                    }
                                }
                            }
                        });
                    }
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

    private void navigateToChatScreen() {
        Intent intent = new Intent(this, ChatRandomActivity.class);
        intent.putExtra("to", remoteUserId);
        intent.putExtra("chatId", chatId );
        startActivity(intent);
    }

    private void navigateToCallVoiceScreen() {
        Intent intent = new Intent(this, CallActivity.class);
        intent.putExtra("to", remoteUserId);
        intent.putExtra("isIncomingCall", false);
        startActivity(intent);
    }

    private void navigateToCallVideoScreen() {
        Intent intent = new Intent(this, CallVideoActivity.class);
        intent.putExtra("to", remoteUserId);
        intent.putExtra("isIncomingCall", false);
        startActivity(intent);
    }
}
