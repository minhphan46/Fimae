//package com.example.fimae.models;
//
//import java.util.Date;
//import java.util.List;
//import java.util.Map;
//
//public class SubComment extends Comment {
//    private String parentId;
//    private List<String> children;
//    public SubComment(Date timeCreated, Map<String, Boolean> likes, String publisher, String id, String content, String parentId, List<String> children,Date timeEdited) {
//        super(timeCreated, likes, publisher, id, content, timeEdited );
//        this.parentId = parentId;
//        this.children = children;
//    }
//    public SubComment(){
//        super();
//    }
//
//    public String getParentId() {
//        return parentId;
//    }
//
//    public void setParentId(String parentId) {
//        this.parentId = parentId;
//    }
//
//    public List<String> getChildren() {
//        return children;
//    }
//    public void setChildren(List<String> children) {
//        this.children = children;
//    }
//}
