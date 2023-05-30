package com.example.fimae.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.fimae.MediaListDialogFragment;
import com.example.fimae.R;
import com.example.fimae.adapters.BottomSheetItemAdapter;
import com.example.fimae.adapters.MessageAdapter;
import com.example.fimae.fragments.ChatBottomSheetFragment;
import com.example.fimae.models.BottomSheetItem;
import com.example.fimae.models.Message;
import com.example.fimae.utils.FileUtils;
import com.example.fimae.utils.FirebaseHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class OnChatActivity extends AppCompatActivity implements MediaListDialogFragment.OnMediaSelectedListener {
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_GALLERY = 2;

    private CollectionReference messagesCol;
    private String conversationId;
    private EditText textInput;
    private RecyclerView recyclerView;
    private MessageAdapter messageAdapter;
    private LinearLayoutManager linearLayoutManager;
    private ArrayList<Message> messages;
    private LinearLayout inputMediaLayout;
    private Uri photoUri;
    private List<BottomSheetItem> bottomSheetItemList;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chat_second_menu, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_chat);
        conversationId = getIntent().getStringExtra("conversationID");
        messagesCol = FirebaseFirestore.getInstance().collection("conversations").document(conversationId).collection("messages");
        initViews();
        initBottomSheetItems();
        initListeners();
        initMessagesListener();
    }

    private void initViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setTitle("");
        }
        recyclerView = findViewById(R.id.list_messages);

        linearLayoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(linearLayoutManager);
        ImageView btnAddMedia = findViewById(R.id.btn_add_media);
        inputMediaLayout = findViewById(R.id.input_media_layout);
        inputMediaLayout.setVisibility(View.GONE);
        ImageView btnSend = findViewById(R.id.btn_send);
        textInput = findViewById(R.id.et_input);
        ImageView btnCamera = findViewById(R.id.btn_camera);
        ImageView btnGallery = findViewById(R.id.btn_galllery);
        ImageView btnMicro = findViewById(R.id.btn_micro);
    }

    private void initBottomSheetItems() {
        bottomSheetItemList = new ArrayList<BottomSheetItem>() {
            {
                add(new BottomSheetItem(R.drawable.ic_share, "Chia sẽ liên hệ"));
                add(new BottomSheetItem(R.drawable.ic_gallery, "Tất cả ảnh"));
                add(new BottomSheetItem(R.drawable.ic_edit, "Chỉnh sửa biệt danh"));
                add(new BottomSheetItem(R.drawable.ic_search, "Tìm kiếm lịch sử cuộc trò chuyện"));
                add(new BottomSheetItem(R.drawable.ic_user_block, "Chặn"));
                add(new BottomSheetItem(R.drawable.ic_chat_dots, "Báo cáo"));
            }
        };
    }

    private void initListeners() {
        ImageView btnAddMedia = findViewById(R.id.btn_add_media);
        btnAddMedia.setOnClickListener(v -> {
            inputMediaLayout.setVisibility(inputMediaLayout.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
        });

        ImageView btnSend = findViewById(R.id.btn_send);
        btnSend.setOnClickListener(v -> {
            sendTextMessage();
        });

        ImageView btnCamera = findViewById(R.id.btn_camera);
        btnCamera.setOnClickListener(v -> {
            dispatchTakePictureIntent();
        });

        ImageView btnGallery = findViewById(R.id.btn_galllery);
        btnGallery.setOnClickListener(v -> {
            MediaListDialogFragment mediaListDialogFragment = new MediaListDialogFragment();
            mediaListDialogFragment.show(getSupportFragmentManager(), "dialog");
        });

        ImageView btnMicro = findViewById(R.id.btn_micro);

        // Bottom sheet menu
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            switch (id) {
                case R.id.option_more:
                    ChatBottomSheetFragment chatBottomSheetFragment = new ChatBottomSheetFragment(bottomSheetItemList,
                            bottomSheetItem -> Toast.makeText(this, bottomSheetItem.getTitle(), Toast.LENGTH_SHORT).show());
                    chatBottomSheetFragment.show(getSupportFragmentManager(), chatBottomSheetFragment.getTag());
                    return true;
                case R.id.option_call:
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(this, AuthenticationActivity.class));
                    return true;
                default:
                    return super.onOptionsItemSelected(item);
            }
        });
    }

    private void initMessagesListener() {
        messages = new ArrayList<>();
        messageAdapter = new MessageAdapter(this, messages);
        recyclerView.setAdapter(messageAdapter);
        messagesCol.orderBy("sentAt", Query.Direction.ASCENDING).addSnapshotListener((value, error) -> {
            if (error != null) {
                Log.e("OnChatActivity", "Error getting messages", error);
                return;
            }
            messages.clear();
            for (QueryDocumentSnapshot document : value) {
                Message message = document.toObject(Message.class);
                messages.add(message);
            }
            messageAdapter.notifyDataSetChanged();
            recyclerView.scrollToPosition(messageAdapter.getItemCount() - 1);
        });
    }

    private void sendTextMessage() {
        if(textInput.getText().toString().trim().isEmpty())
            return;
        DocumentReference messDoc = messagesCol.document();
        Message message = createTextMessage(messDoc.getId(), textInput.getText().toString());
        sendMessage(message, messDoc);
        textInput.setText("");
    }

    private void sendMediaMessage(ArrayList<Uri> uris) {
        inputMediaLayout.setVisibility(View.GONE);
        FirebaseHelper firebaseHelper = new FirebaseHelper();
        firebaseHelper.uploadMultipleFiles(uris, this::handleMediaMessageUpload, "conversation-medias", conversationId);
    }

    private void sendMediaMessage(byte[] bytes) {
        inputMediaLayout.setVisibility(View.GONE);
        FirebaseHelper firebaseHelper = new FirebaseHelper();
        firebaseHelper.uploadBytesToFirebase(bytes, this::handleMediaMessageUpload, "conversation-medias", conversationId);
    }

    private void handleMediaMessageUpload(ArrayList<String> downloadUrls) {
        Message message = createMediaMessage(downloadUrls);
        sendMessage(message);
        textInput.setText("");
    }

    private Message createTextMessage(String messageId, String content) {
        Message message = new Message();
        message.setId(messageId);
        message.setType(Message.TEXT);
        message.setSentAt(Timestamp.now());
        message.setContent(content);
        message.setIdSender(FirebaseAuth.getInstance().getUid());
        return message;
    }

    private Message createMediaMessage(ArrayList<String> downloadUrls) {
        Message message = new Message();
        message.setId(messagesCol.document().getId());
        message.setType(Message.MEDIA);
        message.setSentAt(Timestamp.now());
        message.setContent(downloadUrls);
        message.setConversationID(conversationId);
        message.setIdSender(FirebaseAuth.getInstance().getUid());
        return message;
    }
    private void sendMessage(Message message) {
        messages.add(message);
        messageAdapter.notifyItemInserted(messages.size() - 1);
        recyclerView.scrollToPosition(messageAdapter.getItemCount() - 1);
        messagesCol.document(message.getId()).set(message)
                .addOnFailureListener(e -> {
                    int i = messages.lastIndexOf(message);
                    messageAdapter.notifyItemRemoved(i);
                    recyclerView.scrollToPosition(messageAdapter.getItemCount() - 1);
                });
    }

    private void sendMessage(Message message, DocumentReference messDoc) {
        messages.add(message);
        messageAdapter.notifyItemInserted(messages.size() - 1);
        recyclerView.scrollToPosition(messageAdapter.getItemCount() - 1);
        messDoc.set(message)
                .addOnFailureListener(e -> {
                    int i = messages.lastIndexOf(message);
                    messageAdapter.notifyItemRemoved(i);
                    recyclerView.scrollToPosition(messageAdapter.getItemCount() - 1);
                });
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            if (data.getExtras() != null) {
                Bitmap photoBitmap = (Bitmap) data.getExtras().get("data");
                byte[] bitmapBytes = FileUtils.getBytesFromBitmap(photoBitmap, 100);
                sendMediaMessage(bitmapBytes);
            }

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
                    bottomSheetItem -> Toast.makeText(getBaseContext(), bottomSheetItem.getTitle(), Toast.LENGTH_SHORT).show());
            chatBottomSheetFragment.show(getSupportFragmentManager(), chatBottomSheetFragment.getTag());
        } else if (id == R.id.option_call) {
            FirebaseAuth.getInstance().signOut();
            System.out.println("Click success");
            startActivity(new Intent(this, AuthenticationActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void OnMediaSelected(boolean isSelected, ArrayList<String> data) {
        if (isSelected) {
            ArrayList<Uri> uris = new ArrayList<>();
            data.forEach(e -> uris.add(Uri.parse(e)));
            sendMediaMessage(uris);
        }
    }
}
