package com.example.fimae.models;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Reports {
    private String uid; // id báo cáo
    private String type;
    @ServerTimestamp
    private Date timeCreated;
    public Reports(){}

    public Reports(String uid, String type) {
        this.uid = uid;
        this.type = type;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(Date timeCreated) {
        this.timeCreated = timeCreated;
    }

}
