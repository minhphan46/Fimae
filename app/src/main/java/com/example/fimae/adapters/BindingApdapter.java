package com.example.fimae.adapters;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import com.example.fimae.R;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class BindingApdapter {
    @BindingAdapter(value={"imageUrl", "placeholder"}, requireAll=false)
    public static void setImageUrl(ImageView imageView, String url, Drawable placeHolder) {
        Picasso.get().load(url).placeholder(placeHolder).into(imageView);
    }
    @BindingAdapter("android:visibility")
    public static void setVisibility(View view, Boolean value) {
        view.setVisibility(value ? View.VISIBLE : View.GONE);
    }
    @BindingAdapter("chipsData")
    public static void setChipsData(ChipGroup chipGroup, ArrayList<String> dataList) {
        chipGroup.removeAllViews();
        if (dataList != null && !dataList.isEmpty()) {
            LayoutInflater inflater = LayoutInflater.from(chipGroup.getContext());
            for (String item : dataList) {
                Chip chip = (Chip) inflater.inflate(R.layout.chip_item_layout, chipGroup, false);
                chip.setText(item);
                chipGroup.addView(chip);
            }
        }
    }
}
