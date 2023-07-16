package com.example.fimae.models;

import java.util.Date;
import java.util.Map;

public class SubComment extends CommentBase {
    private String parentId;

    public SubComment(Date timeCreated, Map<String, Boolean> likes, String publisher, String id, String content, String parentId, Date timeEdited) {
        super(timeCreated, likes, publisher, id, content, timeEdited );
        this.parentId = parentId;
    }
    public SubComment(){
        super();
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }
}
