package com.example.fimae.models;

import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.Objects;

public class Message {
    static final public String TEXT = "TEXT";
    static final public String MEDIA = "MEDIA";
    static final public String AUDIO = "AUDIO";
    static final public String CHANGE_NICKNAME = "CHANGE_NICKNAME";
    static final public String LEAVE_CONVERSATION = "LEAVE_CONVERSATION";
    private String id;
    private String conversationID;
    private String idSender; // người gửi tin nhắn
    private String type; // nội dung tin nhắn
    private Timestamp sentAt; // thời điểm gửi tin nhắn
    private ArrayList<String> deleteFromUsers;
    private boolean isHideForAllUsers;
    private Object content;
    public Message(){}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getConversationID() {
        return conversationID;
    }

    public void setConversationID(String conversationID) {
        this.conversationID = conversationID;
    }

    public String getIdSender() {
        return idSender;
    }

    public void setIdSender(String idSender) {
        this.idSender = idSender;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        assert Objects.equals(type, TEXT) || Objects.equals(type, MEDIA) || Objects.equals(type, AUDIO) || Objects.equals(type, CHANGE_NICKNAME) || Objects.equals(type, LEAVE_CONVERSATION);
        this.type = type;
    }

    public Timestamp getSentAt() {
        return sentAt;
    }

    public void setSentAt(Timestamp sentAt) {
        this.sentAt = sentAt;
    }

    public ArrayList<String> getDeleteFromUsers() {
        return deleteFromUsers;
    }

    public void setDeleteFromUsers(ArrayList<String> deleteFromUsers) {
        this.deleteFromUsers = deleteFromUsers;
    }

    public boolean isHideForAllUsers() {
        return isHideForAllUsers;
    }

    public void setHideForAllUsers(boolean hideForAllUsers) {
        isHideForAllUsers = hideForAllUsers;
    }

    public Object getContent() {
        return content;
    }

    public void setContent(Object content) {
        this.content = content;
    }
}
