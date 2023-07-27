package com.example.fimae.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fimae.R;
import com.example.fimae.adapters.BottomSheetItemAdapter;
import com.example.fimae.models.BottomSheetItem;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class FimaeBottomSheet extends BottomSheetDialogFragment {
    String title;
    String subtitle;
    private List<BottomSheetItem> bottomSheetItemList;
    private BottomSheetItemAdapter.IClickBottomSheetItemListener iClickBottomSheetItemListener;

    public void setiClickBottomSheetItemListener(BottomSheetItemAdapter.IClickBottomSheetItemListener iClickBottomSheetItemListener) {
        this.iClickBottomSheetItemListener = iClickBottomSheetItemListener;
    }

    FimaeBottomSheet(List<BottomSheetItem> bottomSheetItemList) {
        this.bottomSheetItemList = bottomSheetItemList;
    }

    FimaeBottomSheet(List<BottomSheetItem> bottomSheetItemList, String title) {
        this.bottomSheetItemList = bottomSheetItemList;
        this.title = title;
    }

    FimaeBottomSheet(List<BottomSheetItem> bottomSheetItemList, String title, String subtitle) {
        this.bottomSheetItemList = bottomSheetItemList;
        this.title = title;
        this.subtitle = subtitle;
    }

    public FimaeBottomSheet(List<BottomSheetItem> bottomSheetItemList, BottomSheetItemAdapter.IClickBottomSheetItemListener iClickBottomSheetItemListener) {
        this.iClickBottomSheetItemListener = iClickBottomSheetItemListener;
        this.bottomSheetItemList = bottomSheetItemList;
    }

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        View view = LayoutInflater.from(this.getContext()).inflate(R.layout.fimae_bottom_sheet, null);
        bottomSheetDialog.setContentView(view);
        TextView textViewTitle = view.findViewById(R.id.tv_bottom_sheet_title);
        TextView textViewSubtitle = view.findViewById(R.id.tv_bottom_sheet_subtitle);
        if (title != null) textViewTitle.setText(title);
        else textViewTitle.setVisibility(View.GONE);
        if (subtitle != null) textViewSubtitle.setText(subtitle);
        else textViewSubtitle.setVisibility(View.GONE);
        RecyclerView recyclerView = view.findViewById(R.id.rcv_bottom_sheet_items);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        BottomSheetItemAdapter adapter = new BottomSheetItemAdapter(bottomSheetItemList, new BottomSheetItemAdapter.IClickBottomSheetItemListener() {
            @Override
            public void onClick(BottomSheetItem bottomSheetItem) {
                if (iClickBottomSheetItemListener != null)
                    iClickBottomSheetItemListener.onClick(bottomSheetItem);
            }
        });
        recyclerView.setAdapter(adapter);
        return bottomSheetDialog;
    }
}
