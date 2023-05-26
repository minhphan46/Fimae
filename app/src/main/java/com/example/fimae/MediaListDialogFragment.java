package com.example.fimae;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.database.MergeCursor;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ImageView;
import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.fimae.databinding.FragmentItemListDialogListDialogItemBinding;
import com.example.fimae.databinding.FragmentItemListDialogListDialogBinding;
import kotlin.text.Regex;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

import static android.telecom.VideoProfile.isVideo;

/**
 * <p>A fragment that shows a list of items as a modal bottom sheet.</p>
 * <p>You can show this modal bottom sheet from your activity like this:</p>
 * <pre>
 *     MediaListDialogFragment.newInstance(30).show(getSupportFragmentManager(), "dialog");
 * </pre>
 */
public class MediaListDialogFragment extends BottomSheetDialogFragment {

    // TODO: Customize parameter argument names
    private static final String ARG_ITEM_COUNT = "item_count";
    private FragmentItemListDialogListDialogBinding binding;

    // TODO: Customize parameters



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentItemListDialogListDialogBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        final RecyclerView recyclerView = (RecyclerView) view;
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        recyclerView.setAdapter(new MediaAdapter());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView tvPickedIndex;
        TextView tvVideoDuration;

        ViewHolder(FragmentItemListDialogListDialogItemBinding binding) {
            super(binding.getRoot());
            imageView = binding.imageView;
            tvPickedIndex = binding.pickedIndex;
            tvVideoDuration = binding.videoDuration;
        }

    }

    private class MediaAdapter extends RecyclerView.Adapter<ViewHolder> {
        private final int IMAGE = 0;
        private final int VIDEO = 1;
        private final int UNDEFINED = -1;


        private List<String> getAllMediaPaths() {
            List<String> mediaPaths = new ArrayList<>();

            String[] projection = {MediaStore.MediaColumns.DATA, MediaStore.MediaColumns.MIME_TYPE};
            String selection = MediaStore.Files.FileColumns.MEDIA_TYPE + " IN (" +
                    MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE + "," +
                    MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO + ")";

            Uri queryUri = MediaStore.Files.getContentUri("external");
            ContentResolver contentResolver = requireContext().getContentResolver();
            Cursor cursor = contentResolver.query(queryUri, projection, selection, null, null);

            if (cursor != null) {
                try {
                    int pathColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                    while (cursor.moveToNext()) {
                        String path = cursor.getString(pathColumnIndex);
                        mediaPaths.add(path);
                    }
                } finally {
                    cursor.close();
                }
            }

            return mediaPaths;
        }
        private List<String> mediaPaths;
        public MediaAdapter() {
            mediaPaths = getAllMediaPaths();
        }



        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(FragmentItemListDialogListDialogItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            String mediaPath = mediaPaths.get(position);
            if(isImageFile(mediaPath) && !mediaPath.isEmpty()){
                File imgFile = new  File(mediaPath);
                if(imgFile.exists())
                    holder.imageView.setImageBitmap(BitmapFactory.decodeFile(imgFile.getAbsolutePath()));
            }
        }

        @Override
        public int getItemCount() {
            return mediaPaths.size();
        }
        public boolean isVideoFile(String filePath) {
            String extension = getFileExtension(filePath);
            return extension.equalsIgnoreCase("mp4") || extension.equalsIgnoreCase("avi")
                    || extension.equalsIgnoreCase("mkv") || extension.equalsIgnoreCase("mov")
                    || extension.equalsIgnoreCase("wmv");
        }

        public boolean isImageFile(String filePath) {
            String extension = getFileExtension(filePath);
            return extension.equalsIgnoreCase("jpg") || extension.equalsIgnoreCase("jpeg")
                    || extension.equalsIgnoreCase("png") || extension.equalsIgnoreCase("gif")
                    || extension.equalsIgnoreCase("bmp");
        }

        public String getFileExtension(String filePath) {
            if (filePath != null && filePath.lastIndexOf(".") != -1) {
                return filePath.substring(filePath.lastIndexOf(".") + 1);
            }
            return "";
        }
    }
}
