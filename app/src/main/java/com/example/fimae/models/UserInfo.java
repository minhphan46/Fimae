package com.example.fimae.models;

public class UserInfo {
    private String id;
    private String name;
    private String avatarUrl;
    private int age;
    private boolean isMale;

    public UserInfo(String id, String name, String avatarUrl, int age, boolean isMale) {
        this.id = id;
        this.name = name;
        this.avatarUrl = avatarUrl;
        this.age = age;
        this.isMale = isMale;
    }

    public String getId() {
        return id;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public int getAge() {
        return age;
    }

    public boolean isMale() {
        return isMale;
    }

    public String getName() {
        return name;
    }

    public static UserInfo[] dummy = new UserInfo[]{
            new UserInfo("1","Hào", "https://picsum.photos/200/300?random=1", 25, true),
            new UserInfo("2", "Hào","https://picsum.photos/200/300?random=2", 30, false),
            new UserInfo("3", "Hào","https://picsum.photos/200/300?random=3", 20, true),
    };
}
