package com.example.fimae.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.view.ContextThemeWrapper;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.BindingAdapter;

import com.example.fimae.R;
import com.example.fimae.activities.EditProfileActivity;
import com.example.fimae.viewmodels.EditProfileViewModel;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class BindingApdapter {
    @BindingAdapter(value={"imageUrl", "placeholder"}, requireAll=false)
    public static void setImageUrl(ImageView imageView, String url, Drawable placeHolder) {
        Picasso.get().load(url).placeholder(placeHolder).into(imageView);
    }
    @BindingAdapter("android:visibility")
    public static void setVisibility(View view, Boolean value) {
        view.setVisibility(value ? View.VISIBLE : View.GONE);
    }


    @BindingAdapter(value = {"chipsData", "isChoiceChip"}, requireAll = false)
    public static void setChipsData(ChipGroup chipGroup, ArrayList<String> dataList, boolean isChoiceChip) {
        chipGroup.removeAllViews();
        if (dataList != null && !dataList.isEmpty()) {
            LayoutInflater inflater = LayoutInflater.from(chipGroup.getContext());
            for (String item : dataList) {
                Chip chip;
                if(isChoiceChip)
                {
                    chip = (Chip) inflater.inflate(R.layout.choice_chip_item_layout, chipGroup, false);
                }
                else
                {
                    chip = (Chip) inflater.inflate(R.layout.chip_item_layout, chipGroup, false);
                }
                chip.setText(item);
                chipGroup.addView(chip);
            }
        }
    }
    @BindingAdapter(value = {"chipsData", "viewModel", "chipType", "checkedChips"}, requireAll = false)
    public static void setChipsData(ChipGroup chipGroup, ArrayList<String> chipsData, EditProfileViewModel viewModel, String chipType, ArrayList<String> checkedChips) {
        chipGroup.removeAllViews();
        List<String> dataList = chipsData;
        if (dataList != null && !dataList.isEmpty()) {
            LayoutInflater inflater = LayoutInflater.from(chipGroup.getContext());
                if(chipType == null) chipType = "";
                switch (chipType)
                {
                    case "choice":
                        for (String item : dataList) {
                            Chip chip;
                            chip = (Chip) inflater.inflate(R.layout.choice_chip_item_layout, chipGroup, false);

                            if(checkedChips != null)
                                chip.setChecked(checkedChips.contains(item));
                            chip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                                    viewModel.hasChange = true;
                                    viewModel.chipClick(compoundButton.getText().toString(),b);
                                }
                            });
                            chip.setText(item);
                            chipGroup.addView(chip);
                        }
                        break;
                    case "input":
                        if(checkedChips == null || checkedChips.isEmpty()) break;
                        for (String item : checkedChips) {
                            Chip chip;
                            chip = (Chip) inflater.inflate(R.layout.input_chip_item_layout, chipGroup, false);
                            chip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                                    chipGroup.removeView(chip);
                                    viewModel.chipClick(compoundButton.getText().toString(),false);
                                }
                            });
                            chip.setText(item);
                            chipGroup.addView(chip);
                        }
                        break;
                    default:
                        int height = 0;
                        for (String item : dataList) {
                            Chip chip;
                            chip = (Chip) inflater.inflate(R.layout.chip_item_layout, chipGroup, false);
                            chip.setText(item);
                            chipGroup.addView(chip);
                            height = (int) chip.getChipMinHeight();
                        }
                        ImageView imageView = new ImageView(chipGroup.getContext());
                        imageView.setImageResource(R.drawable.ic_edit); // Replace "your_image_resource" with the actual resource ID of the image you want to display
                        imageView.setLayoutParams(new ViewGroup.LayoutParams(
                                height,
                                height
                        ));
                        int realHeight =(int) chipGroup.getResources().getDimension(R.dimen.image_size);

                        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                        int padding = (int) (height - realHeight) / 2;
                        imageView.setPadding(padding, padding, padding, padding);
                        imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(imageView.getContext(), EditProfileActivity.class);
                                imageView.getContext().startActivity(intent);
                            }
                        });
                        // Add the ImageView to the chipGroup
                        chipGroup.addView(imageView);

                }

        }
    }
}