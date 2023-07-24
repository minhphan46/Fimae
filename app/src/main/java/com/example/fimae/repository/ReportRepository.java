package com.example.fimae.repository;

import com.example.fimae.adapters.Report.ReportType;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class ReportRepository {
    FirebaseFirestore firestore;
    CollectionReference collectionReference;

    private ReportRepository(ReportType reportType) {
        firestore = FirebaseFirestore.getInstance();
        collectionReference = firestore.collection("reports" + "/" + reportType.toString().toLowerCase());
    }

    public static ReportRepository getReportRepository(ReportType reportType) {
        return new ReportRepository(reportType);
    }

    public Task<Boolean> report(String reportedId, String reason, String description) {
        TaskCompletionSource<Boolean> taskCompletionSource = new TaskCompletionSource<>();
        DocumentReference documentReference = collectionReference.document();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id", documentReference.getId());
        hashMap.put("reporterId", FirebaseAuth.getInstance().getCurrentUser().getUid());
        hashMap.put("reportedId", reportedId);
        hashMap.put("reason", reason);
        hashMap.put("description", description);
        hashMap.put("time", FieldValue.serverTimestamp());
        documentReference.set(hashMap).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                taskCompletionSource.setResult(true);
            } else {
                taskCompletionSource.setResult(false);
            }
        });
        return taskCompletionSource.getTask();
    }
}
