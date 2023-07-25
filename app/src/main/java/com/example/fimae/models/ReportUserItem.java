//package com.example.fimae.models;
//
//import androidx.annotation.NonNull;
//
//import com.example.fimae.repository.FimaerRepository;
//import com.example.fimae.repository.ReportRepository;
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.android.gms.tasks.Task;
//
//import java.util.ArrayList;
//
//public class ReportUserItem {
//    Reports report;
//    Fimaers fimaers;
//    ArrayList<ReportDetail> reportDetails;
//    private String firstReporterAvatar;
//
//    public ReportUserItem(Reports report){
//        this.report = report;
//        FimaerRepository.getInstance().getFimaerById(report.getUid()).addOnSuccessListener(new OnSuccessListener<Fimaers>() {
//            @Override
//            public void onSuccess(Fimaers fimaers) {
//                ReportUserItem.this.fimaers = fimaers;
//            }
//        });
//        FimaerRepository.getInstance().getFimaerById(reportDetails.get(0).getReporter()).addOnSuccessListener(new OnSuccessListener<Fimaers>() {
//            @Override
//            public void onSuccess(Fimaers fimaers) {
//                firstReporterAvatar = fimaers.getAvatarUrl();
//            }
//        });
//        ReportRepository.getInstance().getReportDetails(report).addOnCompleteListener(new OnCompleteListener<ArrayList<ReportDetail>>() {
//            @Override
//            public void onComplete(@NonNull Task<ArrayList<ReportDetail>> task) {
//                reportDetails = task.getResult();
//            }
//        });
//    }
//    public String getFirstReporterAvatar() {
//        return firstReporterAvatar;
//    }
//
//    public Reports getReport() {
//        return report;
//    }
//
//    public Fimaers getFimaers() {
//        return fimaers;
//    }
//
//    public ArrayList<ReportDetail> getReportDetails() {
//        return reportDetails;
//    }
//}
