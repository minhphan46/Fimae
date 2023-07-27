package com.example.fimae.activities;

import static com.example.fimae.activities.DetailPostActivity.REQUEST_EDITPOST_CODE;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fimae.R;
import com.example.fimae.adapters.PostPhotoAdapter;
import com.example.fimae.bottomdialogs.PostModeFragment;
import com.example.fimae.databinding.ActivityPostBinding;
import com.example.fimae.models.Post;
import com.example.fimae.models.Seed;
import com.example.fimae.repository.PostRepository;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PostActivity extends AppCompatActivity implements View.OnClickListener {
    ActivityPostBinding binding;
    List<Uri> imageList;
    List<String> editedImageList;
    PostPhotoAdapter postPhotoAdapter;
    Post editPost;
    PostRepository postRepo;
    boolean canPost = false;
    boolean isEdit = false;
    private PostMode postMode = PostMode.PUBLIC;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         binding = DataBindingUtil.setContentView(this, R.layout.activity_post);

        Intent intent = getIntent();
        String postId = intent.getStringExtra("id");
        if(postId != null && !postId.trim().isEmpty()){
            isEdit = true;
            PostRepository.getInstance().postRef(postId).get().addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    editPost  = task.getResult().toObject(Post.class);
                    if(editPost == null) return;
                    postMode = editPost.getPostMode();
                    setStatusPost();
                    binding.description.setText(editPost.getContent());
                    editedImageList = new ArrayList<>(editPost.getPostImages());
                    postPhotoAdapter.setEditedImageList(editedImageList);
                    postPhotoAdapter.notifyDataSetChanged();
                }
            });
        }
        imageList = new ArrayList<>();
        postPhotoAdapter = new PostPhotoAdapter(this, imageList,true);
        postPhotoAdapter.setEditedImageList(new ArrayList<>());
        LinearLayoutManager linearLayoutManager = new GridLayoutManager(getApplicationContext(), 3);
        binding.imageRecyclerView.setLayoutManager(linearLayoutManager);
//        binding.imageRecyclerView.setHasFixedSize(true);
        binding.imageRecyclerView.setAdapter(postPhotoAdapter);
        postPhotoAdapter.setOnItemClickListener(this);
        postRepo = PostRepository.getInstance();
        binding.close.setOnClickListener(v -> finish());

        binding.description.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(binding.description.getText().length() > 0 ){
                    binding.post.setBackgroundResource(R.drawable.canpost);
                    int text = binding.description.getText().length();
                    binding.numberOfText.setText(text+"/1000");
                    canPost = true;
                }
                else {
                    binding.post.setBackgroundResource(R.drawable.cantpost);
                    binding.numberOfText.setText("0/1000");
                    canPost = false;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        binding.post.setOnClickListener(v -> {
            if(canPost && !isEdit){
                binding.progressBar.setVisibility(View.VISIBLE);
                binding.contentLayout.setVisibility(View.GONE);
               Task<Boolean> task = postRepo.addNewPost(imageList, binding.description.getText().toString(), postMode, getApplicationContext());
               task.addOnCompleteListener(new OnCompleteListener<Boolean>() {
                   @Override
                   public void onComplete(@NonNull Task<Boolean> task) {
                       if(task.isSuccessful()){
                           finish();
                       }
                       else{
                           binding.progressBar.setVisibility(View.GONE);
                           binding.contentLayout.setVisibility(View.VISIBLE);
                       }

                   }
               }).addOnFailureListener(new OnFailureListener() {
                   @Override
                   public void onFailure(@NonNull Exception e) {
                       binding.progressBar.setVisibility(View.GONE);
                       binding.contentLayout.setVisibility(View.VISIBLE);

                   }
               });
            }
            else{
                binding.progressBar.setVisibility(View.VISIBLE);
                binding.contentLayout.setVisibility(View.GONE);
                Task<Boolean> task =postRepo.editPost(editedImageList,imageList, binding.description.getText().toString(), postMode, getApplicationContext(), postId);

                task.addOnCompleteListener(new OnCompleteListener<Boolean>() {
                    @Override
                    public void onComplete(@NonNull Task<Boolean> task) {
                        if(task.isSuccessful()){
                            setResult(REQUEST_EDITPOST_CODE);
                            finish();
                        }
                        else{
                            binding.progressBar.setVisibility(View.GONE);
                            binding.contentLayout.setVisibility(View.VISIBLE);
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        binding.progressBar.setVisibility(View.GONE);
                        binding.contentLayout.setVisibility(View.VISIBLE);

                    }
                });

//                setResult(REQUEST_EDITPOST_CODE);
//                finish();
            }
        });

        binding.rightArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PostModeFragment postModeFragment = PostModeFragment.getInstance(new PostModeFragment.PostModeFragmentListener() {
                    @Override
                    public void onSelectItem(PostMode postMode1) {
                       postMode = postMode1;
                       setStatusPost();
                    }
                }, postMode);
                Bundle bundle = new Bundle();
                bundle.putSerializable("postmode", postMode);
                postModeFragment.setArguments(bundle);
                postModeFragment.show(getSupportFragmentManager(), "PostModelDialog");
            }
        });
    }
    private void setStatusPost(){
        if(postMode == PostMode.PUBLIC){
            binding.status.setText("Công khai");
        }
        else if(postMode == PostMode.FRIEND){
            binding.status.setText("Bạn bè");
        }
        else if(postMode == PostMode.PRIVATE){
            binding.status.setText("Chỉ mình tôi");
        }
    }

    private ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Intent data = result.getData();
                        if (data.getClipData() != null) {
                            int cout =data.getClipData().getItemCount();
                            for (int i = 0; i < cout; i++) {
                                Uri imageurl = data.getClipData().getItemAt(i).getUri();
                                imageList.add(imageurl);
                            }
                        }
                        else {
                            Uri imageurl = data.getData();
                            imageList.add(imageurl);
                        }
                        postPhotoAdapter.notifyDataSetChanged();
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "You haven't picked Image", Toast.LENGTH_LONG).show();
                    }
                }
            }
    );
    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent();
        System.out.print("Click on button");
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        launcher.launch(intent);
    }
}
