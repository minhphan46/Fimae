package com.example.fimae.adapters;


import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fimae.R;

import java.util.List;

public class PostPhotoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private Context context;
    private List<Uri> mImages;
    boolean isEdit;
    int VIEW_TYPE_SPECIAL_ITEM = 1;
    int VIEW_TYPE_NORMAL_ITEM = 0;
    public PostPhotoAdapter(Context context, List<Uri> mImages, boolean isEdit){

        this.context = context;
        this.mImages = mImages;
        this.isEdit = isEdit;
    }
    @Override
    public int getItemViewType(int position) {
        if (isEdit && position == mImages.size()) {
            return VIEW_TYPE_SPECIAL_ITEM;
        } else {
            return VIEW_TYPE_NORMAL_ITEM;
        }
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == VIEW_TYPE_NORMAL_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fotos_item, parent, false);
            return new PostPhotoAdapter.ViewHolder(view);
        }
        else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_button, parent, false);
            return new PostPhotoAdapter.SpecialViewHolder(view);
        }
    }

    private View.OnClickListener mOnItemClickListener;

    public void setOnItemClickListener(View.OnClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if(position == mImages.size() && isEdit){
            SpecialViewHolder viewHolder = (SpecialViewHolder) holder;
        }
        else {
            Uri imageUri = mImages.get(position);
            ViewHolder viewHolder = (ViewHolder)holder;
            Glide.with(context).load(imageUri).into(viewHolder.post_image);
        }
    }

    @Override
    public int getItemCount()  {
        if(!isEdit) return mImages.size();
        return mImages.size() + 1;
    }
    public class SpecialViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView add_image;
        public SpecialViewHolder(@NonNull View itemView){
            super(itemView);
            add_image = itemView.findViewById(R.id.button);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mOnItemClickListener.onClick(view);
        }
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView post_image;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            post_image = itemView.findViewById(R.id.post_image);
        }
    }

}