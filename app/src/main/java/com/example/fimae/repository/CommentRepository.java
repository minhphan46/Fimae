package com.example.fimae.repository;

import android.content.Context;
import android.widget.Toast;

import com.example.fimae.R;
import com.example.fimae.adapters.CommentAdapter;
import com.example.fimae.adapters.SubCommentAdapter;
import com.example.fimae.databinding.CommentItemBinding;
import com.example.fimae.models.Comment;
import com.example.fimae.models.CommentItemAdapter;
import com.example.fimae.models.Fimaers;
import com.example.fimae.models.shorts.ShortMedia;
import com.example.fimae.service.PostService;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;



public class CommentRepository {
    public static String POST_COLLECTION = "posts";
    private static CommentRepository commentRepository;
    public static CommentRepository getInstance(){
        if(commentRepository == null) commentRepository = new CommentRepository();
        return commentRepository;
    }
    public CollectionReference getCommentRef(String postId, String collection){
        CollectionReference ref =  FirebaseFirestore.getInstance().collection(collection)
                .document(postId).collection("comments");
        return ref;
    }
    public Task<Boolean> postComment(String postId, String collection, Comment comment) {
        TaskCompletionSource<Boolean> taskCompletionSource = new TaskCompletionSource<>();
        DocumentReference commentId = getCommentRef(postId, collection).document();
        comment.setId(commentId.getId());
        comment.setLikes(new HashMap<>());
        comment.setChildren(new ArrayList<>());
        comment.setTimeCreated(new Date());
        comment.setPublisher(FirebaseAuth.getInstance().getUid());
        commentId.set(comment).addOnCompleteListener(ss -> taskCompletionSource.setResult(true))
                .addOnFailureListener(f -> taskCompletionSource.setResult(false));
        return taskCompletionSource.getTask();
    }

    public Task<Boolean> editComment(String postId, Comment comment, String content) {
        TaskCompletionSource<Boolean> taskCompletionSource = new TaskCompletionSource<>();
        Map<String, Object> updates = new HashMap<>();
            updates.put("content", content);
            updates.put("timeEdited", new Timestamp(new Date()));
            getCommentRef(postId, POST_COLLECTION).document(comment.getId()).update(updates).addOnCompleteListener(ss -> {
                taskCompletionSource.setResult(true);
            }).addOnFailureListener(e -> {
                taskCompletionSource.setResult(false);
            });
            return taskCompletionSource.getTask();
    }
    public Task<Boolean> deleteComment(String postId, Comment comment){
        TaskCompletionSource<Boolean> taskCompletionSource = new TaskCompletionSource<>();
        getCommentRef(postId, POST_COLLECTION).document(comment.getId()).delete().addOnCompleteListener(ss->{
                taskCompletionSource.setResult(true);
            }).addOnFailureListener(e->{
                taskCompletionSource.setResult(false);
            });
        return taskCompletionSource.getTask();

    }
    private int findCommentItemAdapterById(List<CommentItemAdapter> commentItemAdapters, String targetId) {
        for (CommentItemAdapter commentItem : commentItemAdapters) {
            if (commentItem.getComment().getId().equals(targetId)) {
                return commentItemAdapters.indexOf(commentItem);
            }
        }
        return -1; // Comment with the specified ID not found
    }
    public void getSubComment(CommentItemAdapter commentItem){
        Comment rootComment = commentItem.getComment();
        getCommentRef(rootComment.getPostId(), POST_COLLECTION).whereEqualTo("parentId", rootComment.getId()).addSnapshotListener((value, error) -> {
            if (error != null || value == null) {
                return;
            }
            for(DocumentChange dc: value.getDocumentChanges()){
                Comment comment = dc.getDocument().toObject(Comment.class);
                switch (dc.getType()){
                    case ADDED:
                        commentItem.addNewSubComment(comment);
                        break;
                    case MODIFIED:
                        break;
                    case REMOVED:
                        commentItem.removeSubComment(comment);
                }
            }
        });
    }

    public void getComment(String postId, List<CommentItemAdapter> comments, CommentAdapter commentAdapter) {
        getCommentRef(postId, POST_COLLECTION).whereEqualTo("parentId", "").addSnapshotListener((value, error) -> {
            if (error != null) {
                return;
            }
            assert value != null;
            for (DocumentChange dc : value.getDocumentChanges()) {
                Comment comment = dc.getDocument().toObject(Comment.class);
                switch (dc.getType()) {
                    case ADDED:
                        CommentItemAdapter commentItemAdapter = new CommentItemAdapter(comment);
                        comments.add(commentItemAdapter);
                        commentAdapter.addUpdate();
                        break;
                    case MODIFIED:
                        break;
                    case REMOVED:
                        int h = findCommentItemAdapterById(comments, comment.getId());
                        comments.remove(h);
                        commentAdapter.notifyItemRemoved(h);
                }
            }
        });
    }
}