package com.example.fimae.models;


import com.google.firebase.Timestamp;
import java.util.ArrayList;

public class Calls {
    private Timestamp createdAt;
    private ArrayList<String> participantIDs;

    public  Calls(){}

    public Calls(Timestamp createdAt, ArrayList<String> participantIDs) {
        this.createdAt = createdAt;
        this.participantIDs = participantIDs;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public ArrayList<String> getParticipantIDs() {
        return participantIDs;
    }

    public void setParticipantIDs(ArrayList<String> participantIDs) {
        this.participantIDs = participantIDs;
    }
}
