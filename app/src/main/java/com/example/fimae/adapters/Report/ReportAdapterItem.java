package com.example.fimae.adapters.Report;

import java.io.Serializable;

public class ReportAdapterItem implements Serializable {
    String id;
    String title;
    boolean isChecked;

    public ReportAdapterItem(String id, String title) {
        this.id = id;
        this.title = title;
        this.isChecked = false;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
