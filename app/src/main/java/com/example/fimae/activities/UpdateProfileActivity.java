package com.example.fimae.activities;

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
import java.text.SimpleDateFormat;
import java.util.Date;

public class UpdateProfileActivity extends AppCompatActivity {

    public static final int PICK_IMAGE = 1;
    EditText firstName, lastName, bio, phoneNumber;
    Button create;
    ImageView addDisplayPic;
    CircleImageView dp;
    Uri imageURI = null;
    RadioGroup genderRadioGroup;
    RadioButton genderRadioButton;
    String email, password, ID;
    FirebaseFirestore firestore;
    CollectionReference fimaersRef;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        firstName = findViewById(R.id.first_name);
        lastName = findViewById(R.id.last_name);
        genderRadioGroup = findViewById(R.id.genderRadioGroup);
        bio = findViewById(R.id.bio);
        create = findViewById(R.id.create);
        addDisplayPic = findViewById(R.id.add_display_pic);
        dp = findViewById(R.id.display_pic);
        phoneNumber = findViewById(R.id.phoneNumber);
        firestore = FirebaseFirestore.getInstance();
        fimaersRef = firestore.collection("fimaers");
        EditText dob = findViewById(R.id.age);
        storageReference = FirebaseStorage.getInstance().getReference("AvatarPics");

        Intent intent = getIntent();
        email = intent.getStringExtra("email");
        password = intent.getStringExtra("password");
        ID = intent.getStringExtra("id");

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int radioButtonID = genderRadioGroup.getCheckedRadioButtonId();
                String dateFormat = "dd/MM/yyyy";
                Date dateOfBirth;
                SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
                try {
                    dateOfBirth = sdf.parse(dob.getText().toString());
                } catch (Exception e) {
                    Toast.makeText(UpdateProfileActivity.this,
                            "Date of birth invalid: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    return;
                }
                if (firstName.getText().toString().equals("") ||
                        lastName.getText().toString().equals("") ||
                        radioButtonID == -1) {
                    Toast.makeText(UpdateProfileActivity.this,
                            "Please fill all the necessary input fields", Toast.LENGTH_LONG).show();
                } else if (imageURI == null) {
                    Toast.makeText(UpdateProfileActivity.this,
                            "Please select image", Toast.LENGTH_LONG).show();
                } else {
                    genderRadioButton = findViewById(radioButtonID);
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    assert user != null;
                    storageReference.child(user.getUid() + ".jpg").putFile(imageURI)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Task<Uri> task = taskSnapshot.getStorage().getDownloadUrl();
                                    task
                                            .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    String uid = user.getUid();
                                                    fimaersRef.document(uid).set(new Fimaers(
                                                            user.getUid(), lastName.getText().toString(), firstName.getText().toString(), genderRadioButton.getId() == R.id.radioButtonMale,
                                                            user.getEmail(), phoneNumber.getText().toString(), task.getResult().toString(), bio.getText().toString(), dateOfBirth, new Date(), "eyJjdHkiOiJzdHJpbmdlZS1hcGk7dj0xIiwidHlwIjoiSldUIiwiYWxnIjoiSFMyNTYifQ.eyJqdGkiOiJTSy4wLnM1OFRaMnBJbkIwMFdWMlZmTlQ1RXRmU2xLQ2g3cy0xNjgyODk0NzE0IiwiaXNzIjoiU0suMC5zNThUWjJwSW5CMDBXVjJWZk5UNUV0ZlNsS0NoN3MiLCJleHAiOjE2ODU0ODY3MTQsInVzZXJJZCI6Im1pbmgifQ.rtlgkQhsZMhSUFnxfBk0zSeg0BPHRHHh4SQ54A1GTm8"
                                                    ));
                                                    Intent intent = new Intent(UpdateProfileActivity.this, HomeActivity.class);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(UpdateProfileActivity.this, "Failed to Upload Image", Toast.LENGTH_LONG).show();
                                                }
                                            })
                                    ;
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(UpdateProfileActivity.this, "Failed to Upload Image", Toast.LENGTH_LONG).show();
                                }
                            })
                    ;
                }
            }
        });
        addDisplayPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            addDisplayPic.setAlpha((float) 0);
            imageURI = data.getData();
            dp.setImageURI(imageURI);
        }
    }
}
