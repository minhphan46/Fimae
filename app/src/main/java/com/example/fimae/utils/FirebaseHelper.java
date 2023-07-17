package com.example.fimae.utils;

import android.net.Uri;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;
/**
 * @deprecated Đừng dùng cái này nữa, dùng cái FirebaseService thay thế
 */
@Deprecated
public class FirebaseHelper {
    private FirebaseStorage firebaseStorage;

    public FirebaseHelper() {
        firebaseStorage = FirebaseStorage.getInstance();
    }

    public interface UploadCallback {
        void onUploadComplete(ArrayList<String> downloadUrls);
    }

    public void uploadMultipleFiles(List<Uri> uris, UploadCallback callback, String ref, String child) {
        final ArrayList<String> downloadUrls = new ArrayList<>();
        final int totalFiles = uris.size();
        final AtomicInteger uploadedFiles = new AtomicInteger(0);

        for (Uri uri : uris) {
            String fileName = getFileName(uri);
            StorageReference storageReference = firebaseStorage.getReference(ref).child(child + "/" + fileName);
            storageReference.putFile(Uri.fromFile(new File(String.valueOf(uri))))
                    .addOnSuccessListener(taskSnapshot -> storageReference.getDownloadUrl()
                            .addOnSuccessListener(downloadUri -> {
                                String url = downloadUri.toString();
                                downloadUrls.add(url);
                                checkUploadCompletion(callback, totalFiles, uploadedFiles, downloadUrls);
                            })
                            .addOnFailureListener(exception -> {
                                uploadedFiles.incrementAndGet();
                                checkUploadCompletion(callback, totalFiles, uploadedFiles, downloadUrls);
                            }))
                    .addOnFailureListener(exception -> {
                        uploadedFiles.incrementAndGet();
                        checkUploadCompletion(callback, totalFiles, uploadedFiles, downloadUrls);
                    });
        }
    }

    public void uploadBytesToFirebase(byte[] bytes, UploadCallback callback, String ref, String child) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + ".jpg";
        StorageReference storageReference = firebaseStorage.getReference(ref).child(child + "/" + imageFileName);
        final ArrayList<String> downloadUrls = new ArrayList<>();

        storageReference.putBytes(bytes)
                .addOnSuccessListener(taskSnapshot -> storageReference.getDownloadUrl()
                        .addOnSuccessListener(downloadUri -> {
                            String url = downloadUri.toString();
                            downloadUrls.add(url);
                            callback.onUploadComplete(downloadUrls);
                        }))
                .addOnFailureListener(exception -> callback.onUploadComplete(downloadUrls));
    }

    private void checkUploadCompletion(UploadCallback callback, int totalFiles, AtomicInteger uploadedFiles, ArrayList<String> downloadUrls) {
        if (uploadedFiles.incrementAndGet() == totalFiles) {
            callback.onUploadComplete(downloadUrls);
        }
    }

    private String getFileName(Uri uri) {
        String path = uri.getPath();
        return path.substring(path.lastIndexOf("/") + 1);
    }
}

