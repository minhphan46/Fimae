package com.example.fimae.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.fimae.R;
import com.example.fimae.databinding.ActivityAddShortBinding;
import com.example.fimae.fragments.MediaListDialogFragment;
import com.example.fimae.models.shorts.ShortMedia;
import com.example.fimae.models.story.Story;
import com.example.fimae.repository.ShortsRepository;
import com.example.fimae.repository.StoryRepository;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

public class AddShortActivity extends AppCompatActivity {

    ActivityAddShortBinding binding;
    ImageView imageVideo;
    private String urlVideo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddShortBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        imageVideo = findViewById(R.id.image_video);
        imageVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MediaListDialogFragment mediaListDialogFragment = new MediaListDialogFragment();
                mediaListDialogFragment.setOnMediaSelectedListener(new MediaListDialogFragment.OnMediaSelectedListener() {
                    @Override
                    public void OnMediaSelected(boolean isSelected, ArrayList<String> data) {
                        if(isSelected){
                            urlVideo = data.get(0);
                            Glide.with(AddShortActivity.this).load(data.get(0)).into(imageVideo);
                            Toast.makeText(getBaseContext(), urlVideo, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                mediaListDialogFragment.show(getSupportFragmentManager(), "mediaList");
            }
        });

        binding.btnFinish.setOnClickListener(view -> {
            String description = binding.tvDescription.getText().toString();
            ShortsRepository.getInstance().createShortVideo(description, Uri.parse(urlVideo), PostMode.PUBLIC, true).addOnCompleteListener(new OnCompleteListener<ShortMedia>() {
                @Override
                public void onComplete(@NonNull Task<ShortMedia> task) {
                    if(task.isSuccessful()) {
                        ShortMedia shortMedia = task.getResult();
                        Toast.makeText(getBaseContext(), "Short create successful", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(getBaseContext(), "Error", Toast.LENGTH_SHORT).show();
                        task.getException().printStackTrace();
                    }
                }
            });
        });
    }
}