package com.example.fimae.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.example.fimae.R;
import com.example.fimae.adapters.ReportItemDetailAdapter;
import com.example.fimae.models.ReportDetail;
import com.example.fimae.repository.ReportRepository;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

public class AminDetailReportActivity extends AppCompatActivity {
    private ArrayList<ReportDetail> reportDetails;
    private RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amin_detail_report);
        recyclerView = findViewById(R.id.recycler_view);
        Intent intent = getIntent();
        String reportId = intent.getStringExtra("reportId");
        Query query = ReportRepository.getInstance().getReportDetailRef(reportId);
        ReportItemDetailAdapter adapter = new ReportItemDetailAdapter(query);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
    }
}