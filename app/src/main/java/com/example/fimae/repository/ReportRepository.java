package com.example.fimae.repository;

import androidx.annotation.NonNull;

import com.example.fimae.models.Report;
import com.example.fimae.models.ReportDetail;
import com.example.fimae.models.Reports;
import com.example.fimae.utils.ReportItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ReportRepository {
    private static ReportRepository repository;

    public static ReportRepository getInstance(){
        if(repository == null){
            repository = new ReportRepository();
        }
        return  repository;
    }
    private CollectionReference reportRef = FirebaseFirestore.getInstance().collection("reports");

    public CollectionReference getReportDetailRef(String docId){
        return FirebaseFirestore.getInstance().collection("reports").document(docId).collection("reportdetails");
    }
    public Task<Boolean> addNewReport(String docId, ReportItem typeItem, String reason, String description){
        TaskCompletionSource<Boolean> taskCompletionSource = new TaskCompletionSource<>();
        reportRef.document(docId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Report report = document.toObject(Report.class);
                        DocumentReference documentReference = getReportDetailRef(report.getUid()).document();
                        ReportDetail reportDetail = new ReportDetail(FirebaseAuth.getInstance().getUid(), description, reason, report.getUid(), documentReference.getId());
                        documentReference.set(reportDetail).addOnCompleteListener(task1 -> taskCompletionSource.setResult(true))
                                .addOnFailureListener(e -> taskCompletionSource.setResult(false));

                    } else {
                        Reports report = new Reports(docId, typeItem.toString());
                        reportRef.document(docId).set(report).addOnCompleteListener(task12 -> {
                            if (task12.isSuccessful()) {
                                DocumentReference documentReference = getReportDetailRef(report.getUid()).document();
                                ReportDetail reportDetail = new ReportDetail(FirebaseAuth.getInstance().getUid(), description, reason, report.getUid(), documentReference.getId());
                                documentReference.set(reportDetail).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task12) {
                                        taskCompletionSource.setResult(true);
                                    }
                                }).addOnFailureListener(e -> taskCompletionSource.setResult(false));
                            } else {
                                taskCompletionSource.setResult(false);
                            }
                        });
                    }
                } else {
                    taskCompletionSource.setResult(false);
                }
            }
        });

        return taskCompletionSource.getTask();
    }
}
