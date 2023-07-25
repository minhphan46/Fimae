package com.example.fimae.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.example.fimae.R;
import com.google.android.material.button.MaterialButton;

import de.hdodenhof.circleimageview.CircleImageView;

public class DatingMatchDialog extends DialogFragment {
    CircleImageView myImage, otherImage;
    CardView accept;
    MaterialButton decline;
    TextView datingMatchText;

    public interface ButtonListener {
        void onAcceptClick();

        void onDeclineClick();
    }

    private ButtonListener listener;

    String myImageUrl, otherImageUrl;

    String otherName;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        myImageUrl = getArguments().getString("myImageUrl");
        otherImageUrl = getArguments().getString("otherImageUrl");
        otherName = getArguments().getString("otherName");
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_dating_match, null);
        datingMatchText = view.findViewById(R.id.tv_match_title);
        datingMatchText.setText("Bạn và " + otherName + " đã thích lẫn nhau. Hãy bắt đầu trò chuyện với nhau để tìm hiểu nhau nhiều hơn nhé!");
        accept = view.findViewById(R.id.cv_start_conversation);
        decline = view.findViewById(R.id.btn_continue);
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onAcceptClick();
                dismiss();
            }
        });
        decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onDeclineClick();
                dismiss();
            }
        });
        myImage = view.findViewById(R.id.civ_my_image);
        otherImage = view.findViewById(R.id.civ_other_image);
        Glide.with(getContext()).load(myImageUrl).into(myImage);
        Glide.with(getContext()).load(otherImageUrl).into(otherImage);
        dialog.setContentView(view);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        return dialog;
    }
    //Create builder
    static public class Builder {
        private String myImageUrl, otherImageUrl, otherName;
        private ButtonListener listener;

        public Builder setMyImageUrl(String myImageUrl) {
            this.myImageUrl = myImageUrl;
            return this;
        }

        public Builder setOtherImageUrl(String otherImageUrl) {
            this.otherImageUrl = otherImageUrl;
            return this;
        }

        public Builder setOtherName(String otherName) {
            this.otherName = otherName;
            return this;
        }

        public Builder setListener(ButtonListener listener) {
            this.listener = listener;
            return this;
        }

        public DatingMatchDialog build() {
            DatingMatchDialog dialog = new DatingMatchDialog();
            Bundle bundle = new Bundle();
            bundle.putString("myImageUrl", myImageUrl);
            bundle.putString("otherImageUrl", otherImageUrl);
            bundle.putString("otherName", otherName);
            dialog.setArguments(bundle);
            dialog.listener = listener;
            return dialog;
        }
    }
    static public Builder builder() {
        return new Builder();
    }
}
