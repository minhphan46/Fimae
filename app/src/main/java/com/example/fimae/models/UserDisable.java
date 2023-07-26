package com.example.fimae.models;

import com.google.firebase.firestore.ServerTimestamp;
import com.google.type.DateTime;

import java.util.Date;

public class UserDisable {
    @ServerTimestamp
    Date timeCreated;

    String reason;
    Date timeEnd;
    String userId;
    public UserDisable(){}


    public UserDisable(Date timeCreated, String reason, Date timeEnd, String userId) {
        this.timeCreated = timeCreated;
        this.reason = reason;
        this.timeEnd = timeEnd;
        this.userId = userId;
    }

    public Date getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(Date timeCreated) {
        this.timeCreated = timeCreated;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Date getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(Date timeEnd) {
        this.timeEnd = timeEnd;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
