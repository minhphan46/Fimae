package com.example.fimae.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toolbar;

import com.example.fimae.R;
import com.stringee.StringeeClient;
import com.stringee.call.StringeeCall;
import com.stringee.call.StringeeCall2;
import com.stringee.exception.StringeeError;
import com.stringee.listener.StringeeConnectionListener;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ConnectActivity extends AppCompatActivity {
    // list token
    private String minh = "eyJjdHkiOiJzdHJpbmdlZS1hcGk7dj0xIiwidHlwIjoiSldUIiwiYWxnIjoiSFMyNTYifQ.eyJqdGkiOiJTSy4wLnM1OFRaMnBJbkIwMFdWMlZmTlQ1RXRmU2xLQ2g3cy0xNjgyODk0NzE0IiwiaXNzIjoiU0suMC5zNThUWjJwSW5CMDBXVjJWZk5UNUV0ZlNsS0NoN3MiLCJleHAiOjE2ODU0ODY3MTQsInVzZXJJZCI6Im1pbmgifQ.rtlgkQhsZMhSUFnxfBk0zSeg0BPHRHHh4SQ54A1GTm8";
    private String hao = "eyJjdHkiOiJzdHJpbmdlZS1hcGk7dj0xIiwidHlwIjoiSldUIiwiYWxnIjoiSFMyNTYifQ.eyJqdGkiOiJTSy4wLnM1OFRaMnBJbkIwMFdWMlZmTlQ1RXRmU2xLQ2g3cy0xNjgyODk0NzI5IiwiaXNzIjoiU0suMC5zNThUWjJwSW5CMDBXVjJWZk5UNUV0ZlNsS0NoN3MiLCJleHAiOjE2ODU0ODY3MjksInVzZXJJZCI6ImhhbyJ9.CANIGuM0lLjN3n1A0TtNEHLBWQven8VP_i0DUB0t-8k";
    private String anh = "eyJjdHkiOiJzdHJpbmdlZS1hcGk7dj0xIiwidHlwIjoiSldUIiwiYWxnIjoiSFMyNTYifQ.eyJqdGkiOiJTSy4wLnM1OFRaMnBJbkIwMFdWMlZmTlQ1RXRmU2xLQ2g3cy0xNjgyODk0NzUxIiwiaXNzIjoiU0suMC5zNThUWjJwSW5CMDBXVjJWZk5UNUV0ZlNsS0NoN3MiLCJleHAiOjE2ODU0ODY3NTEsInVzZXJJZCI6ImFuaCJ9.w-uJWfmXAmKn_y9nyTUGYrqwe6GoTyxEqvLsF4qvdGs";
    private String hien = "eyJjdHkiOiJzdHJpbmdlZS1hcGk7dj0xIiwidHlwIjoiSldUIiwiYWxnIjoiSFMyNTYifQ.eyJqdGkiOiJTSy4wLnM1OFRaMnBJbkIwMFdWMlZmTlQ1RXRmU2xLQ2g3cy0xNjgyODk0NzY3IiwiaXNzIjoiU0suMC5zNThUWjJwSW5CMDBXVjJWZk5UNUV0ZlNsS0NoN3MiLCJleHAiOjE2ODU0ODY3NjcsInVzZXJJZCI6ImhpZW4ifQ.OSSprxugshgZDhSux0vG3XOPa5ptsSP_r1qaf-DvtXk";
    // đổi tên token vào đây rồi cài app
    private String token = minh;

    public void setToken(String token) {
        this.token = token;
        initStringeeConnection();
    }

    private EditText etTo;
    private TextView tvStatus;
    private Button btnCall;
    private Button btnCallVideo;

    private Button btnMinh;
    private Button btnHao;
    private Button btnAnh;
    private Button btnHien;

    public static StringeeClient client;
    // luu cuoc goi den = map
    // key = callID
    public static Map<String, StringeeCall> callMap = new HashMap<>();
    // video
    public static Map<String, StringeeCall2> call2Map = new HashMap<>();

    private ImageButton mBtnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);

        etTo = findViewById(R.id.et_to);
        mBtnBack = findViewById(R.id.btn_back_to_home);
        tvStatus = findViewById(R.id.tv_status);
        btnCall = findViewById(R.id.btn_call);
        btnCallVideo = findViewById(R.id.btn_call_video);
        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(etTo.getText() == null || etTo.getText().toString().trim().length() == 0){
                    return;
                }
                Intent intent = new Intent(view.getContext(), CallActivity.class);
                intent.putExtra("to", etTo.getText().toString().trim());
                intent.putExtra("isIncomingCall", false);
                startActivity(intent);
            }
        });

        btnCallVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(etTo.getText() == null || etTo.getText().toString().trim().length() == 0){
                    return;
                }
                Intent intent = new Intent(view.getContext(), CallVideoActivity.class);
                intent.putExtra("to", etTo.getText().toString().trim());
                intent.putExtra("isIncomingCall", false);
                startActivity(intent);
            }
        });

        mBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // set user token
        btnMinh = findViewById(R.id.btn_minh);
        btnMinh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setToken(minh);
            }
        });
        btnHao = findViewById(R.id.btn_hao);
        btnHao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setToken(hao);
            }
        });
        btnAnh = findViewById(R.id.btn_anh);
        btnAnh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setToken(anh);
            }
        });
        btnHien = findViewById(R.id.btn_hien);
        btnHien.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setToken(hien);
            }
        });

        //initStringeeConnection();
    }

    private void initStringeeConnection(){
        client = new StringeeClient(this);
        client.setConnectionListener(new StringeeConnectionListener() {
            @Override
            public void onConnectionConnected(StringeeClient stringeeClient, boolean b) {
                runOnUiThread(()->{
                    tvStatus.setText("Connected as " + stringeeClient.getUserId());
                });
            }

            @Override
            public void onConnectionDisconnected(StringeeClient stringeeClient, boolean b) {
                runOnUiThread(()->{
                    tvStatus.setText("Disconnected");
                });
            }

            @Override
            public void onIncomingCall(StringeeCall stringeeCall) {
                runOnUiThread(()->{
                    callMap.put(stringeeCall.getCallId(), stringeeCall);
                    Intent intent = new Intent(ConnectActivity.this, CallActivity.class);
                    intent.putExtra("callId", stringeeCall.getCallId());
                    intent.putExtra("isIncomingCall", true);
                    startActivity(intent);
                });
            }

            @Override
            public void onIncomingCall2(StringeeCall2 stringeeCall2) {
                runOnUiThread(()->{
                    call2Map.put(stringeeCall2.getCallId(), stringeeCall2);
                    Intent intent = new Intent(ConnectActivity.this, CallVideoActivity.class);
                    intent.putExtra("callId", stringeeCall2.getCallId());
                    intent.putExtra("isIncomingCall", true);
                    startActivity(intent);
                });
            }

            @Override
            public void onConnectionError(StringeeClient stringeeClient, StringeeError stringeeError) {
                runOnUiThread(()->{
                    tvStatus.setText("Connect error: " + stringeeError.getMessage());
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
        client.connect(token);
    }
}