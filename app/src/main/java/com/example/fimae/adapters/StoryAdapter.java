package com.example.fimae.adapters;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.fimae.R;
import com.example.fimae.models.story.Story;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.StoryViewHolder> {

    public interface  StoryListener {
        void addStoryClicked();
        void onStoryClicked(int position);
    }
    ArrayList<Story> stories = Story.getFakeData();
    private  StoryListener storyListener;
    public void setStoryListener(StoryListener storyListener){
        this.storyListener = storyListener;
    }
    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @NonNull
    @Override
    public StoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 0) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_story_item, parent, false);
            return new StoryViewHolder(view, true);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.story_item, parent, false);
            return new StoryViewHolder(view, false);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull StoryViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if (position != 0){
            int storyPosition = position - 1;
            // Get the data at the specified position
            Story story = stories.get(storyPosition);
//        Picasso.get().load(story.getUrl()).placeholder(R.drawable.ic_default_avatar).into(holder.storyImage);
            Glide.with(holder.itemView)
                    .load(story.getUrl())
                    .into(holder.storyImage);
            holder.storyTitle.setText("Nhật Hào"); // Set the story title
            Log.d("Bucket", FirebaseStorage.getInstance().getReference().getBucket());
        }
        holder.storyCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(storyListener != null){
                    if(position == 0){
                        storyListener.addStoryClicked();
                    }else{
                        storyListener.onStoryClicked(position - 1);
                    }
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return stories.size() + 1;
    }

    public class StoryViewHolder extends RecyclerView.ViewHolder {
        CardView storyCard;
        ImageView storyImage;
        CircleImageView storyAvatar;
        TextView storyTitle;

        public StoryViewHolder(View itemView, boolean isAddStory) {
            super(itemView);
            if (isAddStory) {
                storyImage = itemView.findViewById(R.id.add_story_background);
                storyAvatar = itemView.findViewById(R.id.add_story);
                storyTitle = itemView.findViewById(R.id.add_story_title);
                storyCard = itemView.findViewById(R.id.add_story_item_layout);
            } else {
                // Initialize the views from the layout
                storyImage = itemView.findViewById(R.id.story_image);
                storyAvatar = itemView.findViewById(R.id.story_avatar);
                storyTitle = itemView.findViewById(R.id.story_title);
                storyCard = itemView.findViewById(R.id.story_item_layout);
            }
        }
    }
}
