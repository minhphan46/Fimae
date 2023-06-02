package com.example.fimae.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fimae.R;
import com.example.fimae.repository.ConnectRepo;
import com.example.fimae.service.TimerService;
import com.squareup.picasso.Picasso;
import com.stringee.call.StringeeCall;
import com.stringee.common.StringeeAudioManager;
import com.stringee.listener.StatusListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CallActivity extends AppCompatActivity {

    private int TIME_CALL = 5 * 60;

    private CircleImageView mImageLocal;
    private CircleImageView mImageRemote;

    private TextView tvStatus;
    private TextView tvDescriptionCall;
    private FrameLayout frmTextLike;
    private View vIncoming;
    private View vOption;
    private ImageButton btnSpeaker;
    private ImageButton btnMute;
    private ImageButton btnAnswer;
    private ImageButton btnReject;
    private ImageButton btnEnd;

    private StringeeCall call;

    private boolean isInComingCall = false;
    private String to;
    private String callId;

    private StringeeCall.SignalingState mSignalingState;
    private StringeeCall.MediaState mMediaState;

    // audio
    private StringeeAudioManager audioManager;

    // check trang thai speaker and mic
    private boolean isSpeaker = false;
    private boolean isMicOn = true;

    // like
    private boolean isLiked = false;

    // Appbar
    private ImageButton btnClose;
    private ImageButton btnReport;
    private LinearLayout layoutTimer;
    private TimerService timerService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);

        // set image users
        mImageLocal = findViewById(R.id.img_avatar_local);
        mImageRemote = findViewById(R.id.img_avatar_remote);

        if(ConnectRepo.getInstance().getUserLocal() != null){
            Picasso.get().load(ConnectRepo.getInstance().getUserLocal().getAvatarUrl()).placeholder(R.drawable.ic_default_avatar).into(mImageLocal);
        }
        if(ConnectRepo.getInstance().getUserRemote() != null){
            Picasso.get().load(ConnectRepo.getInstance().getUserRemote().getAvatarUrl()).placeholder(R.drawable.ic_default_avatar).into(mImageRemote);
        }
        // ======================================
        tvStatus = findViewById(R.id.tv_status);
        vIncoming = findViewById(R.id.v_incoming);
        vOption = findViewById(R.id.v_option);
        btnAnswer = findViewById(R.id.btn_answer);
        btnSpeaker = findViewById(R.id.btn_speaker);
        btnMute = findViewById(R.id.btn_mute);
        btnReject = findViewById(R.id.btn_reject);
        btnEnd = findViewById(R.id.btn_end);
        tvDescriptionCall = findViewById(R.id.tv_des_call);
        frmTextLike = findViewById(R.id.frame_text_like);

        btnSpeaker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                runOnUiThread(() -> {
                    if(audioManager != null) {
                        audioManager.setSpeakerphoneOn(!isSpeaker);
                        isSpeaker = !isSpeaker;
                        btnSpeaker.setBackgroundResource(isSpeaker? R.drawable.background_btn_speaker_on : R.drawable.background_btn_speaker_off);
                    }
                });
            }
        });
        btnMute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                runOnUiThread(() -> {
                    if(call != null){
                        call.mute(isMicOn);
                        isMicOn = !isMicOn;
                        btnMute.setBackgroundResource(isMicOn? R.drawable.background_btn_mic_on : R.drawable.background_btn_mic_off);
                    }
                });
            }
        });
        btnAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                runOnUiThread(() -> {
                    if(call != null){
                        call.answer(new StatusListener() {
                            @Override
                            public void onSuccess() {

                            }
                        });
                        vIncoming.setVisibility(View.GONE);
                        vOption.setVisibility(View.VISIBLE);
                        btnEnd.setVisibility(View.VISIBLE);
                        frmTextLike.setVisibility(View.VISIBLE);
                    }
                });
            }
        });
        btnReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                runOnUiThread(() -> {
                    if(call != null){
                        call.reject(new StatusListener() {
                            @Override
                            public void onSuccess() {

                            }
                        });
                        onFinish();
                    }
                });
            }
        });
        btnEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isLiked) {
                    // neu chua like
                    onLiked();
                    // delete timer
                    timerService.onDestroy();
                    layoutTimer.setVisibility(View.GONE);
                }
                else {
                    // cup may
                    timerService.onDestroy();
                    onEndCall();
                }
            }
        });

        if(getIntent() != null){
            isInComingCall = getIntent().getBooleanExtra("isIncomingCall", false);
            to = getIntent().getStringExtra("to");
            // duoc goi
            callId = getIntent().getStringExtra("callId");
        }

        // kiem tra dang goi den
        vIncoming.setVisibility(isInComingCall? View.VISIBLE : View.GONE);
        frmTextLike.setVisibility(isInComingCall? View.GONE : View.VISIBLE);
        vOption.setVisibility(isInComingCall? View.GONE: View.VISIBLE);
        btnEnd.setVisibility(isInComingCall? View.GONE: View.VISIBLE);

        // list permission
        List<String> listPermission = new ArrayList<>();
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            // add permisson
            listPermission.add(Manifest.permission.RECORD_AUDIO);
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // add permisson
                listPermission.add(Manifest.permission.BLUETOOTH_CONNECT);
            }
        }
        if(listPermission.size() > 0){
            String[] permissions = new String[listPermission.size()];
            for(int i = 0; i < listPermission.size(); i++){
                permissions[i] = listPermission.get(i);
            }
            ActivityCompat.requestPermissions(this, permissions, 0);
            return;
        }
        initCall();
        // appbar ==================================================================
        btnClose = findViewById(R.id.btn_close_appbar);
        btnReport = findViewById(R.id.btn_report_appbar);
        btnClose.setBackgroundResource(R.drawable.ic_logout);

        btnClose.setOnClickListener(v -> {
            // cup may
            timerService.onDestroy();
            onEndCall();
        });

        btnReport.setOnClickListener(v -> {
            // report
        });

        // timer ==================================================================
        layoutTimer = findViewById(R.id.layout_timer);

        timerService = new TimerService(
                TIME_CALL,
                findViewById(R.id.pbTimer),
                findViewById(R.id.tv_time_connect),
                new TimerService.IOnTimeUp() {
                    @Override
                    public void onTimeUp() {
                        if(!isLiked) {
                            // neu chua like thi dung khi het thoi gian
                            onEndCall();
                            timerService.onDestroy();
                        }
                        else {
                            // neu like roi thi an di
                            layoutTimer.setVisibility(View.GONE);
                        }
                    }
                }
        );
        timerService.setTimeInit();
        timerService.startTimerSetUp();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timerService.onDestroy();
    }

    private void onLiked() {
        // doi background button call
        // an di frame_text_like
        // doi text tv_des_call
        // doi bien like
        isLiked = true;
        btnEnd.setBackgroundResource(R.drawable.background_btn_call);
        frmTextLike.setVisibility(View.GONE);
        tvDescriptionCall.setText("Bây giờ chúng ta là bạn, thưởng thức cuộc trò chuyện không giới hạn");
    }
    // call =======================================================================

    private void onEndCall(){
        if(call != null){
            call.hangup(new StatusListener(){
                @Override
                public void onSuccess() {
                }
            });
            onFinish();
        }
    }

    private void onFinish() {
        audioManager.stop();
        WaitingActivity.isCalled = false;
        finish();
    }

    // lay token de thuc hien cuoc goi
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // lay quyen audio
        boolean isGranted = false;
        if(grantResults.length > 0){
            for(int grantResult : grantResults){
                // check trang thai quyen nguoi dung cho phep
                if(grantResult != PackageManager.PERMISSION_GRANTED){
                    isGranted = false;
                    break;
                }else{
                    isGranted = true;
                }
            }
        }
        // check nguoi dung cap quyen chua
        if(requestCode == 0){
            if(!isGranted){
                onFinish();
            } else {
                initCall();
            }
        }
    }

    private void initCall(){
        if(isInComingCall){
            // cuoc goi den
            call = WaitingActivity.callMap.get(callId);
            if( call == null){
                onFinish();
                return;
            }
        }else{
            // tao cuoc goi moi
            call = new StringeeCall(WaitingActivity.client, WaitingActivity.client.getUserId(), to);
        }

        // theo doi trang thai cuoc goi
        call.setCallListener(new StringeeCall.StringeeCallListener() {
            @Override
            public void onSignalingStateChange(StringeeCall stringeeCall, StringeeCall.SignalingState signalingState, String s, int i, String s1) {
                // trang thai dieu huong cuoc goi
                // khi nao bat dau, ket thuc
                runOnUiThread(()->{
                    mSignalingState = signalingState;
                    switch (signalingState) {
                        case CALLING:
                            tvStatus.setText("Đang gọi");
                            break;
                        case RINGING:
                            tvStatus.setText("Đang đổ chuông");
                            break;
                        case ANSWERED:
                            tvStatus.setText("Đang trả lời");
                            // cuoc goi bat dau
                            if(mMediaState == StringeeCall.MediaState.CONNECTED){
                                tvStatus.setText("");
                            }
                            break;
                        case BUSY:
                            tvStatus.setText("Máy bận");
                            onFinish();
                            break;
                        case ENDED:
                            tvStatus.setText("Kết thúc");
                            onFinish();
                            break;

                    }
                });
            }

            @Override
            public void onError(StringeeCall stringeeCall, int i, String s) {
                // cuoc goi bi loi
                runOnUiThread(()->{
                    tvStatus.setText("Lỗi đường truyền");
                    onFinish();
                });
            }

            @Override
            public void onHandledOnAnotherDevice(StringeeCall stringeeCall, StringeeCall.SignalingState signalingState, String s) {

            }

            @Override
            public void onMediaStateChange(StringeeCall stringeeCall, StringeeCall.MediaState mediaState) {
                // khi nao co media connected
                runOnUiThread(()->{
                    mMediaState = mediaState;
                    if(mediaState == StringeeCall.MediaState.CONNECTED){
                        if(mSignalingState == StringeeCall.SignalingState.ANSWERED){
                            tvStatus.setText("");
                        }
                    }else{
                        // mat ket noi
                        tvStatus.setText("Đang kết nối lại");
                    }
                });
            }

            @Override
            public void onLocalStream(StringeeCall stringeeCall) {

            }

            @Override
            public void onRemoteStream(StringeeCall stringeeCall) {

            }

            @Override
            public void onCallInfo(StringeeCall stringeeCall, JSONObject jsonObject) {

            }
        });

        // manage audio
        audioManager = new StringeeAudioManager(this);
        audioManager.start((audioDevice, set) -> {
            // start audio
        });

        audioManager.setSpeakerphoneOn(false);
        // khoi tao cuoc goi
        if(isInComingCall){
            // do chuong goi
            call.ringing(new StatusListener() {
                @Override
                public void onSuccess() {

                }
            });
        }else{
            call.makeCall(new StatusListener() {
                @Override
                public void onSuccess() {

                }
            });
        }
    }
}