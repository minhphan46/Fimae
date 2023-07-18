package com.example.fimae.models.dating;

public enum RelationshipType {
    UNIMPORTANT("Không quan trọng"),
    CHAT("Người trò chuyện"),
    FRIENDS("Quan hệ bạn bè"),
    CASUAL_DATING("Kiểu hẹn hò không ràng buộc"),
    LONG_TERM("Mối quan hệ lâu dài");

    private final String displayName;

    RelationshipType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
