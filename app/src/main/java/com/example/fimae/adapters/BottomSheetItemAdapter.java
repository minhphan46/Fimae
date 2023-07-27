package com.example.fimae.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import com.example.fimae.R;
import com.example.fimae.models.BottomSheetItem;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BottomSheetItemAdapter extends RecyclerView.Adapter<BottomSheetItemAdapter.ViewHolder> {
    List<BottomSheetItem> bottomSheetItemList;
    public IClickBottomSheetItemListener iClickBottomSheetItemListener;

    public BottomSheetItemAdapter(List<BottomSheetItem> bottomSheetItemList, IClickBottomSheetItemListener iClickBottomSheetItemListener) {
        this.bottomSheetItemList = bottomSheetItemList;
        this.iClickBottomSheetItemListener = iClickBottomSheetItemListener;
    }

    @NonNull
    @NotNull
    @Override
    public BottomSheetItemAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bottomsheet_item,parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull BottomSheetItemAdapter.ViewHolder holder, int position) {
        BottomSheetItem item = bottomSheetItemList.get(position);
        if(item == null) return;
        holder.leadingIcon.setImageResource(item.getLeadingIcon());
        holder.leadingIcon.setColorFilter(R.color.black);
        holder.textViewTitle.setText(item.getTitle());
        if(item.getSubtitle() != null){
            holder.textViewSubtitle.setText(item.getSubtitle());
        } else {
            holder.textViewSubtitle.setVisibility(View.GONE);
        }
        if(item.getTrailingIcon() != Integer.MIN_VALUE){
            holder.trailingIcon.setImageResource(item.getTrailingIcon());
            holder.trailingIcon.setColorFilter(R.color.black);
        } else {
            holder.trailingIcon.setVisibility(View.GONE);
        }
        holder.layout.setOnClickListener(v -> iClickBottomSheetItemListener.onClick(item));
    }

    @Override
    public int getItemCount() {
        if (bottomSheetItemList == null) return 0;
        return bottomSheetItemList.size();
    }

    public interface IClickBottomSheetItemListener {
        void onClick(BottomSheetItem bottomSheetItem);
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView leadingIcon;
        ImageView trailingIcon;
        TextView textViewTitle;
        TextView textViewSubtitle;
        ConstraintLayout layout;
        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            trailingIcon = itemView.findViewById(R.id.trailing_icon);
            leadingIcon = itemView.findViewById(R.id.leading_icon);
            textViewTitle = itemView.findViewById(R.id.tv_title);
            textViewSubtitle = itemView.findViewById(R.id.tv_subtitle);
            layout = itemView.findViewById(R.id.bottom_sheet_item_layout);
        }
    }

}
