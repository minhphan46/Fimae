package com.example.fimae.models.dating;

public enum EducationalLevel {
    UNIMPORTANT("Không quan trọng"),
    HIGH_SCHOOL("Bằng trung học"),
    COLLEGE("Bằng cao đẳng/đại học"),
    GRADUATE("Bằng cao học");

    private final String displayName;

    EducationalLevel(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
