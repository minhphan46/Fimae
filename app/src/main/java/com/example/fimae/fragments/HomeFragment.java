package com.example.fimae.fragments;

import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_MOVE;
import static android.view.MotionEvent.ACTION_UP;
import static androidx.databinding.DataBindingUtil.setContentView;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fimae.R;
import com.example.fimae.activities.ConnectActivity;
import com.example.fimae.activities.HomeActivity;
import com.example.fimae.activities.WaitingActivity;
import com.example.fimae.adapters.UserHomeViewAdapter;
import com.example.fimae.models.Fimaers;
import com.example.fimae.models.GenderMatch;
import com.example.fimae.models.Report;
import com.example.fimae.repository.ConnectRepo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.slider.RangeSlider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeFragment extends Fragment  {

    // when click call button
    // navigate to waiting screen ( with par to distinguish screen type)
    // delay 5s
    // if dont have user remote => wait
    // if you have user
    // navigate to call screen

    private FirebaseFirestore firestore;
    private CollectionReference fimaeUserRef;

    private View mView;
    private RecyclerView mRcvUsers;
    private HomeActivity homeActivity;
    private UserHomeViewAdapter userAdapter;
    private ArrayList<Fimaers> mUsers;

    private LinearLayout mBtnChat;
    private LinearLayout mBtnCallVoice;
    private LinearLayout mBtnCallVideo;
    private ImageButton mBtnNoti;
    private ImageButton mBtnSetting;
    // setting bottom sheet
    private RangeSlider mRangeAges;
    private TextView mTvRangeAges;
    // btn male
    private LinearLayout mLayoutBtnMale;
    private ImageView mImgMale;
    private TextView mTvMale;
    // btn female
    private LinearLayout mLayoutBtnFemale;
    private ImageView mImgFemale;
    private TextView mTvFemale;
    // btn both
    private LinearLayout mLayoutBtnBoth;
    private ImageView mImgBoth;
    private TextView mTvBoth;
    // btn Hoan thanh
    private AppCompatButton mBtnFinish;
    private GenderMatch genderMatch;

    private String typeCall = "";
    private LinearLayout mFlFloatingWaiting;
    private CircleImageView mImgAvatarWaiting;
    float xDown = 0, yDown = 0;

    static public boolean isShowFloatingWaiting = false;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_home, container, false);
        mBtnChat = (LinearLayout) mView.findViewById(R.id.btn_chat_home);
        mBtnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                typeCall = "chat";
                navigateToWaitingScreen(getContext(), typeCall);
            }
        });
        mBtnCallVoice = (LinearLayout) mView.findViewById(R.id.btn_call_home);
        mBtnCallVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                typeCall = "voice";
                navigateToWaitingScreen(getContext(), typeCall);
            }
        });
        mBtnCallVideo = (LinearLayout) mView.findViewById(R.id.btn_call_video_home);
        mBtnCallVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                typeCall = "video";
                navigateToWaitingScreen(getContext(), typeCall);
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
                settingUser();
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

        firestore = FirebaseFirestore.getInstance();
        GetAllUsers();

        // floating waiting
        mFlFloatingWaiting = mView.findViewById(R.id.floating_waiting);
        mImgAvatarWaiting = mView.findViewById(R.id.img_floating_waiting);

        if(isShowFloatingWaiting) {
            setFloatingWaiting();
            mFlFloatingWaiting.setVisibility(View.VISIBLE);
        }
        else {
            mFlFloatingWaiting.setVisibility(View.GONE);
        }

        return mView;
    }

    private void setFloatingWaiting() {
        if(ConnectRepo.getInstance().getUserLocal() != null){
            String avatar = ConnectRepo.getInstance().getUserLocal().getAvatarUrl();
            Picasso.get().load(avatar).placeholder(R.drawable.ic_default_avatar).into(mImgAvatarWaiting);
        }
        mImgAvatarWaiting.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), WaitingActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            isShowFloatingWaiting = false;
            mFlFloatingWaiting.setVisibility(View.GONE);
        });
        mFlFloatingWaiting.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getActionMasked()) {
                    case ACTION_DOWN:
                        xDown = event.getX();
                        yDown = event.getY();
                        break;
                    case ACTION_MOVE:
                        float movedX, movedY;
                        movedX = event.getX();
                        movedY = event.getY();

                        float distanceX = movedX - xDown;
                        float distanceY = movedY - yDown;

                        mFlFloatingWaiting.setX(mFlFloatingWaiting.getX() + distanceX);
                        mFlFloatingWaiting.setY(mFlFloatingWaiting.getY() + distanceY);
                        break;
                }
                return true;
            }
        });
    }

    private void GetAllUsers(){
        String localUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        // get users from firebase
        fimaeUserRef = firestore.collection("fimaers"); // lay het thu muc user ra
        fimaeUserRef.addSnapshotListener((value, error) -> {
            if (error != null) {
                // Xử lý lỗi
                return;
            }
            mUsers.clear();
            // Lặp qua các tài liệu (tin nhắn) và thêm vào danh sách
            for (QueryDocumentSnapshot document : value) {
                Fimaers user = document.toObject(Fimaers.class);
                // set local
                if(user.getUid().equals(localUid)) ConnectRepo.getInstance().setUserLocal(user);
                mUsers.add(user);
            }
            // Cập nhật giao diện người dùng (RecyclerView)
            userAdapter.notifyDataSetChanged();
        });
    }
// ===== setting user =================================================================================
    private void settingUser(){
        // when click setting button
        View dialogSetting = getLayoutInflater().inflate(R.layout.bottom_sheet_setting, null);

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this.getContext());
        bottomSheetDialog.setContentView(dialogSetting);
        bottomSheetDialog.show();

        // set match components
        mRangeAges = bottomSheetDialog.findViewById(R.id.range_slider_age);
        mTvRangeAges = bottomSheetDialog.findViewById(R.id.tv_st_range_ages);
        // btn male
        mLayoutBtnMale = bottomSheetDialog.findViewById(R.id.btn_st_male);
        mImgMale = bottomSheetDialog.findViewById(R.id.img_st_male);
        mTvMale = bottomSheetDialog.findViewById(R.id.tv_st_male);
        // btn female
        mLayoutBtnFemale = bottomSheetDialog.findViewById(R.id.btn_st_female);
        mImgFemale = bottomSheetDialog.findViewById(R.id.img_st_female);
        mTvFemale = bottomSheetDialog.findViewById(R.id.tv_st_female);
        // btn both
        mLayoutBtnBoth = bottomSheetDialog.findViewById(R.id.btn_st_both);
        mImgBoth = bottomSheetDialog.findViewById(R.id.img_st_both);
        mTvBoth = bottomSheetDialog.findViewById(R.id.tv_st_both);
        // btn Hoan thanh
        mBtnFinish = bottomSheetDialog.findViewById(R.id.btn_st_finish);

        // thay doi range slide
        if(ConnectRepo.getInstance().getUserLocal() != null){
            genderMatch = ConnectRepo.getInstance().getUserLocal().getGenderMatch();
            if(genderMatch != null){
                toggleGenderButtons(genderMatch);
            }
            float min = ConnectRepo.getInstance().getUserLocal().getMinAgeMatch();
            float max = ConnectRepo.getInstance().getUserLocal().getMaxAgeMatch();
            if(12 <= min && min <= 40 && 12 <= max && max <= 40) {
                String rangeAges = Math.round(min) + "-" + Math.round(max);
                mTvRangeAges.setText(rangeAges);
                mRangeAges.setValues(min, max);
            }
        }

        mRangeAges.addOnChangeListener((slider, value, fromUser) -> {
            String rangeAges = Math.round(slider.getValues().get(0)) + "-" + Math.round(slider.getValues().get(1));
            mTvRangeAges.setText(rangeAges);
        });

        mLayoutBtnMale.setOnClickListener(v -> {
            // click btn male
            genderMatch = GenderMatch.male;
            toggleGenderButtons(genderMatch);
        });
        mLayoutBtnFemale.setOnClickListener(v -> {
            // click btn female
            genderMatch = GenderMatch.female;
            toggleGenderButtons(genderMatch);
        });
        mLayoutBtnBoth.setOnClickListener(v -> {
            // click btn both
            genderMatch = GenderMatch.both;
            toggleGenderButtons(genderMatch);
        });

        mBtnFinish.setOnClickListener(v -> {
            // finish and save to user
            if(ConnectRepo.getInstance().getUserLocal() != null){
                if(genderMatch != null){
                    ConnectRepo.getInstance().getUserLocal().setGenderMatch(genderMatch);
                }
                ConnectRepo.getInstance().getUserLocal().setMinAgeMatch(Math.round(mRangeAges.getValues().get(0)));
                ConnectRepo.getInstance().getUserLocal().setMaxAgeMatch(Math.round(mRangeAges.getValues().get(1)));
            }
            // update in firebase
            fimaeUserRef.document(ConnectRepo.getInstance().getUserLocal().getUid()).set(ConnectRepo.getInstance().getUserLocal())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getContext(), "User has been updated..", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), "Fail to update the data..", Toast.LENGTH_SHORT).show();
                        }
            });
            bottomSheetDialog.dismiss();
        });
    }
    private void toggleGenderButtons(GenderMatch genderMatch) {
        if(genderMatch == GenderMatch.male) {
            mImgMale.setColorFilter(ContextCompat.getColor(getContext(), R.color.primary_2), PorterDuff.Mode.SRC_IN);
            mTvMale.setTextColor(ContextCompat.getColor(getContext(), R.color.primary_2));

            mImgFemale.setColorFilter(ContextCompat.getColor(getContext(), R.color.text_tertiary), android.graphics.PorterDuff.Mode.SRC_IN);
            mTvFemale.setTextColor(ContextCompat.getColor(getContext(), R.color.background_button_dark_1_startColor));

            mImgBoth.setColorFilter(ContextCompat.getColor(getContext(), R.color.text_tertiary), android.graphics.PorterDuff.Mode.SRC_IN);
            mTvBoth.setTextColor(ContextCompat.getColor(getContext(), R.color.background_button_dark_1_startColor));
        } else if(genderMatch == GenderMatch.female) {
            mImgMale.setColorFilter(ContextCompat.getColor(getContext(), R.color.text_tertiary), android.graphics.PorterDuff.Mode.SRC_IN);
            mTvMale.setTextColor(ContextCompat.getColor(getContext(), R.color.background_button_dark_1_startColor));

            mImgFemale.setColorFilter(ContextCompat.getColor(getContext(), R.color.primary_2), android.graphics.PorterDuff.Mode.SRC_IN);
            mTvFemale.setTextColor(ContextCompat.getColor(getContext(), R.color.primary_2));

            mImgBoth.setColorFilter(ContextCompat.getColor(getContext(), R.color.text_tertiary), android.graphics.PorterDuff.Mode.SRC_IN);
            mTvBoth.setTextColor(ContextCompat.getColor(getContext(), R.color.background_button_dark_1_startColor));
        } else if(genderMatch == GenderMatch.both) {
            mImgMale.setColorFilter(ContextCompat.getColor(getContext(), R.color.text_tertiary), android.graphics.PorterDuff.Mode.SRC_IN);
            mTvMale.setTextColor(ContextCompat.getColor(getContext(), R.color.background_button_dark_1_startColor));

            mImgFemale.setColorFilter(ContextCompat.getColor(getContext(), R.color.text_tertiary), android.graphics.PorterDuff.Mode.SRC_IN);
            mTvFemale.setTextColor(ContextCompat.getColor(getContext(), R.color.background_button_dark_1_startColor));

            mImgBoth.setColorFilter(ContextCompat.getColor(getContext(), R.color.primary_2), android.graphics.PorterDuff.Mode.SRC_IN);
            mTvBoth.setTextColor(ContextCompat.getColor(getContext(), R.color.primary_2));
        }
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
