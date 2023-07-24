package com.example.fimae.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fimae.R;
import com.example.fimae.adapters.ShortAdapter.ShortVideoAdapter;
import com.example.fimae.databinding.PostImageItemBinding;
import com.example.fimae.models.Fimaers;
import com.example.fimae.models.shorts.ShortMedia;
import com.example.fimae.repository.FimaerRepository;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;

public class PostContentAdapter extends RecyclerView.Adapter<PostContentAdapter.PostContentHolder>{
    Context context;
    ArrayList<String> imageLists = new ArrayList<>();
    private IClickListener iClickCardListener;
    boolean isPlaying = false;
    String idPublisher;
    String description;

    public interface IClickListener {
        void onClickClose();
    }

    public PostContentAdapter(Context context, ArrayList<String> imageLists, String idPublisher, String description, IClickListener iClickCardListener) {
        this.context = context;
        this.imageLists = imageLists;
        this.idPublisher = idPublisher;
        this.description = description;
        this.iClickCardListener = iClickCardListener;
    }

    @NonNull
    @Override
    public PostContentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_image_item, parent, false);
        return new PostContentAdapter.PostContentHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull PostContentHolder holder, int position) {
        String imageUrl = imageLists.get(position);
        Glide.with(context).load(imageUrl).into(holder.binding.mImageView);

        holder.binding.itemVideoTvDescription.setText(description);
        FimaerRepository.getInstance().getFimaerById(idPublisher).addOnCompleteListener(task -> {
            task.onSuccessTask(fimaers -> {
                holder.binding.itemVideoTvName.setText(fimaers.getName());
                return null;
            });
        });
        holder.binding.btnClose.setOnClickListener(view -> {
            iClickCardListener.onClickClose();
        });
    }
    @Override
    public int getItemCount() {
        return imageLists.size();
    }

    public class PostContentHolder extends RecyclerView.ViewHolder {
        PostImageItemBinding binding;
        public PostContentHolder(@NonNull View itemView) {
            super(itemView);
            binding = PostImageItemBinding.bind(itemView);
        }
    }
}
