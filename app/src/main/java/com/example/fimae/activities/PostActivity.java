package com.example.fimae.activities;

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

import java.util.ArrayList;
import java.util.List;
public class PostActivity extends AppCompatActivity implements View.OnClickListener {
    ViewDataBinding binding1;
    ImageView close;
    ImageView post;
    EditText description;
    RecyclerView imageRecyclerView;
    List<Uri> imageList;
    PostPhotoAdapter postPhotoAdapter;
    TextView numberOfText;
    ImageView rightArrow;
    TextView status;
    boolean canPost = false;
    private PostMode postMode = PostMode.PUBLIC;
    public void Click(){
        Intent intent = new Intent();
        System.out.print("Click on button");
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 2);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         binding1 = DataBindingUtil.setContentView(this, R.layout.activity_post);
        close = findViewById(R.id.close);
        status = findViewById(R.id.status);
        post = findViewById(R.id.post);
        numberOfText = findViewById(R.id.numberOfText);
        description = findViewById(R.id.description);
        imageRecyclerView = findViewById(R.id.image_recycler_view);
        rightArrow = findViewById(R.id.right_arrow);
        imageList = new ArrayList<>();
        postPhotoAdapter = new PostPhotoAdapter(this, imageList, true);
        LinearLayoutManager linearLayoutManager = new GridLayoutManager(getApplicationContext(), 3);
        imageRecyclerView.setLayoutManager(linearLayoutManager);
        imageRecyclerView.setHasFixedSize(true);
        imageRecyclerView.setAdapter(postPhotoAdapter);
        postPhotoAdapter.setOnItemClickListener(this);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PostActivity.this , MainActivity.class));
                finish();
            }
        });

        description.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(description.getText().length() > 0 ){
                    post.setBackgroundResource(R.drawable.canpost);
                    int text = description.getText().length();
                    numberOfText.setText(text+"/1000");
                    canPost = true;
                }
                else {
                    post.setBackgroundResource(R.drawable.cantpost);
                    numberOfText.setText("0/1000");
                    canPost = false;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(canPost){
                }
            }
        });

        rightArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PostModeFragment postModeFragment = PostModeFragment.getInstance(new PostModeFragment.PostModeFragmentListener() {
                    @Override
                    public void onSelectItem(PostMode postMode1) {
                       postMode = postMode1;
                       setStatusPost();
                    }
                });
                Bundle bundle = new Bundle();
                bundle.putSerializable("postmode", postMode);
                postModeFragment.setArguments(bundle);
                postModeFragment.show(getSupportFragmentManager(), "PostModelDialog");
            }
        });
    }
    private void setStatusPost(){
        if(postMode == PostMode.PUBLIC){
            status.setText("Công khai");
        }
        else if(postMode == PostMode.FRIEND){
            status.setText("Bạn bè");
        }
        else if(postMode == PostMode.PRIVATE){
            status.setText("Chỉ mình tôi");
        }
    }
    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();

        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2 && resultCode == RESULT_OK && null != data) {
            if (data.getClipData() != null) {
                int cout = data.getClipData().getItemCount();
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
        } else {
            Toast.makeText(this, "You haven't picked Image", Toast.LENGTH_LONG).show();
        }
    }
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
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 2);
    }
}
