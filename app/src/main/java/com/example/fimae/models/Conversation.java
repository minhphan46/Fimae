package com.example.fimae.models;

import android.content.Intent;
import com.google.firebase.Timestamp;
import com.google.type.DateTime;
import org.apache.http.entity.SerializableEntity;

import java.util.*;

public class Conversation {
    public static final String FRIEND_CHAT = "friend-chat";
    public static final String GROUP_CHAT = "group-chat";
    String id;
    String isBlockedBy;
    Timestamp createdAt;
    String type;
    String name;
    String lastMessageId;
    ArrayList<String> participantIDs;
    public Conversation(){

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIsBlockedBy() {
        return isBlockedBy;
    }

    public void setIsBlockedBy(String isBlockedBy) {
        this.isBlockedBy = isBlockedBy;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastMessageId() {
        return lastMessageId;
    }

    public void setLastMessageId(String lastMessageId) {
        this.lastMessageId = lastMessageId;
    }

    public ArrayList<String> getParticipantIDs() {
        return participantIDs;
    }

    public void setParticipantIDs(ArrayList<String> participantIDs) {
        this.participantIDs = participantIDs;
    }
}
