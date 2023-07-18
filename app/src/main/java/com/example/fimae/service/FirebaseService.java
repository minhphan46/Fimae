package com.example.fimae.service;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.fimae.utils.FileUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
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
    public UploadTask uploadFile(String storagePath, Uri uri) {
        String filename = UUID.randomUUID().toString() +"." + FileUtils.getFileExtension(uri.toString());
        StorageReference fileRef = FirebaseStorage.getInstance().getReference(storagePath).child(filename);
        return fileRef.putFile(Uri.fromFile(new File(uri.toString())));
    }
     public UploadTask uploadFile(String storagePath, String fileName, byte[] data) {
        return FirebaseStorage.getInstance().getReference(storagePath).child(fileName).putBytes(data);
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
                            taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(downloadUrl -> {
                                future.complete(downloadUrl.toString());
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("FirebaseService", "Error: " + e.getMessage());
                                    e.printStackTrace();
                                    future.completeExceptionally(e);
                                }
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
}
