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
    private static final String API_KEY_SID = "AC9bc361f7d58a91c5d27267fd380a017f";
    private static final String API_KEY_SECRET = "zFot3ZBCik1A9AYLzupXgi4S7zbAmTXj";
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
    private FirebaseFirestore firestore;
    private CollectionReference fimaersRef;
    private StorageReference storageReference;
    private DatePickerDialog datePickerDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        initView();
        initFirebase();
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

    private void initFirebase() {
        firestore = FirebaseFirestore.getInstance();
        fimaersRef = firestore.collection("fimaers");
        storageReference = FirebaseStorage.getInstance().getReference("AvatarPics");
        firestore = FirebaseFirestore.getInstance();
        fimaersRef = firestore.collection("fimaers");
        storageReference = FirebaseStorage.getInstance().getReference("AvatarPics");
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

    public static String genAccessToken(String userID,String keySid, String keySecret, int expireInSecond) {
        try {
            Algorithm algorithmHS = Algorithm.HMAC256(keySecret);

            Map<String, Object> headerClaims = new HashMap<String, Object>();
            headerClaims.put("typ", "JWT");
            headerClaims.put("alg", "HS256");
            headerClaims.put("cty", "stringee-api;v=1");

            long exp = (long) (System.currentTimeMillis()) + expireInSecond * 1000;
            Calendar calendar = Calendar.getInstance();
            Date now = calendar.getTime();

            // Cộng thêm 30 ngày
            calendar.add(Calendar.DAY_OF_YEAR, 30);
            Date futureDate = calendar.getTime();
            String token = JWT.create().withHeader(headerClaims)
                    .withClaim("jti", keySid + "-" + System.currentTimeMillis())
                    .withClaim("iss", keySid)
                    .withClaim("userId", userID)
                    .withExpiresAt(futureDate)
                    .sign(algorithmHS);

            return token;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
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

            String access_token = genAccessToken(user.getUid(),API_KEY_SID, API_KEY_SECRET, 3600);

            storageReference.child(user.getUid() + ".jpg").putFile(imageURI).addOnSuccessListener(taskSnapshot -> {
                Task<Uri> task = taskSnapshot.getStorage().getDownloadUrl();
                task.addOnSuccessListener(uri -> {
                    String uid = user.getUid();
                    fimaersRef.document(uid).set(
                            new Fimaers(user.getUid(),
                                    lastName.getText().toString(),
                                    firstName.getText().toString(),
                                    genderRadioButton.getId() == R.id.radioButtonMale,
                                    user.getEmail(),
                                    phoneNumber.getText().toString(),
                                    uri.toString(),
                                    bio.getText().toString(),
                                    dateOfBirth,
                                    new Date(),
                                    access_token
                            )
                    );

                    Intent intent = new Intent(UpdateProfileActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                });
            }).addOnFailureListener(e -> showToast("Failed to upload image"));
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