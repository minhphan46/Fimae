package com.example.fimae.adapters.Report;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fimae.R;

import java.util.ArrayList;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ReportViewHolder>{
    public ReportAdapter(ArrayList<ReportAdapterItem> reportAdapterItems) {
        this.reportAdapterItems = reportAdapterItems;
    }
    ArrayList<ReportAdapterItem> reportAdapterItems;
    public ReportAdapterItem getSelectedReportAdapterItem() {
        for (int i = 0; i < reportAdapterItems.size(); i++) {
            if (reportAdapterItems.get(i).isChecked()) {
                return reportAdapterItems.get(i);
            }
        }
        return null;
    }
    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.report_bottom_sheet_item_layout, parent, false);
        return new ReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {
        holder.textView.setText(reportAdapterItems.get(position).getTitle());
        holder.checkBox.setChecked(reportAdapterItems.get(position).isChecked());
        holder.layout.setOnClickListener(v -> {
            holder.checkBox.setChecked(!holder.checkBox.isChecked());
            for (int i = 0; i < reportAdapterItems.size(); i++) {
                reportAdapterItems.get(i).setChecked(false);
                holder.checkBox.setChecked(false);
            }
            reportAdapterItems.get(position).setChecked(true);
            notifyDataSetChanged();
        });
        holder.checkBox.setOnClickListener(v -> {
            for (int i = 0; i < reportAdapterItems.size(); i++) {
                reportAdapterItems.get(i).setChecked(false);
            }
            reportAdapterItems.get(position).setChecked(true);
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        if (reportAdapterItems == null) {
            return 0;
        }
        return reportAdapterItems.size();
    }

    public void setCurrentSelectedIndex(int selectedRadioIndex) {
        for (int i = 0; i < reportAdapterItems.size(); i++) {
            reportAdapterItems.get(i).setChecked(false);
        }
        reportAdapterItems.get(selectedRadioIndex).setChecked(true);
        notifyDataSetChanged();
    }

    public class ReportViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout layout;
        AppCompatCheckBox checkBox;
        TextView textView;
        public ReportViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.cb_report_bottom_sheet_item);
            textView = itemView.findViewById(R.id.tv_report_bottom_sheet_item);
            layout = itemView.findViewById(R.id.cl_report_bottom_sheet_item);
        }
    }
}
