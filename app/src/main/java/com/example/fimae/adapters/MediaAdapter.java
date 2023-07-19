package com.example.fimae.adapters;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.fimae.databinding.FragmentItemListDialogListDialogItemBinding;
import com.example.fimae.utils.FileUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.ViewHolder> {
    private static final int IMAGE = 0;
    private static final int VIDEO = 1;
    private final ArrayList<String> pickedItems;
    private final ArrayList<MediaAdapterItem> mediaAdapterItems;

    public static class MediaAdapterItem {
        int position;
        int indexPicked;
        int mediaType;
        String path;
        boolean isSelected;
        String videoDuration;

        public MediaAdapterItem(String path) {
            this.path = path;
            indexPicked = -1;
            isSelected = false;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public MediaAdapter(Context context) {
        pickedItems = new ArrayList<>();
        mediaAdapterItems = new ArrayList<>();
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            //Background work here
            ArrayList<String> mediaPaths = FileUtils.getAllMediaPaths(context);
            for (int i = 0; i < mediaPaths.size(); i++) {
                String path = mediaPaths.get(i);
                MediaAdapterItem item = new MediaAdapterItem(path);
                item.position = i;
                if (FileUtils.isImageFile(path)) {
                    item.mediaType = IMAGE;
                } else {
                    final String videoDuration;
                    try {
                        videoDuration = FileUtils.formatDuration(FileUtils.getVideoDuration(path));
                    } catch (IOException e) {
                        continue;
                    }
                    item.mediaType = VIDEO;
                    item.videoDuration = videoDuration;
                }
                mediaAdapterItems.add(item);
            }
            //UI Thread work here
            handler.post(this::notifyDataSetChanged);
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(FragmentItemListDialogListDialogItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        MediaAdapterItem item = mediaAdapterItems.get(position);
        setMediaItem(holder, item);
        holder.layout.setOnClickListener(v -> {
            if (!item.isSelected) {
                item.isSelected = true;
                pickedItems.add(item.path);
                item.indexPicked = pickedItems.size();
                notifyItemChanged(position);
            } else {
                pickedItems.remove(item.path);
                item.isSelected = false;
                notifyItemChanged(position);
                updateSelectedIndices();
            }
        });
    }
public ArrayList<String> getSelectedMedias(){
        return pickedItems;
}

    @Override
    public int getItemCount() {
        return mediaAdapterItems.size();
    }

    private void updateSelectedIndices() {

        for (MediaAdapterItem item : mediaAdapterItems) {
            if (item.isSelected) {
                item.indexPicked = pickedItems.lastIndexOf(item.path) + 1;
                notifyItemChanged(item.position);
            }
        }
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView tvPickedIndex;
        public TextView tvVideoDuration;
        public CardView layout;

        public ViewHolder(FragmentItemListDialogListDialogItemBinding binding) {
            super(binding.getRoot());
            layout = binding.mediaItemLayout;
            imageView = binding.imageView;
            tvPickedIndex = binding.pickedIndex;
            tvVideoDuration = binding.videoDuration;
        }
    }

    private static void setMediaItem(ViewHolder holder, MediaAdapterItem item) {
        Glide.with(holder.imageView)
                .load(item.path)
                .into(holder.imageView);
        if (item.mediaType == IMAGE) {
            holder.tvVideoDuration.setVisibility(View.GONE);
        } else {
            holder.tvVideoDuration.setText(item.videoDuration);
            holder.tvVideoDuration.setVisibility(View.VISIBLE);
        }
        if (item.isSelected) {
            holder.tvPickedIndex.setText(String.valueOf(item.indexPicked));
            holder.tvPickedIndex.setVisibility(View.VISIBLE);
        } else {
            holder.tvPickedIndex.setVisibility(View.GONE);
        }

    }
}
