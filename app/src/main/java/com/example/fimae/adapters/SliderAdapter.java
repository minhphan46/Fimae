package com.example.fimae.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.fimae.R;
import com.example.fimae.models.FimaeUser;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SliderAdapter extends RecyclerView.Adapter<SliderAdapter.SliderViewHolder>{

    private List<FimaeUser> sliderItems;
    private ViewPager2 viewPager2;

    public SliderAdapter(List<FimaeUser> sliderItemList, ViewPager2 viewPager2) {
        this.sliderItems = sliderItemList;
        this.viewPager2 = viewPager2;
    }

    @NonNull
    @Override
    public SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SliderViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.item_image_avatar,
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull SliderViewHolder holder, int position) {
        FimaeUser user = sliderItems.get(position);
        if(user == null){
            return;
        }

        holder.setImage(sliderItems.get(position));
        if(position == sliderItems.size() - 2) {
            viewPager2.post(runnable);
        }
    }

    @Override
    public int getItemCount() {
        if(sliderItems != null){
            return sliderItems.size();
        }
        return 0;
    }

    class SliderViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView imageView;

        public SliderViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_slide);
        }

        void setImage(FimaeUser user){
            // set image picasso
            Picasso.get().load(user.getAvatarUrl()).placeholder(R.drawable.ic_default_avatar).into(imageView);
        }
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            sliderItems.addAll(sliderItems);
            notifyDataSetChanged();
        }
    };
}
