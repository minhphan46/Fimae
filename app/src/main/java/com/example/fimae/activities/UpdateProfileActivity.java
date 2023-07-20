package com.example.fimae.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.fimae.R;
import com.example.fimae.models.Fimaers;
import com.example.fimae.models.GenderMatch;
import com.example.fimae.repository.FimaerRepository;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import de.hdodenhof.circleimageview.CircleImageView;

import java.text.Collator;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.google.type.DateTime;

public class UpdateProfileActivity extends AppCompatActivity {
    private static final int PICK_IMAGE = 1;
    private static final String DATE_FORMAT = "dd/MM/yyyy";
    EditText firstName;
    EditText lastName;
    RadioGroup genderRadioGroup;
    EditText bio;
    Button create;
    ImageView addDisplayPic;
    CircleImageView dp;
    EditText phoneNumber;
    EditText dob;
    private Uri imageURI = null;
    private RadioButton genderRadioButton;
    private DatePickerDialog datePickerDialog;
    FimaerRepository userRepo = FimaerRepository.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        initView();
        initListener();
    }


    private void initView() {
        firstName = findViewById(R.id.first_name);
        lastName = findViewById(R.id.last_name);
        genderRadioGroup = findViewById(R.id.genderRadioGroup);
        bio = findViewById(R.id.bio);
        create = findViewById(R.id.create);
        addDisplayPic = findViewById(R.id.add_display_pic);
        dp = findViewById(R.id.display_pic);
        phoneNumber = findViewById(R.id.phoneNumber);
        dob = findViewById(R.id.age);
    }


    private void initListener() {
        create.setOnClickListener(view -> saveProfile());
        addDisplayPic.setOnClickListener(view -> pickImage());
        dob.setOnClickListener(view -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            datePickerDialog = new DatePickerDialog(this, (datePicker, y, m, d) -> {
                calendar.set(y, m, d);
                dob.setText(new SimpleDateFormat("dd/MM/yyyy").format(calendar.getTime()));
            }, year, month, day);
            datePickerDialog.show();
        });
    }

    private void saveProfile() {
        int radioButtonID = genderRadioGroup.getCheckedRadioButtonId();
        String dateFormat = DATE_FORMAT;
        Date dateOfBirth;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
            dateOfBirth = sdf.parse(dob.getText().toString());
        } catch (ParseException e) {
            showToast("Date of birth invalid: " + e.getMessage());
            return;
        }

        if (firstName.getText().toString().isEmpty() || lastName.getText().toString().isEmpty() || radioButtonID == -1) {
            showToast("Please fill all the necessary input fields");
        } else if (imageURI == null) {
            showToast("Please select an image");
        } else {
            genderRadioButton = findViewById(radioButtonID);
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user == null) {
                return;
            }

            userRepo.uploadAvatar(user.getUid(), imageURI, new FimaerRepository.UploadAvatarCallback() {
                @Override
                public void onUploadSuccess(Uri uri) {
                    userRepo.updateProfile(
                            new Fimaers(user.getUid(),
                                    lastName.getText().toString(),
                                    firstName.getText().toString(),
                                    genderRadioButton.getId() == R.id.radioButtonMale,
                                    user.getEmail(),
                                    phoneNumber.getText().toString(),
                                    uri.toString(),
                                    null,
                                    bio.getText().toString(),
                                    dateOfBirth,
                                    new Date(),
                                    null,
                                    16,
                                    20,
                                    GenderMatch.male
                            )
                    );
                    Intent intent = new Intent(UpdateProfileActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onUploadError(String errorMessage) {
                    showToast("Failed to upload image");
                }
            });
        }
    }

    private void pickImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            addDisplayPic.setAlpha(0f);
            imageURI = data.getData();
            dp.setImageURI(imageURI);
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
