package com.example.fimae.adapters;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fimae.R;
import com.example.fimae.activities.PostContentActivity;
import com.example.fimae.databinding.CommentItemBinding;
import com.example.fimae.databinding.FotosItemBinding;

import java.util.ArrayList;
import java.util.List;

public class PostPhotoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private Context context;
    private List<Uri> mImages;
    private ArrayList<String> mImageUrls;
    private List<String> editedImageList;
    boolean isEdit;
    int VIEW_TYPE_SPECIAL_ITEM = 1;
    int VIEW_TYPE_NORMAL_ITEM = 0;
    public void showImageDetail(int index){
        Intent intent = new Intent(context, PostContentActivity.class);
        intent.putExtra("urls", mImageUrls);
        intent.putExtra("index", index);
        context.startActivity(intent);
    }
    public PostPhotoAdapter(Context context, ArrayList<String> mImageUrls){
        this.context = context;
        this.mImageUrls = mImageUrls;
        this.isEdit = false;
        updateImages(mImageUrls);
    }
    public void updateImages(ArrayList<String> mImageUrls){
        mImages = new ArrayList<>();
        for (int i = 0; i < mImageUrls.size(); i++){
            mImages.add(Uri.parse(mImageUrls.get(i)));
        }
        notifyDataSetChanged();
    }
    public PostPhotoAdapter(Context context, List<Uri> mImages,  boolean isEdit){
        this.context = context;
        this.mImages = mImages;
        this.isEdit = isEdit;
    }
    public void setEditedImageList(List<String> editedImageList){
        this.editedImageList = editedImageList;
    }
    @Override
    public int getItemViewType(int position) {
        if (isEdit && position == 0) {
            return VIEW_TYPE_SPECIAL_ITEM;
        } else {
            return VIEW_TYPE_NORMAL_ITEM;
        }
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == VIEW_TYPE_NORMAL_ITEM) {
            FotosItemBinding view = FotosItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
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

        if(isEdit && position == 0){
            SpecialViewHolder viewHolder = (SpecialViewHolder) holder;
        }
        else if (editedImageList != null && position <= editedImageList.size() && position >=1){
            Uri imageUri =Uri.parse(editedImageList.get(position - 1));
            ViewHolder viewHolder = (ViewHolder)holder;
            Glide.with(context).load(imageUri).into(viewHolder.binding.postImage);
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showImageDetail(0);
                }
            });
            if(isEdit) viewHolder.binding.closeIcon.setVisibility(View.VISIBLE);
            viewHolder.binding.closeIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    editedImageList.remove(position - 1);
                    notifyItemRemoved(holder.getAdapterPosition());
                }
            });
        }
        else {
            int index = position;
            if(isEdit){
                index = position - 1 - editedImageList.size();
            }
            Uri imageUri = mImages.get(index);
            ViewHolder viewHolder = (ViewHolder)holder;
            Glide.with(context).load(imageUri).into(viewHolder.binding.postImage);
            if(isEdit) viewHolder.binding.closeIcon.setVisibility(View.VISIBLE);
            viewHolder.binding.closeIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(context, "Click on close", Toast.LENGTH_SHORT).show();
                }
            });
            int finalIndex = index;
            viewHolder.binding.closeIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mImages.remove(finalIndex);
                    notifyDataSetChanged();
                }
            });
            viewHolder.binding.postImage.setOnClickListener(view ->{
                if(isEdit){
                    // Apply animation when the close icon becomes visible
//                    AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
//                    alphaAnimation.setDuration(500);
//                    viewHolder.binding.closeIcon.startAnimation(alphaAnimation);
                }
                else{
                    showImageDetail(holder.getPosition());
                }
                });
        }
    }
    private void showCloseIcon() {
    }

    @Override
    public int getItemCount()  {
        if(!isEdit) return mImages.size();
        return mImages.size() +  editedImageList.size() + 1;
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
        public FotosItemBinding binding;
        public ViewHolder(FotosItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}