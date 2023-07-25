package com.example.fimae.models;

import androidx.annotation.NonNull;

import com.example.fimae.adapters.ReportItemAdapter;
import com.example.fimae.models.shorts.ShortMedia;
import com.example.fimae.repository.FimaerRepository;
import com.example.fimae.repository.PostRepository;
import com.example.fimae.repository.ReportRepository;
import com.example.fimae.repository.ShortsRepository;
import com.example.fimae.utils.ReportItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

public class ReportAdapterItem {
    Reports report;
    ArrayList<ReportDetail> reportDetails;
    private Post post;
    private ShortMedia shortMedia;

    private ReportItem reportItem;
    private String firstReporterAvatar;
    public ReportAdapterItem(Reports report){
        this.report = report;
        reportDetails = new ArrayList<>();
        ReportRepository.getInstance().getReportDetails(this).addOnCompleteListener(new OnCompleteListener<Boolean>() {
            @Override
            public void onComplete(@NonNull Task<Boolean> task) {
                ReportAdapterItem.this.getData();
            }
        });
        if(report.getType().equals("POST_ITEM")){
            reportItem = ReportItem.POST_ITEM;
        }
        else if(report.getType().equals("SHORT_ITEM")){
            reportItem = ReportItem.SHORT_ITEM;
        }
    }
    public void addReportDetail(ReportDetail reportDetail){
        reportDetails.add(reportDetail);
    }
    public void removeReportDetail(ReportDetail reportDetail){
        if(reportDetails.contains(reportDetail)){
            reportDetails.remove(reportDetail);
        }
    }
    private void getData(){
        FimaerRepository.getInstance().getFimaerById(reportDetails.get(0).getReporter()).addOnSuccessListener(new OnSuccessListener<Fimaers>() {
            @Override
            public void onSuccess(Fimaers fimaers) {
                firstReporterAvatar = fimaers.getAvatarUrl();
            }
        });

    }
    public Reports getReport() {
        return report;
    }

    public String getFirstReporterAvatar() {
        return firstReporterAvatar;
    }

    public ArrayList<ReportDetail> getReportDetails() {
        return reportDetails;
    }

    public Post getPost() {
        return post;
    }

    public ShortMedia getShortMedia() {
        return shortMedia;
    }

    public ReportItem getReportItem() {
        return reportItem;
    }
}
