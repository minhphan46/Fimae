package com.example.fimae.models;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class ReportDetail {
    private String reporter;
    private String description;
    private String reason;
    @ServerTimestamp
    private Date timeCreated;

    private String idReport;

    private String uid;

    public ReportDetail(String reporter, String description, String reason, String idReport, String uid) {
        this.reporter = reporter;
        this.description = description;
        this.reason = reason;
        this.idReport = idReport;
        this.uid = uid;
    }

    public String getIdReport() {
        return idReport;
    }

    public void setIdReport(String idReport) {
        this.idReport = idReport;
    }

    public String getReporter() {
        return reporter;
    }

    public void setReporter(String reporter) {
        this.reporter = reporter;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Date getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(Date timeCreated) {
        this.timeCreated = timeCreated;
    }
}
