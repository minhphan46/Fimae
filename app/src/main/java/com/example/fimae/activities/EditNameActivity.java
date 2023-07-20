package com.example.fimae.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.fimae.R;
import com.example.fimae.models.Fimaers;
import com.example.fimae.repository.FimaerRepository;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class EditNameActivity extends AppCompatActivity {

    ImageButton backBtn, confirmBtn;
    EditText editNameTxt;
    FimaerRepository userRepo = FimaerRepository.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_name);
        backBtn = findViewById(R.id.editNameBackBtn);
        editNameTxt = findViewById(R.id.nameEditText);
        confirmBtn = findViewById(R.id.editNameConfirmBtn);
        String name = getIntent().getStringExtra("name");
        editNameTxt.setText(name);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("name", editNameTxt.getText().toString());
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });
    }
}