package com.example.fimae.fragments;

// CustomBottomModalItemView.java (Java)

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.fimae.R;

public class BottomModalItemView extends ConstraintLayout {
    private ImageView iconImageView;
    private TextView textTextView;

    public BottomModalItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        View view = inflate(context, R.layout.bottom_modal_item, this);
        iconImageView = view.findViewById(R.id.iconView);
        textTextView = view.findViewById(R.id.textView);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs, R.styleable.BottomModalItem, 0, 0);

        try {
            // Get the iconSrc attribute and set it as the ImageView's source
            int iconSrcResId = a.getResourceId(R.styleable.BottomModalItem_iconSrc, 0);
            if (iconSrcResId != 0) {
                iconImageView.setImageResource(iconSrcResId);
            }

            // Get the textString attribute and set it as the TextView's text
            String textString = a.getString(R.styleable.BottomModalItem_textString);
            if (textString != null) {
                textTextView.setText(textString);
            }
        } finally {
            a.recycle();
        }
    }
}
