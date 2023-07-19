package com.example.fimae.bottomdialogs;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;

import com.example.fimae.R;
import com.example.fimae.databinding.FragmentAvatarBottomSheetBinding;
import com.example.fimae.fragments.BottomModalItemView;
import com.example.fimae.utils.FileUtils;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PickImageBottomSheetFragment extends BottomSheetDialogFragment
{
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PICK = 2;

    private PickImageCallBack callBack;

    private Uri photoUri;

    public void setCallBack(PickImageCallBack callBack)
    {
        this.callBack = callBack;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_image_bottom_modal,container,false);
        BottomModalItemView camera = view.findViewById(R.id.takePhoto);
        BottomModalItemView galery = view.findViewById(R.id.choose_image);

        camera.setOnItemClickListener(new BottomModalItemView.OnItemClickListener() {
            @Override
            public void onItemClick() {
                dispatchTakePictureIntent();
            }
        });
        galery.setOnItemClickListener(new BottomModalItemView.OnItemClickListener() {
            @Override
            public void onItemClick() {
                pickImageFromGallery();
            }
        });

        return view;
    }
    private void dispatchTakePictureIntent() {
        long time = System.currentTimeMillis();
        String stvalue = Long.toString(time);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        File output = new File(dir, stvalue + ".jpg"); // Append ".jpg" to the file name to make it a valid image file
        photoUri = FileProvider.getUriForFile(getContext(), "com.example.fimae.provider", output);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
    }

    public interface PickImageCallBack {

        public void pickImageComplete(Uri uri);
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK) {
            if (data != null) {
                callBack.pickImageComplete(data.getData());
                dismiss();
            }
        }
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            if (data != null) {
                callBack.pickImageComplete(photoUri);
                dismiss();
            }
        }
    }
}
