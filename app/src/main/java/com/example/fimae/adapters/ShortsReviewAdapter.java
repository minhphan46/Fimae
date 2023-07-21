package com.example.fimae.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fimae.R;
import com.example.fimae.adapters.StoryAdapter.StoryAdapter;
import com.example.fimae.models.shorts.ShortMedia;
import com.example.fimae.models.story.Story;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ShortsReviewAdapter extends FirestoreAdapter<ShortsReviewAdapter.ShortsReviewHolder>{

    ArrayList<ShortMedia> shortMedias = ShortMedia.getFakeData();

    private ShortsReviewAdapter.IClickCardListener iClickCardListener;

    private boolean isInProfile = false;

    public interface IClickCardListener {
        void addShortClicked();
        void onClickUser(ShortMedia video);
    }

    public ShortsReviewAdapter(Query query, boolean isInProfile,IClickCardListener iClickCardListener) {
        super(query);
        this.isInProfile = isInProfile;
        this.iClickCardListener = iClickCardListener;
    }

    @NonNull
    @Override
    public ShortsReviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
          if (viewType == 0 && !isInProfile) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_short_item, parent, false);
            return new ShortsReviewHolder(view, true);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.short_review_item, parent, false);
            return new ShortsReviewHolder(view, false);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ShortsReviewHolder holder, int position) {
        if(isInProfile){
            // Get the data at the specified position
            ShortMedia shortMedia = shortMedias.get(position);
            //Picasso.get().load(shortMedia.getURL()).placeholder(R.drawable.ic_default_avatar).into(holder.shortAvatar);
            Glide.with(holder.itemView)
                    .load(shortMedia.getMediaUrl())
                    .into(holder.shortImage);
            holder.shortImage.setOnClickListener(view -> {
                iClickCardListener.onClickUser(shortMedia);
            });
            holder.shortViewCount.setText("120K");
        }
        else if( position == 0) {
            holder.shortImage.setOnClickListener(view -> {
                iClickCardListener.addShortClicked();
            });
        }
        else {
            // Get the data at the specified position
            int shortPosition = position - 1;
            ShortMedia shortMedia = shortMedias.get(shortPosition);
            //Picasso.get().load(shortMedia.getURL()).placeholder(R.drawable.ic_default_avatar).into(holder.shortAvatar);
            Glide.with(holder.itemView)
                    .load(shortMedia.getMediaUrl())
                    .into(holder.shortImage);
            holder.shortImage.setOnClickListener(view -> {
                iClickCardListener.onClickUser(shortMedia);
            });
            holder.shortViewCount.setText("120K");
        }
    }


    @Override
    public void OnSuccessQueryListener(ArrayList<DocumentSnapshot> queryDocumentSnapshots) {

    }

    @Override
    public int getItemCount() {
        if(isInProfile) return shortMedias.size();
        else
        if(shortMedias == null){
            return 1;
        }
        else  return shortMedias.size() + 1;
    }
    @Override
    public int getItemViewType(int position) {
        return position;
    }
    public class ShortsReviewHolder extends RecyclerView.ViewHolder {
        ImageView shortImage;
        CircleImageView shortAvatar;
        TextView shortViewCount;

        public ShortsReviewHolder(View itemView, boolean isAddShort) {
            super(itemView);
            // Initialize the views from the layout
            if(isAddShort) {
                shortImage = itemView.findViewById(R.id.short_add_image);
                shortAvatar = itemView.findViewById(R.id.ic_video_border);
                shortViewCount = itemView.findViewById(R.id.add_short_title);
            }
            else {
                shortImage = itemView.findViewById(R.id.short_image);
                shortAvatar = itemView.findViewById(R.id.short_avatar);
                shortViewCount = itemView.findViewById(R.id.short_view_count);
            }
        }
    }
}
