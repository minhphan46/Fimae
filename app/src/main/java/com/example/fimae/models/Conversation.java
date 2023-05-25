package com.example.fimae.models;

import java.util.*;

public class Conversation {
    public static final String FRIEND_CHAT = "friend-chat";
    public static final String GROUP_CHAT = "group-chat";
    String id;
    Date created_at;
    String type;
    String name;
    ArrayList<String> memberIds;

    public Conversation(){}

    public Conversation(String id, Date created_at, String type, String name, ArrayList<String> memberIds) {
        this.id = id;
        this.created_at = created_at;
        this.type = type;
        assert (Objects.equals(type, FRIEND_CHAT) || Objects.equals(type, GROUP_CHAT));
        this.name = name;
        this.memberIds = memberIds;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
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

    public ArrayList<String> getMemberIds() {
        return memberIds;
    }

    public void setMemberIds(ArrayList<String> membersId) {
        this.memberIds = membersId;
    }
}
