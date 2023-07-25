package com.example.fimae.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DownloadManager;
import android.os.Bundle;

import com.example.fimae.R;
import com.example.fimae.adapters.ReportItemAdapter;
import com.example.fimae.models.Fimaers;
import com.example.fimae.models.Report;
import com.example.fimae.models.ReportAdapterItem;
import com.example.fimae.models.Reports;
import com.example.fimae.repository.ReportRepository;
import com.example.fimae.utils.ReportItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;

public class AdminReportActivity extends AppCompatActivity {
     ReportItemAdapter adapter;
     ArrayList<ReportAdapterItem> reportAdapterItems = new ArrayList<>();
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_report);
        recyclerView = findViewById(R.id.recycler);
        adapter = new ReportItemAdapter();
        adapter.setData(AdminReportActivity.this, reportAdapterItems, new ReportItemAdapter.IClickCardUserListener() {
            @Override
            public void onClickUser(Fimaers user) {
            }
        });
        recyclerView.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(AdminReportActivity.this);
        recyclerView.setLayoutManager(linearLayoutManager);
        ReportRepository.getInstance().getReports(reportAdapterItems, adapter);
    }
}