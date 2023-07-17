package com.example.fimae.service;

import android.net.Uri;
import android.util.Log;

import com.example.fimae.utils.FileUtils;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class FirebaseService {
    private static FirebaseService instance;
    public static FirebaseService getInstance() {
        if(instance == null) {
            instance = new FirebaseService();
        }
        return instance;
    }
    public UploadTask uploadFile(String path, Uri uri) {
        return FirebaseStorage.getInstance().getReference(path).putFile(Uri.fromFile(new File(String.valueOf(uri))));
    }
     public UploadTask uploadFile(String path, String fileName, byte[] data) {
        return FirebaseStorage.getInstance().getReference(path).child(fileName).putBytes(data);
    }
     public CompletableFuture<List<String>> uploadTaskFiles(String path, ArrayList<Uri> fileUris) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference(path);
        List<CompletableFuture<String>> futures = new ArrayList<>();

        for (Uri uri : fileUris) {
            String fileNameFromUri = FileUtils.getFileNameFromUri(uri);
            try {
                StorageReference fileRef = storageRef.child(fileNameFromUri);
                UploadTask uploadTask = uploadFile(path, uri);

                CompletableFuture<String> future = new CompletableFuture<>();
                uploadTask
                        .addOnSuccessListener(taskSnapshot -> {
                            // Get the download URL
                            fileRef.getDownloadUrl().addOnSuccessListener(downloadUrl -> {
                                future.complete(downloadUrl.toString());
                            });
                        })
                        .addOnFailureListener(future::completeExceptionally);

                futures.add(future);
            } catch (Exception e) {
                Log.d("FirebaseService", "Error: " + e.getMessage());
                e.printStackTrace();
                Log.e("FirebaseService", "Error: " + e.getMessage());
            }
        }

        CompletableFuture<Void> allTasks = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

        return allTasks.thenApply(
                ignored -> futures.stream()
                        .map(CompletableFuture::join)
                        .collect(Collectors.toList())
        );
    }
     public  CompletableFuture<List<String>> uploadTaskImages(String path, ArrayList<String> imagePaths) {
       return uploadTaskFiles(path, imagePaths.stream().map(Uri::parse).collect(Collectors.toCollection(ArrayList::new)));
    }
}
