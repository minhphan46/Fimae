package com.example.fimae.components;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fimae.R;
import com.example.fimae.activities.DetailPostActivity;
import com.example.fimae.activities.MediaSliderActivity;
import com.example.fimae.adapters.ImageMessageAdapter;
import com.example.fimae.models.Fimaers;
import com.example.fimae.models.Message;
import com.example.fimae.repository.PostRepository;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageView extends ConstraintLayout {
    private Fimaers fimaers;
    private Message message;
    CircleImageView avatar;
    LinearLayout layout;
    PostMessage postMessage;
    TextView textView;
    RecyclerView recyclerView;
    LinearLayout linearLayoutContent;

    private void initView(Context context) {
        View view = inflate(context, R.layout.receiver_message, this);
        //Set width match parent
        view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        layout = view.findViewById(R.id.layout_message);
        avatar = view.findViewById(R.id.avatar_image_view);
        postMessage = view.findViewById(R.id.post_message);
        textView = view.findViewById(R.id.incoming_msg);
        recyclerView = view.findViewById(R.id.list_images);
        linearLayoutContent = view.findViewById(R.id.layout_message_content);
    }

    public MessageView(@NonNull Context context) {
        super(context);
        initView(context);
    }

    public MessageView(@NonNull Context context, Message message, Fimaers fimaers) {
        super(context);
        initView(context);
        setMessageAndFimaer(message, fimaers);
    }

    public MessageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public MessageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    public MessageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context);
    }

    public void setMessage(Message message) {
        this.message = message;
        if (Objects.equals(message.getIdSender(), FirebaseAuth.getInstance().getUid())) {
            layout.setForegroundGravity(Gravity.END);
            layout.setGravity(Gravity.END);
            avatar.setVisibility(GONE);
            linearLayoutContent.setGravity(Gravity.END);
            //Delete background tint
            textView.setBackgroundTintList(null);
            textView.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        }
        if (Objects.equals(message.getType(), Message.TEXT)) {
            this.textView.setText(message.getContent().toString());
            this.textView.setVisibility(VISIBLE);
            this.postMessage.setVisibility(GONE);
            this.recyclerView.setVisibility(GONE);
        } else if (Objects.equals(message.getType(), Message.MEDIA)) {
            this.textView.setVisibility(GONE);
            this.postMessage.setVisibility(GONE);
            this.recyclerView.setVisibility(VISIBLE);
            ArrayList<String> urls = (ArrayList<String>) message.getContent();
            ImageMessageAdapter imageMessageAdapter = new ImageMessageAdapter(urls);
            imageMessageAdapter.setOnClickMediaItem((url, position) -> {
                Intent intent = new Intent(getContext(), MediaSliderActivity.class);
                intent.putExtra("urls", urls);
                intent.putExtra("currentIndex", position);
                getContext().startActivity(intent);
            });
            int col = calculateGridColumns(urls.size());
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), col);
            recyclerView.setLayoutManager(gridLayoutManager);
            recyclerView.setAdapter(imageMessageAdapter);
        } else if (Objects.equals(message.getType(), Message.POST_LINK)) {
            this.textView.setVisibility(GONE);
            this.postMessage.setVisibility(VISIBLE);
            this.recyclerView.setVisibility(GONE);
            PostRepository.getInstance().getPostById(message.getContent().toString()).addOnSuccessListener(post -> {
                if (post.getPostImages() != null && !post.getPostImages().isEmpty()) {
                    postMessage.setImage(post.getPostImages().get(0));
                } else {
                    postMessage.setImageVisibility(View.GONE);
                }
                String content = post.getContent();
                postMessage.setTitle(content);
            });
            postMessage.setLabelButton("XEM BÀI VIẾT");
            postMessage.setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), DetailPostActivity.class);
                intent.putExtra("id", message.getContent().toString());
                getContext().startActivity(intent);
            });
        }
    }

    public void setFimaers(Fimaers fimaers) {
        this.fimaers = fimaers;
        if (!Objects.equals(message.getIdSender(), FirebaseAuth.getInstance().getUid())) {
            Glide.with(getContext()).load(fimaers.getAvatarUrl()).into(avatar).onLoadFailed(ContextCompat.getDrawable(getContext(), R.drawable.avatar));
        }
    }

    public void setMessageAndFimaer(Message message, Fimaers fimaers) {
        setMessage(message);
        setFimaers(fimaers);
    }

    private int calculateGridColumns(int urlsSize) {
        if (urlsSize == 1) {
            return 1;
        } else if (urlsSize % 2 == 0 && urlsSize <= 4) {
            return 2;
        } else {
            return 3;
        }
    }
}
