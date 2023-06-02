package com.example.fimae.models;

public class BottomSheetItem {
    int iconResourceId;
    String title;

    public BottomSheetItem(int iconResourceId, String title) {
        this.iconResourceId = iconResourceId;
        this.title = title;
    }

    public int getIconResourceId() {
        return iconResourceId;
    }

    public void setIconResourceId(int iconResourceId) {
        this.iconResourceId = iconResourceId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
