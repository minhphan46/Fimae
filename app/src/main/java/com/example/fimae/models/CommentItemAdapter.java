package com.example.fimae.models;

import com.example.fimae.adapters.SubCommentAdapter;

import java.util.ArrayList;
import java.util.List;

public class CommentItemAdapter{
    private Comment comment;
    private List<CommentItemAdapter> subComment;
    private SubCommentAdapter adapter;

    public CommentItemAdapter(Comment comment){
        this.comment = comment;
        subComment = new ArrayList<>();
    }
    public void addNewSubComment(Comment comment){
        subComment.add(new CommentItemAdapter(comment));

        if(adapter != null){
            adapter.notifyItemInserted(subComment.size() - 1);
        }
    }
    public void modifyComment(Comment comment){
        this.comment = comment;
    }
    public Comment getComment(){
        return this.comment;
    }
    private int findCommentItem(String id){
        for(int i = 0; i < subComment.size(); i++){
            if(subComment.get(i).comment.getId().equals(id)){
                return i;
            }
        }
        return -1;
    }
    public void modifySubComment(Comment comment){
        int i = findCommentItem(comment.getId());
        if(i  == -1) return;
        subComment.get(i).modifyComment(comment);
        adapter.notifyItemChanged(i);
    }
    public void removeSubComment(Comment comment){
        int i = findCommentItem(comment.getId());
        if(i  == -1) return;
        subComment.remove(i);
        adapter.notifyItemRemoved(i);
    }
    public void setSubAdapter(SubCommentAdapter adapter){
        this.adapter = adapter;
    }
    public List<CommentItemAdapter> getSubComment(){
        return this.subComment;
    }
}