package com.example.fimae.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.ServerTimestamp;
import com.google.type.DateTime;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Participant{
    private String uid;
    private String role;

    private DateTime joinedAt;

    public Participant() {

    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {

        this.uid = uid;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public DateTime getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(DateTime joinedAt) {
        this.joinedAt = joinedAt;
    }
}
