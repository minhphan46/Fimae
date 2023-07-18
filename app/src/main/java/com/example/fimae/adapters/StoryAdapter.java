package com.example.fimae.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.fimae.R;
import com.example.fimae.models.story.Story;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.StoryViewHolder>{

    ArrayList<Story> stories = Story.getFakeData();
    @NonNull
    @Override
    public StoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.story_item, parent, false);
        return new StoryViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull StoryViewHolder holder, int position) {
        // Get the data at the specified position
        Story story = stories.get(position);
//        Picasso.get().load(story.getUrl()).placeholder(R.drawable.ic_default_avatar).into(holder.storyImage);
        Glide.with(holder.itemView.getContext())
                .load("http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4")
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.storyImage);
        holder.storyTitle.setText("Nhật hào"); // Set the story title
        Log.d("Bucket", FirebaseStorage.getInstance().getReference().getBucket());
    }

    @Override
    public int getItemCount() {
        return stories.size();
    }

    public class StoryViewHolder extends RecyclerView.ViewHolder {
    ImageView storyImage;
    CircleImageView storyAvatar;
    TextView storyTitle;

    public StoryViewHolder(View itemView) {
        super(itemView);
        // Initialize the views from the layout
        storyImage = itemView.findViewById(R.id.story_image);
        storyAvatar = itemView.findViewById(R.id.story_avatar);
        storyTitle = itemView.findViewById(R.id.story_title);
    }
}
}
