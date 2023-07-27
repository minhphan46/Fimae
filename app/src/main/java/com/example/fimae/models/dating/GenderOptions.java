package com.example.fimae.models.dating;

public enum GenderOptions {
    MALE("Nam"),
    FEMALE("Nữ"),
    UNIMPORTANT("Không quan trọng");

    String displayName;
    GenderOptions(String displayName) {
        this.displayName = displayName;
    }
    public String getDisplayName() {
        return displayName;
    }
}
