package com.example.fimae.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.fimae.R;

public class PostMessage extends CardView {
    ConstraintLayout layout;
    private ImageView imageView;
    private TextView title;
    private TextView viewButton;
    private boolean isSender;
    private OnClickListener onClickListener;

    public PostMessage(@NonNull Context context) {
        super(context);
    }

    private void initView(Context context) {
        View view = inflate(context, R.layout.post_link_sender, this);
        //Set width match parent
        view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        layout = view.findViewById(R.id.post_message_layout);
        title = view.findViewById(R.id.post_message_title);
        imageView = view.findViewById(R.id.post_image);
        viewButton = view.findViewById(R.id.label_post_view);
    }

    private void initTypedArray(Context context, TypedArray typedArray) {
        if (typedArray.hasValue(R.styleable.PostMessage_title)) {
            title.setText(typedArray.getString(R.styleable.PostMessage_title));
        }
        if (typedArray.hasValue(R.styleable.PostMessage_label_button)) {
            viewButton.setText(typedArray.getString(R.styleable.PostMessage_label_button));
        }
        if (typedArray.hasValue(R.styleable.PostMessage_image)) {
            imageView.setImageResource(typedArray.getResourceId(R.styleable.PostMessage_image, 0));
        }
        if (!typedArray.getBoolean(R.styleable.PostMessage_isSender, true)) {
            layout.setBackgroundColor(ContextCompat.getColor(context, R.color.gray_400));
            title.setTextColor(ContextCompat.getColor(context, R.color.black));
            viewButton.setTextColor(ContextCompat.getColor(context, R.color.black));
        }
        typedArray.recycle();
    }

    public PostMessage(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PostMessage);
        initTypedArray(context, typedArray);
    }

    public PostMessage(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PostMessage);
        initTypedArray(context, typedArray);
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
        viewButton.setOnClickListener(onClickListener);
    }

    public void setTitle(String title) {
        this.title.setText(title);
    }

    public void setLabelButton(String labelButton) {
        this.viewButton.setText(labelButton);
    }

    public void setImage(String image) {
        Glide.with(getContext()).load(image).into(imageView);
    }
    public void setImageVisibility(int visibility) {
        imageView.setVisibility(visibility);
    }
    public void setSender(boolean sender) {
        isSender = sender;
        if (!isSender) {
            layout.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.gray_400));
            title.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
            viewButton.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
        }
    }
}
