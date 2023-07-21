package com.example.fimae.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import androidx.annotation.RequiresApi;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FileUtils {
    public static boolean isVideoFile(String filePath) {
        String extension = getFileExtension(filePath);
        return extension.startsWith("mp4") || extension.startsWith("avi")
                || extension.startsWith("mkv") || extension.startsWith("mov")
                || extension.startsWith("wmv");
    }

    public static boolean isImageFile(String filePath) {
        String extension = getFileExtension(filePath);
        return extension.startsWith("jpg") || extension.startsWith("jpeg")
                || extension.startsWith("png") || extension.startsWith("gif")
                || extension.startsWith("bmp");
    }

    public static String getFileExtension(String filePath) {
        if (filePath != null && filePath.lastIndexOf(".") != -1) {
            return filePath.substring(filePath.lastIndexOf(".") + 1);
        }
        return "";
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public static long getVideoDuration(String videoPath) throws IOException {
        try (MediaMetadataRetriever retriever = new MediaMetadataRetriever()) {
            retriever.setDataSource(videoPath);
            String durationString = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            retriever.release();
            return Long.parseLong(durationString);
        } catch (Exception exception){
            exception.printStackTrace();
            return  -1;
        }
    }
    public static String formatDuration(long duration) {
        long seconds = (duration / 1000) % 60;
        long minutes = (duration / (1000 * 60)) % 60;

        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }
    public static ArrayList<String> getAllMediaPaths(Context context) {
        ArrayList<String> mediaPaths = new ArrayList<>();

        String[] projection = {MediaStore.MediaColumns.DATA, MediaStore.MediaColumns.MIME_TYPE};
        String selection = MediaStore.Files.FileColumns.MEDIA_TYPE + " IN (" +
                MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE + "," +
                MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO + ")";

        Uri queryUri = MediaStore.Files.getContentUri("external");
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(queryUri, projection, selection, null, null);

        if (cursor != null) {
            try {
                int pathColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                while (cursor.moveToNext()) {
                    String path = cursor.getString(pathColumnIndex);
                    mediaPaths.add(path);
                }
            } finally {
                cursor.close();
            }
        }

        return mediaPaths;
    }
    public static byte[] getBytesFromBitmap(Bitmap bitmap, int quality) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream);
        // bitmap.compress(Bitmap.CompressFormat.PNG, quality, stream); // According to the needed quality
        return stream.toByteArray();
    }
     static public String getFileNameFromUri(Uri uri) {
        String path = uri.getPath();
        return path.substring(path.lastIndexOf("/") + 1);
    }
    public static String getFilePathFromContentUri(Context context, Uri contentUri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = null;

        try {
            ContentResolver contentResolver = context.getContentResolver();
            cursor = contentResolver.query(contentUri, projection, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                return cursor.getString(columnIndex);
            }
        } catch (Exception e) {
            // Handle the exception, if any
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return null;
    }
}
