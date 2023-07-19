package com.example.fimae.models;

import com.example.fimae.adapters.SubCommentAdapter;

import java.util.ArrayList;
import java.util.List;

public class CommentItemAdapter{
    private Comment comment;
    private List<Comment> subComment(){
        return adapter.getmSubComment();
    }
    private SubCommentAdapter adapter;

    public CommentItemAdapter(Comment comment){
        this.comment = comment;
        adapter = new SubCommentAdapter();
    }
    public void addNewSubComment(Comment comment){
        subComment().add(comment);

        if(adapter != null){
            adapter.notifyItemInserted(subComment().size() - 1);
        }
    }
    public void modifyComment(Comment comment){
        this.comment = comment;
    }

    public Comment getComment(){
        return this.comment;
    }
    private int findCommentItem(String id){
        for(int i = 0; i < subComment().size(); i++){
            if(subComment().get(i).getId().equals(id)){
                return i;
            }
        }
        return -1;
    }
    public void modifySubComment(Comment comment){
        int i = findCommentItem(comment.getId());
        if(i  == -1) return;
        subComment().set(i, comment);
        adapter.notifyItemChanged(i);
    }
    public void removeSubComment(Comment comment){
        int i = findCommentItem(comment.getId());
        if(i  == -1) return;
        subComment().remove(i);
        adapter.notifyItemRemoved(i);
    }
    public void setSubAdapter(SubCommentAdapter adapter){
        this.adapter = adapter;
    }
    public SubCommentAdapter getSubAdapter(){
        return this.adapter;
    }
    public List<Comment> getSubComment(){
        return this.subComment();
    }
}