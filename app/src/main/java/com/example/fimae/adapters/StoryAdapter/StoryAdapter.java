package com.example.fimae.adapters.StoryAdapter;

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
import com.example.fimae.R;
import com.example.fimae.adapters.FirestoreAdapter;
import com.example.fimae.models.story.Story;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class StoryAdapter extends FirestoreAdapter<StoryAdapter.StoryViewHolder> {
    public StoryAdapter(Query query) {
        super(query);
    }

    public interface  StoryListener {
        void addStoryClicked();
        void onStoryClicked(StoryAdapterItem storyAdapterItem);
    }
    private  StoryListener storyListener;
    public void setStoryListener(StoryListener storyListener){
        this.storyListener = storyListener;
    }

    ArrayList< StoryAdapterItem> storyAdapterItems;
    //Get storyAdapterItems
    public ArrayList<StoryAdapterItem> getStoryAdapterItems() {
        return storyAdapterItems;
    }

    @Override
    public void OnSuccessQueryListener(ArrayList<DocumentSnapshot> queryDocumentSnapshots, ArrayList<DocumentChange> documentChanges) {
        if(storyAdapterItems == null || storyAdapterItems.size() == 0){
            storyAdapterItems = new ArrayList<>();
            for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                Story story = documentSnapshot.toObject(Story.class);
                boolean isFound = false;
                for (StoryAdapterItem storyAdapterItem : storyAdapterItems) {
                    if(storyAdapterItem.getUid().equals(story.getUid())){
                        storyAdapterItem.getStories().add(story);
                        isFound = true;
                        break;
                    }
                }
                if (!isFound){
                    StoryAdapterItem storyAdapterItem = new StoryAdapterItem(story.getUid());
                    storyAdapterItem.getStories().add(story);
                    storyAdapterItems.add(storyAdapterItem);
                }
            }
        }
        notifyDataSetChanged();
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
            Story story = storyAdapterItems.get(storyPosition).getStories().get(0);
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
                        storyListener.onStoryClicked(storyAdapterItems.get(position - 1));
                    }
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        if(storyAdapterItems == null){
            return 1;
        }
        return storyAdapterItems.size() + 1;
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
