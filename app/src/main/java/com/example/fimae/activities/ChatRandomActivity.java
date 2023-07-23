package com.example.fimae.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fimae.R;
import com.example.fimae.activities.WaitingActivity;
import com.example.fimae.models.Report;
import com.example.fimae.repository.ConnectRepo;
import com.example.fimae.service.TimerService;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
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
    private TimerService timerService;
    EditText textInputEditText;
    private String to;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_random);

        if(getIntent() != null){
            to = getIntent().getStringExtra("to");
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
            showReport();
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
        //==========================================================================
        btnSend = findViewById(R.id.random_chat_btn_send);
        textInputEditText = findViewById(R.id.random_chat_edt_message);

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
//== report ===============================================================================
    AppCompatButton mBtnKhongThich;
    AppCompatButton mBtnQuayRoi;
    AppCompatButton mBtnBatHopPhap;
    AppCompatButton mBtnGianLan;
    AppCompatButton mBtnAnhGia;
    AppCompatButton mBtnBatNat;
    AppCompatButton mBtnViThanhNien;
    AppCompatButton mBtnKhac;
    EditText mEdtMoTa;
    AppCompatButton mBtnSend;
    AtomicBoolean isKhongThich = new AtomicBoolean(false);
    AtomicBoolean isQuayRoi = new AtomicBoolean(false);
    AtomicBoolean isBatHopPhap = new AtomicBoolean(false);
    AtomicBoolean isGianLan = new AtomicBoolean(false);
    AtomicBoolean isAnhGia = new AtomicBoolean(false);
    AtomicBoolean isBatNat = new AtomicBoolean(false);
    AtomicBoolean isViThanhNien = new AtomicBoolean(false);
    AtomicBoolean isKhac = new AtomicBoolean(false);
    AtomicBoolean isCanSend = new AtomicBoolean(false);
    String sMota = "";
    String sReport = "";
    private void showReport() {
        // when click report button
        View dialogSetting = getLayoutInflater().inflate(R.layout.bottom_sheet_report, null);

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(dialogSetting);
        bottomSheetDialog.show();
        // extended bottom sheet
        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from((View)dialogSetting.getParent());
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        // set component
        mBtnKhongThich = dialogSetting.findViewById(R.id.btn_khong_thich);
        mBtnQuayRoi = dialogSetting.findViewById(R.id.btn_quay_roi);
        mBtnBatHopPhap = dialogSetting.findViewById(R.id.btn_bat_hop_phap);
        mBtnGianLan = dialogSetting.findViewById(R.id.btn_gian_lan);
        mBtnAnhGia = dialogSetting.findViewById(R.id.btn_anh_gia);
        mBtnBatNat = dialogSetting.findViewById(R.id.btn_bat_nat);
        mBtnViThanhNien = dialogSetting.findViewById(R.id.btn_vi_thanh_nien);
        mBtnKhac = dialogSetting.findViewById(R.id.btn_khac);
        mEdtMoTa = dialogSetting.findViewById(R.id.edt_mo_ta);
        mBtnSend = dialogSetting.findViewById(R.id.btn_send);

        isKhongThich = new AtomicBoolean(false);
        isQuayRoi = new AtomicBoolean(false);
        isBatHopPhap = new AtomicBoolean(false);
        isGianLan = new AtomicBoolean(false);
        isAnhGia = new AtomicBoolean(false);
        isBatNat = new AtomicBoolean(false);
        isViThanhNien = new AtomicBoolean(false);
        isKhac = new AtomicBoolean(false);
        isCanSend = new AtomicBoolean(false);

        mBtnKhongThich.setOnClickListener(v -> {
            isKhongThich.set(!isKhongThich.get());
            if(isKhongThich.get()) {
                setButtonColor(mBtnKhongThich, R.color.background_button_dark_2, R.color.white);
            } else {
                setButtonColor(mBtnKhongThich, R.color.background_button_2, R.color.text_primary);
            }
            checkCanSend();
        });
        mBtnQuayRoi.setOnClickListener(v -> {
            isQuayRoi.set(!isQuayRoi.get());
            if(isQuayRoi.get()) {
                setButtonColor(mBtnQuayRoi, R.color.background_button_dark_2, R.color.white);
            } else {
                setButtonColor(mBtnQuayRoi, R.color.background_button_2, R.color.text_primary);
            }
            checkCanSend();
        });
        mBtnBatHopPhap.setOnClickListener(v -> {
            isBatHopPhap.set(!isBatHopPhap.get());
            if(isBatHopPhap.get()) {
                setButtonColor(mBtnBatHopPhap, R.color.background_button_dark_2, R.color.white);
            } else {
                setButtonColor(mBtnBatHopPhap, R.color.background_button_2, R.color.text_primary);
            }
            checkCanSend();
        });
        mBtnGianLan.setOnClickListener(v -> {
            isGianLan.set(!isGianLan.get());
            if(isGianLan.get()) {
                setButtonColor(mBtnGianLan, R.color.background_button_dark_2, R.color.white);
            } else {
                setButtonColor(mBtnGianLan, R.color.background_button_2, R.color.text_primary);
            }
            checkCanSend();
        });
        mBtnAnhGia.setOnClickListener(v -> {
            isAnhGia.set(!isAnhGia.get());
            if(isAnhGia.get()) {
                setButtonColor(mBtnAnhGia, R.color.background_button_dark_2, R.color.white);
            } else {
                setButtonColor(mBtnAnhGia, R.color.background_button_2, R.color.text_primary);
            }
            checkCanSend();
        });
        mBtnBatNat.setOnClickListener(v -> {
            isBatNat.set(!isBatNat.get());
            if(isBatNat.get()) {
                setButtonColor(mBtnBatNat, R.color.background_button_dark_2, R.color.white);
            } else {
                setButtonColor(mBtnBatNat, R.color.background_button_2, R.color.text_primary);
            }
            checkCanSend();
        });
        mBtnViThanhNien.setOnClickListener(v -> {
            isViThanhNien.set(!isViThanhNien.get());
            if(isViThanhNien.get()) {
                setButtonColor(mBtnViThanhNien, R.color.background_button_dark_2, R.color.white);
            } else {
                setButtonColor(mBtnViThanhNien, R.color.background_button_2, R.color.text_primary);
            }
            checkCanSend();
        });
        // neu chua chon khac thi an di
        mEdtMoTa.setVisibility(View.GONE);
        mBtnKhac.setOnClickListener(v -> {
            isKhac.set(!isKhac.get());
            sMota = "";
            if(isKhac.get()) {
                setButtonColor(mBtnKhac, R.color.background_button_dark_2, R.color.white);
                mEdtMoTa.setVisibility(View.VISIBLE);
            } else {
                mEdtMoTa.setVisibility(View.GONE);
                mEdtMoTa.setText("");
                setButtonColor(mBtnKhac, R.color.background_button_2, R.color.text_primary);
            }
            checkCanSend();
        });
        mBtnSend.setOnClickListener(v -> {
            sMota = mEdtMoTa.getText().toString();
            setReportDescription();
            Toast.makeText(this, sReport, Toast.LENGTH_LONG).show();
            createNewReport();
            bottomSheetDialog.dismiss();
        });
    }

    private void setReportDescription() {
        sReport = "";
        if(isKhongThich.get()) sReport += "Không thích, ";
        if(isQuayRoi.get()) sReport += "Quấy rối tình dục, ";
        if(isBatHopPhap.get()) sReport += "Hoạt động bất hợp pháp, ";
        if(isGianLan.get()) sReport += "Gian lận, ";
        if(isAnhGia.get()) sReport += "Ảnh giả, ";
        if(isBatNat.get()) sReport += "Bắt nạt, ";
        if(isViThanhNien.get()) sReport += "Vị thành niên, ";
        if(isKhac.get()) sReport += "Khác, ";
        sReport += sMota;
    }

    private void checkCanSend() {
        isCanSend.set(isKhongThich.get() || isQuayRoi.get() || isBatHopPhap.get() || isGianLan.get() || isAnhGia.get() || isBatNat.get() || isViThanhNien.get() || isKhac.get());
        if(isCanSend.get()){
            mBtnSend.setClickable(true);
            setButtonColor(mBtnSend, R.color.primary_2, R.color.white);
        } else {
            mBtnSend.setClickable(false);
            setButtonColor(mBtnSend, R.color.text_tertiary, R.color.white);
        }
    }

    private void createNewReport() {
        Report report = new Report("1",
                ConnectRepo.getInstance().getUserRemote().getUid(),
                ConnectRepo.getInstance().getUserLocal().getUid(),
                new Date(),
                sReport);

        CollectionReference dbReport = FirebaseFirestore.getInstance().collection("REPORTS");
        dbReport.add(report).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                // update ID report
                report.setUid(documentReference.getId());
                dbReport.document(report.getUid()).set(report);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ChatRandomActivity.this, "Fail to add report \n" + e, Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void setButtonColor(AppCompatButton btn, int colorButton, int colorText) {
        btn.setBackgroundTintList(this.getResources().getColorStateList(colorButton));
        btn.setTextColor(this.getResources().getColorStateList(colorText));
    }
}