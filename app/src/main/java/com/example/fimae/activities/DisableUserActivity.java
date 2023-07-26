package com.example.fimae.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;

import com.example.fimae.R;
import com.example.fimae.databinding.ActivityCreateProfileBinding;
import com.example.fimae.databinding.ActivityDisableUserBinding;
import com.example.fimae.models.UserDisable;
import com.example.fimae.repository.AdminRepository;
import com.google.android.gms.tasks.OnSuccessListener;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DisableUserActivity extends AppCompatActivity {
    ActivityDisableUserBinding binding;
    UserDisable userDisable;
    String uid;
    String type;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_disable_user);
        Intent intent = getIntent();
        uid = intent.getStringExtra("id");
        type = intent.getStringExtra("type");
        userDisable = new UserDisable();
        userDisable.setUserId(uid);
        if(type.equals("POST")){
            binding.titleTxt.setText("Vô hiệu hóa tạo bài viết mới");
            binding.warningTxt.setText("Người dùng sẽ không thể đăng bài viết cho đến khi hết thời hạn vô hiệu hóa");
        }else if(type.equals("SHORT")){
            binding.titleTxt.setText("Vô hiệu hóa tạo short");
            binding.warningTxt.setText("Người dùng sẽ không thể đăng short cho đến khi hết thời hạn vô hiệu hóa");
        }
        else if(type.equals("STORY")){
            binding.titleTxt.setText("Vô hiệu hóa tạo story mới");
            binding.warningTxt.setText("Người dùng sẽ không thể đăng story cho đến khi hết thời hạn vô hiệu hóa");
        }
        binding.dobEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateTimePicker();
            }
        });
        binding.buttonFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.progressBar.setVisibility(View.VISIBLE);
                binding.contentLayout.setVisibility(View.GONE);
                userDisable.setReason(binding.nameEditText.getText().toString());
                AdminRepository.getInstance().disableUser(userDisable, type).addOnSuccessListener(new OnSuccessListener<Boolean>() {
                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        finish();
                    }
                });
            }
        });
    }
    private void showDateTimePicker() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        Date dob = new Date();
        if(binding.dobEditText != null)
        {
            try {
                dob = sdf.parse(binding.dobEditText.getText().toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        if (dob != null) {
            calendar.setTime(dob);
        }
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        // Get the Date object from the ViewModel



        // Create a date-time picker dialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        calendar.set(year, month, day); // Set the selected year, month, and day to the Calendar
                        userDisable.setTimeEnd(calendar.getTime());
                        binding.dobEditText.setText(sdf.format(calendar.getTime()));
                        validateData();
                    }
                },
                year,
                month,
                dayOfMonth
        );

        datePickerDialog.show();
    }

    private void validateData() {
        // Get the data from EditTexts using binding
        String reason = binding.nameEditText.getText().toString().trim();
        String date = binding.dobEditText.getText().toString().trim();

        // Perform your data validation here
        if (!reason.isEmpty() && !date.isEmpty() && userDisable.getTimeEnd() != null && userDisable.getTimeEnd().after(new Date())) {
            binding.buttonFinish.setEnabled(true);
        } else {
            binding.buttonFinish.setEnabled(false);
        }
    }
}