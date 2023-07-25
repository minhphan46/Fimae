package com.example.fimae.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.example.fimae.R;

public class ListTile extends ConstraintLayout {
    ImageView leadingIcon;
    ImageView trailingIcon;
    TextView title;
    TextView subtitle;
    private void initView(Context context) {
        View view = inflate(context, R.layout.bottomsheet_item, this);
        leadingIcon = view.findViewById(R.id.leading_icon);
        trailingIcon = view.findViewById(R.id.trailing_icon);
        title = view.findViewById(R.id.tv_title);
        subtitle = view.findViewById(R.id.tv_subtitle);
        title.setTextColor(ContextCompat.getColor(context, R.color.black));
        subtitle.setTextColor(ContextCompat.getColor(context, R.color.black));
        view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
    }
    public void initTypedArray(Context context ,TypedArray typedArray){
        int leadingIconResId = typedArray.getResourceId(R.styleable.ListTile_leadingIcon, 0);
        int trailingIconResId = typedArray.getResourceId(R.styleable.ListTile_trailingIcon, 0);
        String titleText = typedArray.getString(R.styleable.ListTile_title);
        String subtitleText = typedArray.getString(R.styleable.ListTile_subtitle);
        boolean trailingIconVisibility = typedArray.getBoolean(R.styleable.ListTile_trailingIconVisibility, true);
        boolean leadingIconVisibility = typedArray.getBoolean(R.styleable.ListTile_leadingIconVisibility, true);
        boolean subtitleVisibility = typedArray.getBoolean(R.styleable.ListTile_subtitleVisibility, true);
        if (leadingIconResId != 0)
            leadingIcon.setImageResource(leadingIconResId);
        if (trailingIconResId != 0)
            trailingIcon.setImageResource(trailingIconResId);
        if (titleText != null)
            title.setText(titleText);
        if (subtitleText != null)
            subtitle.setText(subtitleText);
        subtitle.setVisibility(subtitleVisibility ? VISIBLE : GONE);
        leadingIcon.setVisibility(leadingIconVisibility ? VISIBLE : GONE);
        trailingIcon.setVisibility(trailingIconVisibility ? VISIBLE : GONE);
        typedArray.recycle();
    }
    public ListTile(@NonNull Context context) {
        super(context);
        initView(context);
    }
    public ListTile(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
        initTypedArray(context, context.obtainStyledAttributes(attrs, R.styleable.ListTile));

    }

    public ListTile(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
        initTypedArray(context, context.obtainStyledAttributes(attrs, R.styleable.ListTile));
    }

    public ListTile(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context);
        initTypedArray(context, context.obtainStyledAttributes(attrs, R.styleable.ListTile));
    }

    public void setLeadingIcon(int resId) {
        leadingIcon.setImageResource(resId);
    }
    public void setTrailingIcon(int resId) {
        trailingIcon.setImageResource(resId);
    }
    public void setTitle(String text) {
        title.setText(text);
    }
    public void setSubtitle(String text) {
        subtitle.setText(text);
    }
    public void setOnClickListener(OnClickListener onClickListener) {
        this.setOnClickListener(onClickListener);
    }
    public void setLeadingIconVisibility(int visibility) {
        leadingIcon.setVisibility(visibility);
    }
    public void setTrailingIconVisibility(int visibility) {
        trailingIcon.setVisibility(visibility);
    }
    public void setTitleVisibility(int visibility) {
        title.setVisibility(visibility);
    }
    public void setSubtitleVisibility(int visibility) {
        subtitle.setVisibility(visibility);
    }
}
