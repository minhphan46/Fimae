package com.example.fimae.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.fimae.R;
import com.example.fimae.models.DisableUser;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class DisabledUserActivity extends AppCompatActivity {
    TextView reason;
    TextView noti;
    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disabled_user);
        noti = findViewById(R.id.titleTxt);
        reason = findViewById(R.id.warningTxt);
        button = findViewById(R.id.buttonFinish);
        Intent intent = getIntent();
        String disableId =  intent.getStringExtra("disableId");
        String type = intent.getStringExtra("type");
        FirebaseFirestore.getInstance().collection("user_disable")
                .document(disableId+type)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        DisableUser disableUser = documentSnapshot.toObject(DisableUser.class);
                        if(disableUser == null){
                            if(type=="POST"){
                                Intent intent1 = new Intent(DisabledUserActivity.this, PostActivity.class);
                                startActivity(intent1);
                            }
                            else if(type == "SHORT"){
                                Intent intent1 = new Intent(DisabledUserActivity.this, AddShortActivity.class);
                                startActivity(intent1);
                            }
                            finish();
                        }
                        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                        if(type.equals("USER")){
                            noti.setText("Tài khoản của bạn sẽ bị vô hiệu hóa đến ngày " + sdf.format(disableUser.getTimeEnd()));
                            reason.setText("Lí do: " + disableUser.getReason().toString());
                        }
                        else if(type.equals("POST")) {
                            noti.setText("Tài khoản của bạn sẽ bị vô hiệu hóa đăng tin đến ngày " + sdf.format(disableUser.getTimeEnd()));
                            reason.setText("Lí do: " + disableUser.getReason().toString());
                            button.setVisibility(View.GONE);

                        }
                        else if(type.equals("SHORT")){
                            noti.setText("Tài khoản của bạn sẽ bị vô hiệu hóa đăng short đến ngày " + sdf.format(disableUser.getTimeEnd()));
                            reason.setText("Lí do: " + disableUser.getReason().toString());
                            button.setVisibility(View.GONE);
                        }
                    }
                });
//                .get(new EventListener<DocumentSnapshot>() {
//                    @Override
//                    public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException error) {
//                        if (error != null) {
//                            return;
//                        }
//                        if (snapshot != null && snapshot.exists()) {
//                            // Document exists, check if the user is disabled
//                            DisableUser disableUser = snapshot.toObject(DisableUser.class);
//                            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
//                            noti.setText("Tài khoản của bạn sẽ bị vô hiệu hóa đến ngày " + sdf.format(disableUser.getTimeEnd()));
//                            reason.setText("Lí do: " + disableUser.getReason().toString());
//                        }
//                    }
//                });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent1 = new Intent(DisabledUserActivity.this, AuthenticationActivity.class);
                startActivity(intent1);
            }
        });

    }
}