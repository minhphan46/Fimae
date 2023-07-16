package com.example.fimae.models;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Report {
    private String uid; // id báo cáo
    private String uidReportedUser; // id người bị báo cáo
    private String uidReporter; // id người báo cáo
    @ServerTimestamp
    private Date timeCreated;
    private String content;

    public Report(){}
    public Report(String uid, String uidReportedUser, String uidReporter, Date timeCreated, String content) {
        this.uid = uid;
        this.uidReportedUser = uidReportedUser;
        this.uidReporter = uidReporter;
        this.timeCreated = timeCreated;
        this.content = content;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUidReportedUser() {
        return uidReportedUser;
    }

    public void setUidReportedUser(String uidReportedUser) {
        this.uidReportedUser = uidReportedUser;
    }

    public String getUidReporter() {
        return uidReporter;
    }

    public void setUidReporter(String uidReporter) {
        this.uidReporter = uidReporter;
    }

    public Date getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(Date timeCreated) {
        this.timeCreated = timeCreated;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
