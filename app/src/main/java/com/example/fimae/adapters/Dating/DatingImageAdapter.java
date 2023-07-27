package com.example.fimae.adapters.Dating;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fimae.R;
import com.example.fimae.adapters.SquareImageView;
import com.example.fimae.databinding.FotosItemBinding;
import com.example.fimae.utils.FileUtils;

import java.util.ArrayList;

public class DatingImageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    ArrayList<DatingImageAdapterItem> items;

    public ArrayList<DatingImageAdapterItem> getItems() {
        return items;
    }

    public void setItems(ArrayList<DatingImageAdapterItem> items) {
        this.items = items;
    }

    public DatingImageAdapter(ArrayList<DatingImageAdapterItem> items, addImageBtnListener listener) {
        this.items = items;
        this.listener = listener;
    }

    public interface addImageBtnListener {
        void onClick();
    }

    addImageBtnListener listener;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType > 0) {
            View view = View.inflate(parent.getContext(), R.layout.fotos_item, null);
            return new ViewHolder(view);
        } else {
            View view = View.inflate(parent.getContext(), R.layout.add_button, null);
            return new SpecialViewHolder(view);
        }
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (position == 0) {
            SpecialViewHolder specialViewHolder = (SpecialViewHolder) holder;
            holder.itemView.setVisibility(View.VISIBLE);
            specialViewHolder.add_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onClick();
                }
            });
        } else {
            ViewHolder viewHolder = (ViewHolder) holder;
            DatingImageAdapterItem item = items.get(position - 1);
            if (item.isLocal) {
                viewHolder.postImage.setImageURI(Uri.parse(item.url));
            } else {
                Glide.with(viewHolder.itemView).load(item.url).into(viewHolder.postImage);
            }
            viewHolder.closeIcon.setVisibility(View.VISIBLE);
            viewHolder.closeIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    items.remove(item);
                    notifyItemRemoved(position - 1);
                }
            });
        }
    }

    Context context;
    public void addImage(String url, boolean isLocal) {
        if (isLocal){
            items.add(new DatingImageAdapterItem(url, true));
        } else
            items.add(new DatingImageAdapterItem(url, false));
        notifyItemInserted(items.size() - 1);
    }

    @Override
    public int getItemCount() {
        if (items == null || items.size() == 0) return 1;
        return items.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    private View.OnClickListener mOnItemClickListener;


    public class SpecialViewHolder extends RecyclerView.ViewHolder {
        public ImageView add_image;

        public SpecialViewHolder(@NonNull View itemView) {
            super(itemView);
            add_image = itemView.findViewById(R.id.button);
        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public SquareImageView postImage;
        public ImageView closeIcon;

        public ViewHolder(View view) {
            super(view);
            postImage = view.findViewById(R.id.post_image);
            closeIcon = view.findViewById(R.id.close_icon);
        }
    }
}
