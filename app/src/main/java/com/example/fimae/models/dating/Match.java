package com.example.fimae.models.dating;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class Match {
    private String id;
    private @ServerTimestamp Date timeMatched;
    ArrayList<String> matchedUsers;

    public Match() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getTimeMatched() {
        return timeMatched;
    }

    public void setTimeMatched(Date timeMatched) {
        this.timeMatched = timeMatched;
    }

    public ArrayList<String> getMatchedUsers() {
        return matchedUsers;
    }

    public void setMatchedUsers(ArrayList<String> matchedUsers) {
        Collections.sort(matchedUsers);
        this.matchedUsers = matchedUsers;
    }
}
