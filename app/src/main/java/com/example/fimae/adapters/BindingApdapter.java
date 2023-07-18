package com.example.fimae.adapters;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import com.squareup.picasso.Picasso;

public class BindingApdapter {
    @BindingAdapter(value={"imageUrl", "placeholder"}, requireAll=false)
    public static void setImageUrl(ImageView imageView, String url, Drawable placeHolder) {
        Picasso.get().load(url).placeholder(placeHolder).into(imageView);
    }
}
