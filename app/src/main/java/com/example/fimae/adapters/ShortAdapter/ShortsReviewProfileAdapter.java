package com.example.fimae.adapters.ShortAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fimae.R;
import com.example.fimae.adapters.FirestoreAdapter;
import com.example.fimae.models.Fimaers;
import com.example.fimae.models.shorts.ShortMedia;
import com.example.fimae.repository.FimaerRepository;
import com.example.fimae.repository.ShortsRepository;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ShortsReviewProfileAdapter extends FirestoreAdapter<ShortsReviewProfileAdapter.ShortsReviewHolder> {
    ArrayList<ShortMedia> shortMedias = new ArrayList<>();
    private ShortsReviewProfileAdapter.IClickCardListener iClickCardListener;

    public interface IClickCardListener {
        void onClickUser(ShortMedia video);
    }

    public ShortsReviewProfileAdapter(Query query,  IClickCardListener iClickCardListener) {
        super(query);
        this.iClickCardListener = iClickCardListener;
    }

    @NonNull
    @Override
    public ShortsReviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.short_review_profile_item, parent, false);
        return new ShortsReviewHolder(view, false);
    }

    @Override
    public void onBindViewHolder(@NonNull ShortsReviewHolder holder, int position) {
        // Get the data at the specified position
        ShortMedia shortMedia = shortMedias.get(position);
        Glide.with(holder.itemView)
                .load(shortMedia.getMediaUrl())
                .into(holder.shortImage);
        holder.shortImage.setOnClickListener(view -> {
            iClickCardListener.onClickUser(shortMedia);
        });
        holder.shortViewCount.setText(formatNumber(ShortsRepository.getInstance().getNumOfWatched(shortMedia)));
    }

    public static String formatNumber(int number) {
        if (number < 1000) {
            return String.valueOf(number).replace(",", ".");
        } else if (number < 1000000) {
            return formatWithDot(number / 1000.0, "k");
        } else {
            return formatWithDot(number / 1000000.0, "M");
        }
    }

    private static String formatWithDot(double value, String unit) {
        DecimalFormat decimalFormat = new DecimalFormat("#0.#");
        decimalFormat.setDecimalSeparatorAlwaysShown(false);
        String formattedValue = decimalFormat.format(value).replace(",", ".");
        return formattedValue.endsWith(".0") ? formattedValue.substring(0, formattedValue.length() - 2) : formattedValue + unit;
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
    public int getItemCount() {
        return shortMedias.size();
    }
    @Override
    public int getItemViewType(int position) {
        return position;
    }
    public class ShortsReviewHolder extends RecyclerView.ViewHolder {
        ImageView shortImage;
        TextView shortViewCount;

        public ShortsReviewHolder(View itemView, boolean isAddShort) {
            super(itemView);
            shortImage = itemView.findViewById(R.id.short_review_profile_image);
            shortViewCount = itemView.findViewById(R.id.short_review_view_count);
        }
    }
}
