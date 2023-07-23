package com.example.fimae.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.DatePickerDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fimae.R;
import com.example.fimae.Utils;
import com.example.fimae.databinding.ActivityEditProfileBinding;
import com.example.fimae.models.Fimaers;
import com.example.fimae.viewmodels.EditProfileViewModel;
import com.example.fimae.viewmodels.ProfileViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;
import java.util.Date;

public class EditProfileActivity extends AppCompatActivity {

    ImageButton backBtn, confirmBtn;
    TextView dobTxt, litIdTxt;
    RelativeLayout relativeName, relativeLitId, relativeDOB;
    TextInputEditText bioTxt;

    public static final int REQUEST_CODE_EDIT_NAME = 511;

    ActivityEditProfileBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EditProfileViewModel viewModel = new ViewModelProvider(this).get(EditProfileViewModel.class);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_edit_profile);
        binding.setLifecycleOwner(this);
        binding.setViewmodel(viewModel);
        confirmBtn = findViewById(R.id.confirmBtn);
        backBtn = findViewById(R.id.backBtn);
        relativeName = findViewById(R.id.relativeName);
        relativeLitId = findViewById(R.id.relaviveLitId);
        relativeDOB = findViewById(R.id.relativeDOB);
        dobTxt = findViewById(R.id.dobTxt);
        litIdTxt = findViewById(R.id.litIdTxt);
        bioTxt = findViewById(R.id.editText);


        binding.tabView.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.i("TAG", "onTabSelected: ");
                viewModel.setChip(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        bioTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(bioTxt.hasFocus())
                {
                    viewModel.hasChange = true;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                viewModel.updateUser().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        finish();
                    }
                });
            }
        });
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
                intent.putExtra("name", binding.getViewmodel().getUser().getValue().getName());
                startActivityForResult(intent,REQUEST_CODE_EDIT_NAME);
            }
        });
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(binding.getViewmodel().hasChange)
        {
            Utils.showConfirmationDialog(EditProfileActivity.this, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    binding.getViewmodel().fetchUser();
                    finish();
                }
            });
        } else
        {
            super.onBackPressed();
        }
    }

    private void copyLitId() {
        // Get the text from the TextView
        String litId = binding.getViewmodel().getUser().getValue().getUid();

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
        Date dob = binding.getViewmodel().getUser().getValue().getDob();
        if (dob != null) {
            // Set the selected date components from the existing Date object
            calendar.setTime(dob);
        }
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        // Get the Date object from the ViewModel



        // Create a date-time picker dialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                EditProfileActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        calendar.set(year, month, day); // Set the selected year, month, and day to the Calendar
                        Date dob = calendar.getTime();
                        binding.getViewmodel().hasChange = true;
                        binding.getViewmodel().getUser().getValue().setDob(dob);
                    }
                },
                year,
                month,
                dayOfMonth
        );

        datePickerDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_EDIT_NAME && resultCode == RESULT_OK)
        {
            if(data!=null)
            {
                binding.getViewmodel().hasChange = true;
                binding.getViewmodel().setName(data.getStringExtra("name"));
            }

        }
    }
}