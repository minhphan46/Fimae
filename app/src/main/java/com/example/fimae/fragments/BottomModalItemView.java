package com.example.fimae.fragments;

// CustomBottomModalItemView.java (Java)

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.fimae.R;

public class BottomModalItemView extends ConstraintLayout {
    private ImageView iconImageView;
    private TextView textTextView;

    private OnItemClickListener itemClickListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick();
    }

    public BottomModalItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        View view = inflate(context, R.layout.bottom_modal_item, this);
        iconImageView = view.findViewById(R.id.iconView);
        textTextView = view.findViewById(R.id.textView);
        ConstraintLayout layout = view.findViewById(R.id.bottom_model_item);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs, R.styleable.BottomModalItem, 0, 0);

        layout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("TESt", "onClick: ");
                if (itemClickListener != null) {
                    itemClickListener.onItemClick();
                }
            }
        });

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
