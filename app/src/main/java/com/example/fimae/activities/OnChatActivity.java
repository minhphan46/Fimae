package com.example.fimae.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.fimae.R;
import com.example.fimae.adapters.BottomSheetItemAdapter;
import com.example.fimae.adapters.MessageAdapter;
import com.example.fimae.fragments.ChatBottomSheetFragment;
import com.example.fimae.models.BottomSheetItem;
import com.example.fimae.models.Fimaers;
import com.example.fimae.models.Message;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class OnChatActivity extends AppCompatActivity {
    List<BottomSheetItem> bottomSheetItemList = new ArrayList<BottomSheetItem>(){
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ArrayList<Message> messages = new ArrayList<>();
        setContentView(R.layout.activity_on_chat);
        String conversationId = String.valueOf(getIntent().getStringExtra("conversationId"));
        messagesCol = FirebaseFirestore.getInstance().collection("conversations").document(conversationId).collection("messages");
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.chat_second_menu);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setTitle("");
        }
        RecyclerView recyclerView = findViewById(R.id.list_messages);
        MessageAdapter messageAdapter = new MessageAdapter(this, messages);
        System.out.println("Data goc la day: " + Message.dummy);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setAdapter(messageAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.scrollToPosition(messageAdapter.getItemCount() - 1);
        ImageView btnPlus = findViewById(R.id.btn_add);
        LinearLayout linearLayout = findViewById(R.id.input_media_layout);
        linearLayout.setVisibility(View.GONE);

        btnPlus.setOnClickListener(v -> {
            if(linearLayout.getVisibility() == View.GONE){
                linearLayout.setVisibility(View.VISIBLE);
            } else {
                linearLayout.setVisibility(View.GONE);
            }
        });
        ImageView btnEmoji = findViewById(R.id.btn_emoji);
        EditText editText = findViewById(R.id.et_input);
        btnEmoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, Object> messReq = new HashMap<>();
                DocumentReference documentReference = messagesCol.document();
                messReq.put("conversationId", conversationId);
                messReq.put("idSender", FirebaseAuth.getInstance().getUid());
                messReq.put("content", editText.getText().toString());
                messReq.put("timeSent", new Date());
                Message message = new Message(documentReference.getId(), editText.getText().toString(), new Date());
               messages.add(message);
                messageAdapter.notifyItemInserted(Message.dummy.size() - 1);
                recyclerView.scrollToPosition(messageAdapter.getItemCount() - 1);
                messagesCol.document().set(messReq).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        int i = messages.lastIndexOf(message);
                        messageAdapter.notifyItemRemoved(i);
                        recyclerView.scrollToPosition(messageAdapter.getItemCount() - 1);
                    }
                });

                editText.setText("");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    editText.setFocusable(View.NOT_FOCUSABLE);
                }
            }
        });
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
        messagesCol.addSnapshotListener((value, error) -> {
            if (error != null) {
                // Xử lý lỗi
                return;
            }
            messages.clear();
            // Lặp qua các tài liệu (tin nhắn) và thêm vào danh sách
            for (QueryDocumentSnapshot document : value) {
                System.out.println(document.toString());
                Message message = document.toObject(Message.class);
                messages.add(message);
            }
            messages.sort(Comparator.comparing(Message::getTimeSent));
            messageAdapter.notifyDataSetChanged();
        });
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivity(takePictureIntent);
    }
    private void dispatchPickImageFromGalleryIntent() {
        Intent pickImageIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickImageIntent, REQUEST_IMAGE_GALLERY);
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
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
        } else if(id == R.id.option_more) {
            ChatBottomSheetFragment chatBottomSheetFragment = new ChatBottomSheetFragment(bottomSheetItemList,
                    new BottomSheetItemAdapter.IClickBottomSheetItemListener() {
                        @Override
                        public void onClick(BottomSheetItem bottomSheetItem) {
                            Toast.makeText(getBaseContext(), bottomSheetItem.getTitle(), Toast.LENGTH_SHORT).show();
                        }
                    });
            chatBottomSheetFragment.show(getSupportFragmentManager(),chatBottomSheetFragment.getTag());
        } else if (id == R.id.option_call){
            FirebaseAuth.getInstance().signOut();
            System.out.println("Click success");
            startActivity(new Intent(this, AuthenticationActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

}
