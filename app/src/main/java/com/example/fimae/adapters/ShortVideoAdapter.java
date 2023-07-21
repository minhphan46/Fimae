package com.example.fimae.adapters;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import java.util.concurrent.TimeUnit;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.fimae.R;
import com.example.fimae.databinding.LayoutReelBinding;
import com.example.fimae.models.Fimaers;
import com.example.fimae.models.shorts.ShortMedia;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

public class ShortVideoAdapter extends FirestoreAdapter<ShortVideoAdapter.VideoHolder>{
    Context context;
    ArrayList<ShortMedia> shortMedias = new ArrayList<>();
    private ShortVideoAdapter.IClickCardListener iClickCardListener;
    boolean isPlaying = true;

    public interface IClickCardListener {
        void onClickUser(ShortMedia video);
    }

    public ShortVideoAdapter(Query query, Context context, ArrayList<ShortMedia> shortMedias, IClickCardListener iClickCardListener) {
        super(query);
        this.context = context;
        this.shortMedias = shortMedias;
        this.iClickCardListener = iClickCardListener;
    }

    @NonNull
    @Override
    public VideoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_reel, parent, false);
        return new ShortVideoAdapter.VideoHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoHolder holder, int position) {
        ShortMedia media = shortMedias.get(position);
        onBeginPlayVideo(holder, media);

        holder.binding.videoView.setVideoURI(Uri.parse((String) media.getMediaUrl()));
        holder.binding.itemVideoTvName.setText("Minh Phan");
        holder.binding.itemVideoTvDescription.setText(media.getDescription());
        //holder.binding.itemVideoTvLike.setText(media.getUsersLiked().size());
        //holder.binding.itemVideoTvComment.setText(media.getNumOfComments());

        holder.binding.itemVideoIcBack.setOnClickListener(view -> {
            iClickCardListener.onClickUser(media);
        });
        holder.binding.videoView.setOnPreparedListener(mp -> {
            mp.start();
            mp.setLooping(true);
            isPlaying = true;
            checkPlaying(holder);
            wait(300);
            holder.binding.imageThumb.setVisibility(View.GONE);
            holder.binding.loading.setVisibility(View.GONE);
        });
        holder.binding.videoView.setOnClickListener(view -> {
            togglePlaying(holder);
        });
        holder.binding.itemVideoIcPlay.setOnClickListener(view -> {
            togglePlaying(holder);
        });

    }

    private void onBeginPlayVideo(@NonNull VideoHolder holder, ShortMedia media) {
        // anh thumbnail
        long interval = 0;
        RequestOptions options = new RequestOptions().frame(interval);
        Glide.with(holder.itemView)
                .load(Uri.parse((String) media.getMediaUrl()))
                .apply(options)
                .into(holder.binding.imageThumb);
        holder.binding.imageThumb.setVisibility(View.VISIBLE);
        holder.binding.loading.setVisibility(View.VISIBLE);
        holder.binding.itemVideoIcPlay.setVisibility(View.GONE);
    }
    public static void wait(int ms)
    {
        try
        {
            Thread.sleep(ms);
        }
        catch(InterruptedException ex)
        {
            Thread.currentThread().interrupt();
        }
    }
    void togglePlaying(@NonNull VideoHolder holder) {
        if (isPlaying) {
            holder.binding.videoView.pause();
            isPlaying = false;
            holder.binding.itemVideoIcPlay.setVisibility(View.VISIBLE);
        } else {
            holder.binding.videoView.start();
            isPlaying = true;
            holder.binding.itemVideoIcPlay.setVisibility(View.GONE);
        }
    }

    void checkPlaying(@NonNull VideoHolder holder) {
        if (!isPlaying) {
            holder.binding.itemVideoIcPlay.setVisibility(View.VISIBLE);
        } else {
            holder.binding.itemVideoIcPlay.setVisibility(View.GONE);
        }
    }

    @Override
    public void OnSuccessQueryListener(ArrayList<DocumentSnapshot> queryDocumentSnapshots) {

    }

    @Override
    public void OnSuccessQueryListener(ArrayList<DocumentSnapshot> queryDocumentSnapshots, ArrayList<DocumentChange> documentChanges) {
        if(shortMedias == null){
            shortMedias = new ArrayList<>();
        } else {
            shortMedias.clear();
        }
        for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots){
            ShortMedia shortMedia = documentSnapshot.toObject(ShortMedia.class);
            shortMedias.add(shortMedia);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return shortMedias.size();
    }

    public class VideoHolder extends RecyclerView.ViewHolder {
        LayoutReelBinding binding;
        public VideoHolder(@NonNull View itemView) {
            super(itemView);
            binding = LayoutReelBinding.bind(itemView);
        }
    }
}
