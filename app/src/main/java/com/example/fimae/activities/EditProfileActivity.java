package com.example.fimae.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fimae.R;

import java.util.Calendar;

public class EditProfileActivity extends AppCompatActivity {

    ImageButton backBtn;
    TextView dobTxt, litIdTxt;
    RelativeLayout relativeName, relativeLitId, relativeDOB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        backBtn = findViewById(R.id.backBtn);
        relativeName = findViewById(R.id.relativeName);
        relativeLitId = findViewById(R.id.relaviveLitId);
        relativeDOB = findViewById(R.id.relativeDOB);
        dobTxt = findViewById(R.id.dobTxt);
        litIdTxt = findViewById(R.id.litIdTxt);
        relativeDOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateTimePicker();
            }
        });
        relativeLitId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                copyLitId();
            }
        });
        relativeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditProfileActivity.this,EditNameActivity.class);
                startActivity(intent);
            }
        });
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void copyLitId() {
        // Get the text from the TextView
        String litId = litIdTxt.getText().toString();

        // Copy the text to the clipboard
        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("Lit ID", litId);
        if (clipboardManager != null) {
            clipboardManager.setPrimaryClip(clipData);
        }

        // Show a toast message
        Toast.makeText(this, "Lit ID copied", Toast.LENGTH_SHORT).show();
    }

    private void showDateTimePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        // Create a date-time picker dialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                EditProfileActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        String selectedDate = day + "-" + (month + 1) + "-" + year;
                        dobTxt.setText(selectedDate);
                    }
                },
                year,
                month,
                dayOfMonth
        );

        datePickerDialog.show();
    }
}