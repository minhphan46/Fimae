package com.example.fimae.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Participant{
    public static final String ROLE_ADMIN = "admin";
    public static final String ROLE_Participant = "participant";
    private String uid;
    private String role;

    private String nickname;
    private @ServerTimestamp Date joinedAt;
    private  @ServerTimestamp Date readLastMessageAt;

    public static Participant create(String uid, String role){
        Participant participant = new Participant();
        participant.setUid(uid);
        participant.setRole(role);
        return participant;
    }

    public Participant() {

    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Date getReadLastMessageAt() {
        return readLastMessageAt;
    }

    public void setReadLastMessageAt(Date readLastMessageAt) {
        this.readLastMessageAt = readLastMessageAt;
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

    public Date getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(Date joinedAt) {
        this.joinedAt = joinedAt;
    }
}
