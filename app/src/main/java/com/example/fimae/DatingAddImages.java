package com.example.fimae;

import static com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.example.fimae.activities.DatingSettings;
import com.example.fimae.adapters.Dating.DatingImageAdapter;
import com.example.fimae.adapters.Dating.DatingImageAdapterItem;
import com.example.fimae.adapters.PostPhotoAdapter;
import com.example.fimae.models.dating.DatingProfile;
import com.example.fimae.models.dating.LatLng;
import com.example.fimae.repository.DatingRepository;
import com.example.fimae.utils.FileUtils;
import com.google.android.gms.location.CurrentLocationRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class DatingAddImages extends AppCompatActivity {

    private boolean checkReadImageAndVideoPermission(){
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_VIDEO) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_MEDIA_IMAGES, android.Manifest.permission.READ_MEDIA_VIDEO}, 1);
                Toast.makeText(getApplicationContext(), "Please allow permission to access your gallery", Toast.LENGTH_SHORT).show();
                return false;
            } else {
                return true;
            }
        } else {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                Toast.makeText(getApplicationContext(), "Please allow permission to access your gallery", Toast.LENGTH_SHORT).show();
                return false;
            } else {
                return true;
            }
        }
    }
    DatingImageAdapter postPhotoAdapter;
    RecyclerView recyclerView;
    private ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Intent data = result.getData();
                        if (data.getClipData() != null) {
                            int itemCount = data.getClipData().getItemCount();
                            for (int i = 0; i < itemCount; i++) {
                                Uri imageUrl = data.getClipData().getItemAt(i).getUri();
                                postPhotoAdapter.addImage(imageUrl.toString(), true);
                            }
                        } else {
                            Uri imageUrl = data.getData();
                            postPhotoAdapter.addImage(imageUrl.toString(), true);
                        }
                        postPhotoAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getApplicationContext(), "You haven't picked Image", Toast.LENGTH_LONG).show();
                    }
                }
            }
    );

    public void addImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        //add request code
        launcher.launch(intent);
    }

    void createProfile() {
        //Use fusedLocationProviderClient to get current location
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            return;
        }
        fusedLocationProviderClient.getCurrentLocation(PRIORITY_HIGH_ACCURACY, null)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Location location = task.getResult();
                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                        ArrayList<Uri> images = new ArrayList<>();
                        for (DatingImageAdapterItem item : postPhotoAdapter.getItems()) {


                            String url = item.getUrl();
                            Uri uri = Uri.parse(url);
                            String path = FileUtils.getFilePathFromContentUri(getApplicationContext(), uri);
                            images.add(Uri.parse(path));
                        }
                        Log.e("DatingAddImages", "createProfile: " + images.size());
                        DatingRepository.getInstance().createDatingProfile(latLng, images).addOnCompleteListener(new OnCompleteListener<DatingProfile>() {
                            @Override
                            public void onComplete(@NonNull Task<DatingProfile> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(), "Profile created", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(), DatingSettings.class);
                                    intent.putExtra("uid", uid);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    progressdialog.dismiss();
                                    Toast.makeText(getApplicationContext(), "Profile creation failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        Toast.makeText(getApplicationContext(), "Failed to get location", Toast.LENGTH_SHORT).show();
                        progressdialog.dismiss();
                    }
                });
    }

    void submit() {
        if (!checkReadImageAndVideoPermission()) {
            return;
        }
        progressdialog.show();
        if (postPhotoAdapter.getItems().size() < 2 || postPhotoAdapter.getItems().size() > 6) {
            Toast.makeText(getApplicationContext(), "Please select 2 to 6 images", Toast.LENGTH_SHORT).show();
            return;
        }
        if (isCreate) {
            createProfile();
        } else {
            updateProfile();
        }
    }

    private void updateProfile() {
        ArrayList<Uri> remoteImages = new ArrayList<>();
        ArrayList<Uri> localImages = new ArrayList<>();
        for (DatingImageAdapterItem item : postPhotoAdapter.getItems()) {
            if (item.isLocal()) {
                Uri uri = Uri.parse(item.getUrl());
                Uri fileUri = Uri.parse(FileUtils.getFilePathFromContentUri(this, uri));
                localImages.add(fileUri);
            } else {
                remoteImages.add(Uri.parse(item.getUrl()));
            }
        }
        DatingRepository.getInstance().updateDatingProfileImages(remoteImages, localImages).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Intent intent = new Intent();
                ArrayList<String> images = new ArrayList<>();
                for(Uri uri : task.getResult()){
                    images.add(uri.toString());
                }
                intent.putExtra("urls", images);
                setResult(RESULT_OK,intent );
                Toast.makeText(getApplicationContext(), "Profile updated", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(getApplicationContext(), "Profile update failed", Toast.LENGTH_SHORT).show();
            }
            progressdialog.dismiss();
        });
    }
    ProgressDialog progressdialog;
    MaterialButton button;
    private boolean isCreate = true;
    private String uid;
    ArrayList<DatingImageAdapterItem> imageList;
    FusedLocationProviderClient fusedLocationProviderClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dating_add_images);
        getArgs(getIntent());
        postPhotoAdapter = new DatingImageAdapter(imageList, () -> addImage());
        recyclerView = findViewById(R.id.dating_images_recycler_view);
        LinearLayoutManager linearLayoutManager = new GridLayoutManager(getApplicationContext(), 3);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(postPhotoAdapter);
        button = findViewById(R.id.dating_images_add_button);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        button.setOnClickListener(v -> submit());
         progressdialog = new ProgressDialog(this);
        progressdialog.setMessage("Đang cập nhật ảnh");
        progressdialog.setCancelable(false);
    }

    private void getArgs(Intent intent) {
        assert intent != null;
        isCreate = intent.getBooleanExtra("isCreate", true);
        uid = intent.getStringExtra("uid");
        if (isCreate) {
            imageList = new ArrayList<>();
        } else {
            imageList = (ArrayList<DatingImageAdapterItem>) intent.getSerializableExtra("images");
            if (imageList == null) {
                throw new NullPointerException("imageList is null");
            }
        }
    }
}