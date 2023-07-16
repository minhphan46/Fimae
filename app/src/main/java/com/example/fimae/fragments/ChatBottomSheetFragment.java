package com.example.fimae.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
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

public class ChatBottomSheetFragment extends BottomSheetDialogFragment {
    private List<BottomSheetItem> bottomSheetItemList;
    private BottomSheetItemAdapter.IClickBottomSheetItemListener iClickBottomSheetItemListener;

    public ChatBottomSheetFragment(List<BottomSheetItem> bottomSheetItemList, BottomSheetItemAdapter.IClickBottomSheetItemListener iClickBottomSheetItemListener) {
        this.iClickBottomSheetItemListener = iClickBottomSheetItemListener;
        this.bottomSheetItemList = bottomSheetItemList;
    }

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        View view = LayoutInflater.from(this.getContext()).inflate(R.layout.chat_bottom_sheet, null);
        bottomSheetDialog.setContentView(view);

        RecyclerView recyclerView = view.findViewById(R.id.rcv_bottom_sheet_items);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        BottomSheetItemAdapter adapter = new BottomSheetItemAdapter(bottomSheetItemList, new BottomSheetItemAdapter.IClickBottomSheetItemListener() {
            @Override
            public void onClick(BottomSheetItem bottomSheetItem) {

                iClickBottomSheetItemListener.onClick(bottomSheetItem);
            }
        });
        recyclerView.setAdapter(adapter);
        return bottomSheetDialog;
    }
}
