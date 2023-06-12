package com.example.fimae;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.fimae.activities.WaitingActivity;
import com.example.fimae.service.TimerService;

public class ChatRandomActivity extends AppCompatActivity {
    private int TIME_CALL = 3 * 60;

    private TextView tvDescriptionCall;
    private ImageButton btnLike;
    // like
    private boolean isLiked = false;
    // Appbar
    private ImageButton btnClose;
    private ImageButton btnReport;
    private LinearLayout layoutTimer;
    private TimerService timerService;
    private String to;

    private String chatId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_random);

        if(getIntent() != null){
            to = getIntent().getStringExtra("to");
            chatId = getIntent().getStringExtra("chatId");
        }
        // like
        btnLike = findViewById(R.id.btn_chat_like);
        btnLike.setOnClickListener(v -> {
            onLiked();
        });

        // appbar ==================================================================
        btnClose = findViewById(R.id.btn_close_appbar);
        btnReport = findViewById(R.id.btn_report_appbar);
        btnClose.setBackgroundResource(R.drawable.ic_logout);
        tvDescriptionCall = findViewById(R.id.tv_des_call);

        btnClose.setOnClickListener(v -> {
            // cup may
            timerService.onDestroy();
            onClose();
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
                            timerService.onDestroy();
                            onClose();
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

    private void onClose(){
        isLiked = false;
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        WaitingActivity.isCalled = false;
        timerService.onDestroy();
    }

    private void onLiked() {
        isLiked = true;
        btnLike.setVisibility(View.GONE);
        tvDescriptionCall.setText("Bây giờ chúng ta là bạn, thưởng thức cuộc trò chuyện không giới hạn");
        timerService.onDestroy();

        // delete timer
        timerService.onDestroy();
        layoutTimer.setVisibility(View.GONE);
        // gửi tin nhắn qua bên kia là đã like
        //sendMessageToRemote("Bên kia đã like rồi nha");
    }

}