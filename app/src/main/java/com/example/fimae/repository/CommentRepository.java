package com.example.fimae.repository;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.fimae.R;
import com.example.fimae.adapters.CommentAdapter;
import com.example.fimae.adapters.SubCommentAdapter;
import com.example.fimae.databinding.CommentItemBinding;
import com.example.fimae.models.Comment;
import com.example.fimae.models.CommentBase;
import com.example.fimae.models.Fimaers;
import com.example.fimae.models.SubComment;
import com.example.fimae.service.PostService;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class CommentRepository {
    private static CommentRepository commentRepository;
    public static CommentRepository getInstance(){
        if(commentRepository == null) commentRepository = new CommentRepository();
        return commentRepository;
    }
    public CollectionReference getSubCommentRef(String postId, String commentId){
       CollectionReference ref =  FirebaseFirestore.getInstance().collection("posts")
               .document(postId).collection("comments")
               .document(commentId).collection("subcomments");
        return ref;
    }
    public CollectionReference getCommentRef(String postId){
        CollectionReference ref =  FirebaseFirestore.getInstance().collection("posts")
                .document(postId).collection("comments");
        return ref;
    }
    public void postComment(String postId, Comment comment){
        DocumentReference commentId = getCommentRef(postId).document();
        HashMap<String , Object> hashMap = new HashMap<>();
        hashMap.put("content" , comment.getContent());
        hashMap.put("publisher" , comment.getPublisher());
        hashMap.put("id" , commentId.getId());
        hashMap.put("timeCreated", new Timestamp(new Date()));
        hashMap.put("likes", new HashMap<>());
        hashMap.put("postId", comment.getPostId());
        commentId.set(hashMap);
    }
    public void postSubComment(String postId, String parentId, SubComment comment){
        DocumentReference commentId = getSubCommentRef(postId, parentId).document();
        HashMap<String , Object> hashMap = new HashMap<>();
        hashMap.put("content" , comment.getContent());
        hashMap.put("publisher" , comment.getPublisher());
        hashMap.put("timeCreated", new Timestamp(new Date()));
        hashMap.put("likes", new HashMap<>());
        hashMap.put("tags", new ArrayList<>());
        hashMap.put("id", commentId.getId());
        hashMap.put("parentId", comment.getParentId());
        commentId.set(hashMap);
    }
    public void editComment(String postId, CommentBase comment, Context context) {
        if (comment instanceof SubComment) {
            getSubCommentRef(postId, ((SubComment) comment).getParentId())
                    .document(comment.getId())
                    .update("content", comment.getContent(), "timeEdited", new Timestamp(new Date())).addOnCompleteListener(ss->{
                        Toast.makeText(context, "Chỉnh sửa bình luận thành công", Toast.LENGTH_SHORT).show();
                    }).addOnFailureListener(e->{
                        Toast.makeText(context, "Chỉnh sửa bình luận thật bại", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Map<String, Object> updates = new HashMap<>();
            updates.put("content", comment.getContent());
            updates.put("timeEdited", new Timestamp(new Date()));
            getCommentRef(postId).document(comment.getId()).update(updates).addOnCompleteListener(ss -> {
                Toast.makeText(context, "Chỉnh sửa bình luận thành công", Toast.LENGTH_SHORT).show();
            }).addOnFailureListener(e -> {
                Toast.makeText(context, "Chỉnh sửa bình luận thật bại", Toast.LENGTH_SHORT).show();
            });
        }
    }
    public void deleteComment(String postId, CommentBase comment, Context context){
        if(comment instanceof SubComment){
            getSubCommentRef(postId, ((SubComment) comment).getParentId())
                    .document(comment.getId()).delete().addOnCompleteListener(ss->{
                        Toast.makeText(context, "Xóa bình luận thành công", Toast.LENGTH_SHORT).show();
                    }).addOnFailureListener(e->{
                        Toast.makeText(context, "Xóa bình luận thật bại", Toast.LENGTH_SHORT).show();
                    });
        }
        else {
            getCommentRef(postId).document(comment.getId()).delete().addOnCompleteListener(ss->{
                Toast.makeText(context, "Xóa bình luận thành công", Toast.LENGTH_SHORT).show();
            }).addOnFailureListener(e->{
                Toast.makeText(context, "Xóa bình luận thật bại", Toast.LENGTH_SHORT).show();
            });
        }
    }
    private int findCommentById(List<Comment> comments, String targetId) {
        for (Comment comment : comments) {
            if (comment.getId().equals(targetId)) {
                return comments.indexOf(comment);
            }
        }
        return -1; // Comment with the specified ID not found
    }
    private int findSubCommentById(List<SubComment> comments, String targetId) {
        for (SubComment comment : comments) {
            if (comment.getId().equals(targetId)) {
                return comments.indexOf(comment);
            }
        }
        return -1; // Comment with the specified ID not found
    }
    public void getSubComment(String postId, String commentId, List<SubComment> subComments, SubCommentAdapter subCommentAdapter){
        getSubCommentRef(postId, commentId).addSnapshotListener((value, error) -> {
            if (error != null || value == null) {
                return;
            }
            for(DocumentChange dc: value.getDocumentChanges()){
                SubComment comment = dc.getDocument().toObject(SubComment.class);
                switch (dc.getType()){
                    case ADDED:
                        subComments.add(comment);
                        subCommentAdapter.addUpdate();
                        break;
                    case MODIFIED:
                        int i = findSubCommentById(subComments, comment.getId());
                        if(i != -1){
                            if(comment.getContent().equals(subComments.get(i).getContent())) break;
                            subComments.set(i, comment);
                            subCommentAdapter.addModify(i);
                        }
                        break;
                    case REMOVED:
                        int h = findSubCommentById(subComments, comment.getId());
                        subComments.remove(h);
                        subCommentAdapter.notifyItemRemoved(h);
                        break;
                }
            }
//            subComments.clear();
//            int number = 0;
//            for (QueryDocumentSnapshot doc : value) {
//                SubComment subComment = doc.toObject(SubComment.class);
//                subComments.add(subComment);
//            }
//            subCommentAdapter.notifyDataSetChanged();
        });
    }


    public void getComment(String postId, List<Comment> comments, CommentAdapter commentAdapter){
        getCommentRef(postId).addSnapshotListener((value, error) -> {
            if (error != null) {
                return;
            }
            for(DocumentChange dc: value.getDocumentChanges()){
                Comment comment = dc.getDocument().toObject(Comment.class);
                switch (dc.getType()){
                    case ADDED:
                        comments.add(comment);
                        commentAdapter.addUpdate();
                        break;
                    case MODIFIED:
                        int i = findCommentById(comments, comment.getId());
                        if(i != -1){
                            if(comment.getContent().equals(comments.get(i).getContent())) break;
                            comments.set(i, comment);
                            commentAdapter.addModify(i);
                        }
                        break;
                    case REMOVED:
                        int h = findCommentById(comments, comment.getId());
                        comments.remove(h);
                        commentAdapter.notifyItemRemoved(h);
                }
            }
        });
//        getCommentRef(postId).addSnapshotListener((value, error) -> {
//            if (error != null) {
//                return;
//            }
//            comments.clear();
//            int number = 0;
//            for (QueryDocumentSnapshot doc : value) {
//                Comment comment = doc.toObject(Comment.class);
//                comments.add(comment);
//            }
//            commentAdapter.notifyDataSetChanged();
//        });
    }
    public void updateComment(CommentItemBinding binding ,DocumentReference reference, CommentBase current, Fimaers fimaers){
        reference.addSnapshotListener((value, error) -> {
            if (error != null) {
                return;
            }
            Comment updateComment = value.toObject(Comment.class);
            //truong hop xoa thi update comment se = null
            if(updateComment == null) return;
            //hien da chinh sua
            binding.likeNumber.setText(String.valueOf(PostService.getInstance().getNumberOfLikes(updateComment.getLikes())));
            if (updateComment.getLikes().containsKey(current.getPublisher()) && Boolean.TRUE.equals(updateComment.getLikes().get(current.getPublisher()))) {
                binding.heart.setImageResource(R.drawable.ic_heart1);
                binding.heart.setOnClickListener(view -> {
                    String path = "likes." + fimaers.getUid();
                    binding.heart.setImageResource(R.drawable.ic_heart1);
                    reference.update(
                            path, false
                    );
                });
            } else {
                binding.heart.setImageResource(R.drawable.ic_heart_gray);
                binding.heart.setOnClickListener(view -> {
                    String path = "likes." + fimaers.getUid();
                    binding.heart.setImageResource(R.drawable.ic_heart1);
                    reference.update(
                            path, true
                    );
                });
            }
        });
    }
    public void subCommentListener(CommentItemBinding binding, Fimaers fimaers, String postId, String commentId, CommentBase current){
        CollectionReference reference = FirebaseFirestore.getInstance().collection("posts");
        DocumentReference reference1 = reference.document(postId).collection("comments").document(commentId).collection("subcomments").document(current.getId());
        updateComment(binding, reference1, current, fimaers);
    }

    public void commentListener(CommentItemBinding binding, CommentBase current, Fimaers fimaers) {
        Comment comment = (Comment) current;
        CollectionReference reference = FirebaseFirestore.getInstance().collection("posts");
        DocumentReference reference1 = reference.document(comment.getPostId()).collection("comments").document(current.getId());
        updateComment(binding, reference1, current, fimaers);
    }

}
