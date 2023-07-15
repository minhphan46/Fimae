package com.example.fimae.models;

import androidx.annotation.IntDef;
import androidx.annotation.StringDef;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.ServerTimestamp;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.*;

public class Conversation {
    public static final String FRIEND_CHAT = "friend-chat";
    public static final String GROUP_CHAT = "group-chat";
    private String id;
    private String blockedBy;
    @ServerTimestamp
    private Date createdAt;
    private String type;
    private String name;
    private DocumentReference lastMessage;
    ArrayList<String> participantIds = new ArrayList<> ();
    public Conversation(){

    }

    public static Conversation create(String id, String type, ArrayList<String> participantIds){
        Conversation conversation = new Conversation();
        conversation.setId(id);
        conversation.setType(type);
        Collections.sort(participantIds);
        conversation.setParticipantIds(participantIds);
        return conversation;
    }

    public ArrayList<String> getParticipantIds() {
        return participantIds;
    }

    public void setParticipantIds(ArrayList<String> participantIds) {
        this.participantIds = participantIds;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBlockedBy() {
        return blockedBy;
    }

    public void setBlockedBy(String blockedBy) {
        this.blockedBy = blockedBy;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
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

    public DocumentReference getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(DocumentReference lastMessage) {
        this.lastMessage = lastMessage;
    }
}
