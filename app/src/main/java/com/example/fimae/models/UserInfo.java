package com.example.fimae.models;

import androidx.annotation.Nullable;

public class UserInfo {
    private String id;
    private String name;
    private String avatarUrl;
    private int age;
    private boolean isMale;
    private String description;
    @Nullable private String token;

    public UserInfo(){
    }

    public UserInfo(String id, String name, String avatarUrl, int age, boolean isMale, String description) {
        this.id = id;
        this.name = name;
        this.avatarUrl = avatarUrl;
        this.age = age;
        this.isMale = isMale;
        this.description = description;
    }

    public UserInfo(String id, String name, String avatarUrl, int age, boolean isMale, String description, @Nullable String token) {
        this.id = id;
        this.name = name;
        this.avatarUrl = avatarUrl;
        this.age = age;
        this.isMale = isMale;
        this.description = description;
        this.token = token;
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

    @Override
    public String toString() {
        return "UserInfo{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", avatarUrl='" + avatarUrl + '\'' +
                ", age=" + age +
                ", isMale=" + isMale +
                ", description='" + description + '\'' +
                ", token='" + token + '\'' +
                '}';
    }

    @Nullable
    public String getToken() {
        return token;
    }

    public void setToken(@Nullable String token) {
        this.token = token;
    }

    public static UserInfo[] dummy = new UserInfo[]{
            new UserInfo(
                    "1",
                    "minh",
                    "https://picsum.photos/200/300?random=1",
                    25,
                    true,
                    "lanh lung ik loi",
                    "eyJjdHkiOiJzdHJpbmdlZS1hcGk7dj0xIiwidHlwIjoiSldUIiwiYWxnIjoiSFMyNTYifQ.eyJqdGkiOiJTSy4wLnM1OFRaMnBJbkIwMFdWMlZmTlQ1RXRmU2xLQ2g3cy0xNjgyODk0NzE0IiwiaXNzIjoiU0suMC5zNThUWjJwSW5CMDBXVjJWZk5UNUV0ZlNsS0NoN3MiLCJleHAiOjE2ODU0ODY3MTQsInVzZXJJZCI6Im1pbmgifQ.rtlgkQhsZMhSUFnxfBk0zSeg0BPHRHHh4SQ54A1GTm8"
            ),
            new UserInfo(
                    "2",
                    "hao",
                    "https://picsum.photos/200/300?random=2",
                    21,
                    false,
                    "hello word",
                    "eyJjdHkiOiJzdHJpbmdlZS1hcGk7dj0xIiwidHlwIjoiSldUIiwiYWxnIjoiSFMyNTYifQ.eyJqdGkiOiJTSy4wLnM1OFRaMnBJbkIwMFdWMlZmTlQ1RXRmU2xLQ2g3cy0xNjgyODk0NzI5IiwiaXNzIjoiU0suMC5zNThUWjJwSW5CMDBXVjJWZk5UNUV0ZlNsS0NoN3MiLCJleHAiOjE2ODU0ODY3MjksInVzZXJJZCI6ImhhbyJ9.CANIGuM0lLjN3n1A0TtNEHLBWQven8VP_i0DUB0t-8k"
            ),
            new UserInfo("3",
                    "anh",
                    "https://picsum.photos/200/300?random=3",
                    23,
                    true,
                    "de thuong",
                    "eyJjdHkiOiJzdHJpbmdlZS1hcGk7dj0xIiwidHlwIjoiSldUIiwiYWxnIjoiSFMyNTYifQ.eyJqdGkiOiJTSy4wLnM1OFRaMnBJbkIwMFdWMlZmTlQ1RXRmU2xLQ2g3cy0xNjgyODk0NzUxIiwiaXNzIjoiU0suMC5zNThUWjJwSW5CMDBXVjJWZk5UNUV0ZlNsS0NoN3MiLCJleHAiOjE2ODU0ODY3NTEsInVzZXJJZCI6ImFuaCJ9.w-uJWfmXAmKn_y9nyTUGYrqwe6GoTyxEqvLsF4qvdGs"
            ),
            new UserInfo(
                    "4",
                    "hien",
                    "https://picsum.photos/200/300?random=4",
                    24,
                    false,
                    "ngu ngok",
                    "eyJjdHkiOiJzdHJpbmdlZS1hcGk7dj0xIiwidHlwIjoiSldUIiwiYWxnIjoiSFMyNTYifQ.eyJqdGkiOiJTSy4wLnM1OFRaMnBJbkIwMFdWMlZmTlQ1RXRmU2xLQ2g3cy0xNjgyODk0NzY3IiwiaXNzIjoiU0suMC5zNThUWjJwSW5CMDBXVjJWZk5UNUV0ZlNsS0NoN3MiLCJleHAiOjE2ODU0ODY3NjcsInVzZXJJZCI6ImhpZW4ifQ.OSSprxugshgZDhSux0vG3XOPa5ptsSP_r1qaf-DvtXk"
            ),
            new UserInfo(
                    "5",
                    "phong",
                    "https://picsum.photos/200/300?random=5",
                    22,
                    true,
                    "trẻ trung, năng động",
                    "eyJjdHkiOiJzdHJpbmdlZS1hcGk7dj0xIiwidHlwIjoiSldUIiwiYWxnIjoiSFMyNTYifQ.eyJqdGkiOiJTSy4wLnM1OFRaMnBJbkIwMFdWMlZmTlQ1RXRmU2xLQ2g3cy0xNjg0NzkwNzEyIiwiaXNzIjoiU0suMC5zNThUWjJwSW5CMDBXVjJWZk5UNUV0ZlNsS0NoN3MiLCJleHAiOjE2ODczODI3MTIsInVzZXJJZCI6InBob25nIn0.4R7ExpITP7RcG0e0HmeJTP94NYkS24dUxpQ27UOu228"
            ),
            new UserInfo(
                    "6",
                    "hung",
                    "https://picsum.photos/200/300?random=6",
                    23,
                    true,
                    "mạnh mẽ, kiên nhẫn",
                    "eyJjdHkiOiJzdHJpbmdlZS1hcGk7dj0xIiwidHlwIjoiSldUIiwiYWxnIjoiSFMyNTYifQ.eyJqdGkiOiJTSy4wLnM1OFRaMnBJbkIwMFdWMlZmTlQ1RXRmU2xLQ2g3cy0xNjg0NzkxMDI5IiwiaXNzIjoiU0suMC5zNThUWjJwSW5CMDBXVjJWZk5UNUV0ZlNsS0NoN3MiLCJleHAiOjE2ODczODMwMjksInVzZXJJZCI6Imh1bmcifQ.WapzB8ukfEzvKLEKPiDpf-FGBrQx3EoFd8W1EUzOK8Q"
            ),
            new UserInfo(
                    "7",
                    "thang",
                    "https://picsum.photos/200/300?random=7",
                    20,
                    true,
                    "hướng ngoại",
                    "eyJjdHkiOiJzdHJpbmdlZS1hcGk7dj0xIiwidHlwIjoiSldUIiwiYWxnIjoiSFMyNTYifQ.eyJqdGkiOiJTSy4wLnM1OFRaMnBJbkIwMFdWMlZmTlQ1RXRmU2xLQ2g3cy0xNjg0NzkxMDUyIiwiaXNzIjoiU0suMC5zNThUWjJwSW5CMDBXVjJWZk5UNUV0ZlNsS0NoN3MiLCJleHAiOjE2ODczODMwNTIsInVzZXJJZCI6InRoYW5nIn0.DY1DYalMhdzikAdPcRfkWgYFCCnB2k2aRf3UNYyArOM"
            ),
            new UserInfo(
                    "8",
                    "quan",
                    "https://picsum.photos/200/300?random=8",
                    19,
                    false,
                    "nội tâm",
                    "eyJjdHkiOiJzdHJpbmdlZS1hcGk7dj0xIiwidHlwIjoiSldUIiwiYWxnIjoiSFMyNTYifQ.eyJqdGkiOiJTSy4wLnM1OFRaMnBJbkIwMFdWMlZmTlQ1RXRmU2xLQ2g3cy0xNjg0NzkxMDc4IiwiaXNzIjoiU0suMC5zNThUWjJwSW5CMDBXVjJWZk5UNUV0ZlNsS0NoN3MiLCJleHAiOjE2ODczODMwNzgsInVzZXJJZCI6InF1YW4ifQ.gUaBgtE705q4y-p4vNC2M0T4RItNdzmhZaso9YEqLyA"
            ),
    };
}
