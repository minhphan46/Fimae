package com.example.fimae.bottomdialogs;

import android.app.Dialog;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fimae.R;
import com.example.fimae.adapters.Report.ReportAdapter;
import com.example.fimae.adapters.Report.ReportAdapterItem;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.protobuf.LazyStringArrayList;

import java.util.ArrayList;

public class ReportDialog extends BottomSheetDialogFragment {
    String title;
    String subtitle;
    ArrayList<ReportAdapterItem> reportAdapterItems;

    public interface OnReportDialogListener {
        void onReportDialog(ReportAdapterItem reportAdapterItem, String description);
    }

    OnReportDialogListener onReportDialogListener;

    public void setOnReportDialogListener(OnReportDialogListener onReportDialogListener) {
        this.onReportDialogListener = onReportDialogListener;
    }

    private ReportDialog() {
        super();
    }

    public ReportDialog(String title, String subtitle, ArrayList<ReportAdapterItem> reportAdapterItems, OnReportDialogListener onReportDialogListener) {
        super();
        this.title = title;
        this.subtitle = subtitle;
        this.reportAdapterItems = reportAdapterItems;
        this.onReportDialogListener = onReportDialogListener;
    }

    public ReportDialog(String title, ArrayList<ReportAdapterItem> reportAdapterItems, OnReportDialogListener onReportDialogListener) {
        super();
        this.title = title;
        this.reportAdapterItems = reportAdapterItems;
        this.onReportDialogListener = onReportDialogListener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            title = getArguments().getString("title");
            subtitle = getArguments().getString("subtitle");
            reportAdapterItems = (ArrayList<ReportAdapterItem>) getArguments().getSerializable("reportAdapterItems");
        }
        BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        View view = LayoutInflater.from(this.getContext()).inflate(R.layout.report_bottom_sheet_layout, null);
        TextInputEditText textInputEditText = view.findViewById(R.id.edt_bottom_sheet_input);
        bottomSheetDialog.setContentView(view);
        MaterialButton button = view.findViewById(R.id.report_bottom_sheet_button);
        RecyclerView listView = view.findViewById(R.id.report_bottom_sheet_list_view);
        TextView textViewTitle = view.findViewById(R.id.report_bottom_sheet_title);
        TextView textViewSubtitle = view.findViewById(R.id.report_bottom_sheet_subtitle);
        if (title != null && !title.trim().isEmpty())
            textViewTitle.setText(title);
        if (subtitle != null && !subtitle.trim().isEmpty())
            textViewSubtitle.setText(subtitle);
        if (reportAdapterItems == null)
            reportAdapterItems = new ArrayList<>();
        //Create listView content report with checkboxes
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        listView.setLayoutManager(linearLayoutManager);
        ReportAdapter adapter = new ReportAdapter(reportAdapterItems);
        listView.setAdapter(adapter);
        button.setOnClickListener(v -> {
            if (onReportDialogListener != null) {
                ReportAdapterItem reportAdapterItem = adapter.getSelectedReportAdapterItem();
                if (reportAdapterItem != null) {
                    String description = textInputEditText.getText().toString();
                    onReportDialogListener.onReportDialog(reportAdapterItem, description);
                    bottomSheetDialog.dismiss();
                } else
                    Toast.makeText(getContext(), "Vui lòng chọn lý do báo cáo", Toast.LENGTH_SHORT).show();
            }

        });
        return bottomSheetDialog;
    }


    static public Builder builder() {
        return new Builder();
    }

    //Create builder for report dialog
    public static class Builder {
        private String title;
        private String subtitle;
        private ArrayList<ReportAdapterItem> reportAdapterItems;
        private OnReportDialogListener onReportDialogListener;

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setSubtitle(String subtitle) {
            this.subtitle = subtitle;
            return this;
        }

        public Builder setReportAdapterItems(ArrayList<ReportAdapterItem> reportAdapterItems) {
            this.reportAdapterItems = reportAdapterItems;
            return this;
        }

        public Builder setOnReportDialogListener(OnReportDialogListener onReportDialogListener) {
            this.onReportDialogListener = onReportDialogListener;
            return this;
        }

        public ReportDialog build() {
            ReportDialog reportDialog = new ReportDialog();
            Bundle bundle = new Bundle();
            bundle.putString("title", title);
            bundle.putString("subtitle", subtitle);
            bundle.putSerializable("reportAdapterItems", reportAdapterItems);
            reportDialog.setOnReportDialogListener(onReportDialogListener);
            reportDialog.setArguments(bundle);
            return reportDialog;
        }
    }
}
