package com.example.fimae.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fimae.R;
import com.example.fimae.adapters.MessageAdapter;
import com.example.fimae.models.Message;
import com.example.fimae.models.Report;
import com.example.fimae.repository.ChatRepository;
import com.example.fimae.repository.ConnectRepo;
import com.example.fimae.service.TimerService;
import com.example.fimae.utils.TimerUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

public class ChatRandomActivity extends AppCompatActivity {
    private int TIME_CALL = 3 * 60;

    private TextView tvDescriptionCall;
    private ImageButton btnLike;
    // like
    private boolean isLiked = false;
    // Appbar
    private ImageButton btnClose;
    private ImageButton btnReport;
    private ImageView btnSend;
    private LinearLayout layoutTimer;
    EditText textInputEditText;
    private String to;
    private String chatId;

    TimerUtil timer;
    TextView tvTimer;
    MessageAdapter messageAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_random);

        if(getIntent() != null){
            to = getIntent().getStringExtra("to");
        }
        chatId = getIntent().getStringExtra("chatId");
        btnSend = findViewById(R.id.random_chat_btn_send);
        btnSend.setOnClickListener(v -> {
            // send message
            String message = textInputEditText.getText().toString();
            if(message.isEmpty()) return;
            ChatRepository.getRandomChatInstance().sendTextMessage(chatId, message)
                    .addOnCompleteListener(new OnCompleteListener<Message>() {
                        @Override
                        public void onComplete(@NonNull Task<Message> task) {
                            if(task.isSuccessful())
                            {

                            }
                            else
                            {
                                Toast.makeText(ChatRandomActivity.this, "Error: Your partner has left the chat.", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }
                    });
            textInputEditText.setText("");
        });

        Query query = ChatRepository.getRandomChatInstance().getRandomMessageQuery(chatId);
         messageAdapter = new MessageAdapter(query, this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        RecyclerView recyclerView = findViewById(R.id.random_chat_recycler_view);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(messageAdapter);

        messageAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                recyclerView.scrollToPosition(messageAdapter.getItemCount() - 1);
            }
        });

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
            timer.stop();
            onClose();
        });

        btnReport.setOnClickListener(v -> {
            // report

        });

        // timer ==================================================================
        layoutTimer = findViewById(R.id.layout_timer);

        //==========================================================================
        btnSend = findViewById(R.id.random_chat_btn_send);
        textInputEditText = findViewById(R.id.random_chat_edt_message);

        ProgressBar progressBar = findViewById(R.id.pbTimer);
        progressBar.setMax((int) (TIME_CALL* 1000L));
        tvTimer = findViewById(R.id.tv_time_connect);
        timer = new TimerUtil( TIME_CALL* 1000L,new TimerUtil.TimerUtilTask() {
            @Override
            public void onTick(long milliseconds) {
                progressBar.setProgress((int) milliseconds);
                tvTimer.setText(TimerUtil.formatMinutes(milliseconds));
            }

            @Override
            public void onFinish() {
                if(!isLiked) {
                    // neu chua like thi dung khi het thoi gian
                    timer.stop();
                    onClose();
                }
                else {
                    // neu like roi thi an di
                    layoutTimer.setVisibility(View.GONE);
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        timer.start();
    }

    private void onClose(){
        isLiked = false;
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        WaitingActivity.isCalled = false;
        timer.stop();
        if (messageAdapter != null) {
            messageAdapter.stopListening();
        }
        FirebaseFirestore.getInstance().collection("chats").document(chatId).delete().addOnCompleteListener(task -> {
            if(!task.isSuccessful()){
                task.getException().printStackTrace();
            }
        });
    }

    private void onLiked() {
        ChatRepository.getRandomChatInstance().likeFimaerInRandomChat(chatId).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    isLiked = true;
                    btnLike.setVisibility(View.GONE);
                    tvDescriptionCall.setText("Bây giờ chúng ta là bạn, thưởng thức cuộc trò chuyện không giới hạn");

                    timer.stop();

                    layoutTimer.setVisibility(View.GONE);
                } else {
                    Toast.makeText(ChatRandomActivity.this, "Đã xảy ra lỗi, vui lòng thử lại sau", Toast.LENGTH_SHORT).show();
                }
            }
        });
        if(ConnectRepo.getInstance().getUserRemote() != null){
            ChatRepository.getDefaultChatInstance().getOrCreateFriendConversation(ConnectRepo.getInstance().getUserRemote().getUid());
        }
    }
//== report ===============================================================================

//    private void setReportDescription() {
//        sReport = "";
//        if(isKhongThich.get()) sReport += "Không thích, ";
//        if(isQuayRoi.get()) sReport += "Quấy rối tình dục, ";
//        if(isBatHopPhap.get()) sReport += "Hoạt động bất hợp pháp, ";
//        if(isGianLan.get()) sReport += "Gian lận, ";
//        if(isAnhGia.get()) sReport += "Ảnh giả, ";
//        if(isBatNat.get()) sReport += "Bắt nạt, ";
//        if(isViThanhNien.get()) sReport += "Vị thành niên, ";
//        if(isKhac.get()) sReport += "Khác, ";
//        sReport += sMota;
//    }

}