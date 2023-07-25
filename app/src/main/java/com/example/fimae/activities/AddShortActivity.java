package com.example.fimae.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
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
    private String urlVideo;

    private boolean isLoading = false;
    @SuppressLint("UseCompatLoadingForColorStateLists")
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
        // image video
        binding.imageVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MediaListDialogFragment mediaListDialogFragment = new MediaListDialogFragment();
                mediaListDialogFragment.setOnMediaSelectedListener(new MediaListDialogFragment.OnMediaSelectedListener() {
                    @Override
                    public void OnMediaSelected(boolean isSelected, ArrayList<String> data) {
                        if(isSelected){
                            urlVideo = data.get(0);
                            binding.icAdd.setVisibility(View.GONE);
                            Glide.with(AddShortActivity.this).load(data.get(0)).into(binding.imageVideo);
                        }
                    }
                });
                mediaListDialogFragment.show(getSupportFragmentManager(), "mediaList");
            }
        });
        // post mode =====================================================================================
        // set default value
        binding.radioGroup.check(binding.rbPublic.getId());
        // click layout public
        binding.layoutPublic.setOnClickListener(view -> {
            binding.rbPublic.setChecked(true);
            binding.rbPublic.setButtonDrawable(R.drawable.radio_button_custom_background);
        });
        // click layout friend
        binding.layoutFriends.setOnClickListener(view -> {
            binding.rbFriend.setChecked(true);
            binding.rbFriend.setButtonDrawable(R.drawable.radio_button_custom_background);
        });
        // click layout private
        binding.layoutPrivate.setOnClickListener(view -> {
            binding.rbPrivate.setChecked(true);
            binding.rbPrivate.setButtonDrawable(R.drawable.radio_button_custom_background);
        });
        // allow comment =================================================================================
        binding.cbComment.setChecked(true);
        // click layout allow comment
        binding.layoutAllowComment.setOnClickListener(view -> {
            binding.cbComment.setChecked(!binding.cbComment.isChecked());
        });
        //================================================================================================
        // finish
        binding.btnFinish.setOnClickListener(view -> {
            String description = binding.tvDescription.getText().toString();
            PostMode postMode = getPostMode(binding.radioGroup.getCheckedRadioButtonId());
            boolean allowComment = binding.cbComment.isChecked();
            if(description.isEmpty() || urlVideo == null){
                Toast.makeText(getBaseContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }
            binding.loading.setVisibility(View.VISIBLE);
            binding.btnFinish.setEnabled(false);
            binding.btnFinish.setBackgroundTintList(this.getResources().getColorStateList(R.color.gray_400));
            ShortsRepository.getInstance().createShortVideo(description, Uri.parse(urlVideo), postMode, allowComment).addOnCompleteListener(new OnCompleteListener<ShortMedia>() {
                @Override
                public void onComplete(@NonNull Task<ShortMedia> task) {
                    binding.loading.setVisibility(View.GONE);
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

    private PostMode getPostMode(int id) {
        switch (id)
        {
            case R.id.rb_public:
                return PostMode.PUBLIC;
            case R.id.rb_friend:
                return PostMode.FRIEND;
            case R.id.rb_private:
                return PostMode.PRIVATE;
        }
        return PostMode.PUBLIC;
    }
}