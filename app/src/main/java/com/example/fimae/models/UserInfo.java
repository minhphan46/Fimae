package com.example.fimae.models;

public class UserInfo {
    private String id;
    private String name;
    private String avatarUrl;
    private int age;
    private boolean isMale;
    private String description;

    public UserInfo(String id, String name, String avatarUrl, int age, boolean isMale, String description) {
        this.id = id;
        this.name = name;
        this.avatarUrl = avatarUrl;
        this.age = age;
        this.isMale = isMale;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public boolean isMale() {
        return isMale;
    }

    public void setMale(boolean male) {
        isMale = male;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static UserInfo[] dummy = new UserInfo[]{
            new UserInfo("1","Minh", "https://picsum.photos/200/300?random=1", 25, true, "lanh lung ik loi"),
            new UserInfo("2", "Hào","https://picsum.photos/200/300?random=2", 21, false, "hello word"),
            new UserInfo("3", "Anh","https://picsum.photos/200/300?random=3", 23, true, "de thuong"),
            new UserInfo("4", "Hiển","https://picsum.photos/200/300?random=4", 24, false, "ngu ngok"),
    };
}
