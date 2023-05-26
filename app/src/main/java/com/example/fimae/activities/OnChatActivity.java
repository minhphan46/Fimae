package com.example.fimae.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.fimae.MediaListDialogFragment;
import com.example.fimae.R;
import com.example.fimae.adapters.BottomSheetItemAdapter;
import com.example.fimae.adapters.MessageAdapter;
import com.example.fimae.fragments.ChatBottomSheetFragment;
import com.example.fimae.models.BottomSheetItem;
import com.example.fimae.models.Message;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;
import com.google.firebase.storage.FirebaseStorage;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class OnChatActivity extends AppCompatActivity {
    List<BottomSheetItem> bottomSheetItemList = new ArrayList<BottomSheetItem>() {
        {
            add(new BottomSheetItem(R.drawable.ic_share, "Chia sẽ liên hệ"));
            add(new BottomSheetItem(R.drawable.ic_gallery, "Tất cả ảnh"));
            add(new BottomSheetItem(R.drawable.ic_edit, "Chỉnh sửa biệt danh"));
            add(new BottomSheetItem(R.drawable.ic_search, "Tìm kiếm lịch sử cuộc trò chuyện"));
            add(new BottomSheetItem(R.drawable.ic_user_block, "Chặn"));
            add(new BottomSheetItem(R.drawable.ic_chat_dots, "Báo cáo"));
        }
    };
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_GALLERY = 2;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chat_second_menu, menu);
        return true;
    }

    private CollectionReference messagesCol;
    private String conversationId;
    EditText textInput;
    RecyclerView recyclerView;
    MessageAdapter messageAdapter;
    LinearLayoutManager linearLayoutManager;
    ArrayList<Message> messages;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        messages = new ArrayList<>();
        setContentView(R.layout.activity_on_chat);
        conversationId = String.valueOf(getIntent().getStringExtra("conversationID"));
        System.out.println("=============" + conversationId+"==================");
        messagesCol = FirebaseFirestore.getInstance().collection("conversations").document(conversationId).collection("messages");
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.chat_second_menu);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setTitle("");
        }
         recyclerView = findViewById(R.id.list_messages);
         messageAdapter = new MessageAdapter(this, messages);
         linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setAdapter(messageAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.scrollToPosition(messageAdapter.getItemCount() - 1);
        ImageView btnPlus = findViewById(R.id.btn_add);
        LinearLayout linearLayout = findViewById(R.id.input_media_layout);
        linearLayout.setVisibility(View.GONE);

        btnPlus.setOnClickListener(v -> {
            if (linearLayout.getVisibility() == View.GONE) {
                linearLayout.setVisibility(View.VISIBLE);
            } else {
                linearLayout.setVisibility(View.GONE);
            }
        });
        ImageView btnEmoji = findViewById(R.id.btn_emoji);
         textInput = findViewById(R.id.et_input);
        btnEmoji.setOnClickListener(v -> sendTextMessage());
        ImageView btnCamera = findViewById(R.id.btn_camera);
        ImageView btnGallery = findViewById(R.id.btn_galllery);
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });
        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchPickImageFromGalleryIntent();
            }
        });
        messagesCol.orderBy("sentAt", Query.Direction.ASCENDING).addSnapshotListener((value, error) -> {
            if (error != null) {
                System.out.println(error.getMessage());
                System.out.println(error.getCode());
                return;
            }
            messages.clear();
            // Lặp qua các tài liệu (tin nhắn) và thêm vào danh sách
            for (QueryDocumentSnapshot document : value) {
                System.out.println(document.toString());
                Message message = document.toObject(Message.class);
                messages.add(message);
            }
            messageAdapter.notifyDataSetChanged();
            recyclerView.scrollToPosition(messageAdapter.getItemCount() - 1);
        });
        ImageView btnMicro = findViewById(R.id.btn_micro);
        btnMicro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MediaListDialogFragment().show(getSupportFragmentManager(), "dialog");
            }
        });
    }
    private void sendTextMessage(){
        DocumentReference messDoc = messagesCol.document();
        Message message = new Message();
        message.setId(messDoc.getId());
        message.setType(Message.TEXT);
        message.setSentAt(Timestamp.now());
        message.setContent( textInput.getText().toString());
        message.setIdSender(FirebaseAuth.getInstance().getUid());
        messages.add(message);
        messageAdapter.notifyItemInserted(messages.size() - 1);
        recyclerView.scrollToPosition(messageAdapter.getItemCount() - 1);
        messagesCol.document().set(message).addOnFailureListener(e -> {
            int i = messages.lastIndexOf(message);
            messageAdapter.notifyItemRemoved(i);
            recyclerView.scrollToPosition(messageAdapter.getItemCount() - 1);
        });
        textInput.setText("");
    }
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivity(takePictureIntent);
    }
    private static final int REQUEST_IMAGE = 1;
    private static final int REQUEST_VIDEO = 2;
    private void dispatchPickImageFromGalleryIntent() {
//        Intent pickImageIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        startActivityForResult(pickImageIntent, REQUEST_IMAGE_GALLERY);


    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            FirebaseStorage.getInstance().getReference("conversation-medias").child(conversationId);
            // Use the captured imageBitmap as needed
        }
        if (requestCode == REQUEST_IMAGE_GALLERY && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            // Use the selected imageUri as needed
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (id == R.id.option_more) {
            ChatBottomSheetFragment chatBottomSheetFragment = new ChatBottomSheetFragment(bottomSheetItemList,
                    new BottomSheetItemAdapter.IClickBottomSheetItemListener() {
                        @Override
                        public void onClick(BottomSheetItem bottomSheetItem) {
                            Toast.makeText(getBaseContext(), bottomSheetItem.getTitle(), Toast.LENGTH_SHORT).show();
                        }
                    });
            chatBottomSheetFragment.show(getSupportFragmentManager(), chatBottomSheetFragment.getTag());
        } else if (id == R.id.option_call) {
            FirebaseAuth.getInstance().signOut();
            System.out.println("Click success");
            startActivity(new Intent(this, AuthenticationActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

}
