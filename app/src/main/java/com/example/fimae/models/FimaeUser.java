package com.example.fimae.models;

import androidx.annotation.Nullable;

public class FimaeUser {
    private String id;
    private String name;
    private String avatarUrl;
    private int age;
    private boolean isMale;
    private String description;
    @Nullable private String token;

    public FimaeUser(){
    }

    public FimaeUser(String id, String name, String avatarUrl, int age, boolean isMale, String description) {
        this.id = id;
        this.name = name;
        this.avatarUrl = avatarUrl;
        this.age = age;
        this.isMale = isMale;
        this.description = description;
    }

    public FimaeUser(String id, String name, String avatarUrl, int age, boolean isMale, String description, @Nullable String token) {
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

    public static FimaeUser[] dummy = new FimaeUser[]{
            new FimaeUser(
                    "1",
                    "minh",
                    "https://images.unsplash.com/photo-1543610892-0b1f7e6d8ac1?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8MjB8fGF2YXRhcnxlbnwwfHwwfHx8MA%3D%3D&auto=format&fit=crop&w=500&q=60",
                    25,
                    true,
                    "lanh lung ik loi",
                    "eyJjdHkiOiJzdHJpbmdlZS1hcGk7dj0xIiwidHlwIjoiSldUIiwiYWxnIjoiSFMyNTYifQ.eyJqdGkiOiJTSy4wLnM1OFRaMnBJbkIwMFdWMlZmTlQ1RXRmU2xLQ2g3cy0xNjgyODk0NzE0IiwiaXNzIjoiU0suMC5zNThUWjJwSW5CMDBXVjJWZk5UNUV0ZlNsS0NoN3MiLCJleHAiOjE2ODU0ODY3MTQsInVzZXJJZCI6Im1pbmgifQ.rtlgkQhsZMhSUFnxfBk0zSeg0BPHRHHh4SQ54A1GTm8"
            ),
            new FimaeUser(
                    "2",
                    "hao",
                    "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8M3x8YXZhdGFyfGVufDB8fDB8fHww&auto=format&fit=crop&w=500&q=60",
                    21,
                    false,
                    "hello word",
                    "eyJjdHkiOiJzdHJpbmdlZS1hcGk7dj0xIiwidHlwIjoiSldUIiwiYWxnIjoiSFMyNTYifQ.eyJqdGkiOiJTSy4wLnM1OFRaMnBJbkIwMFdWMlZmTlQ1RXRmU2xLQ2g3cy0xNjgyODk0NzI5IiwiaXNzIjoiU0suMC5zNThUWjJwSW5CMDBXVjJWZk5UNUV0ZlNsS0NoN3MiLCJleHAiOjE2ODU0ODY3MjksInVzZXJJZCI6ImhhbyJ9.CANIGuM0lLjN3n1A0TtNEHLBWQven8VP_i0DUB0t-8k"
            ),
            new FimaeUser("3",
                    "anh",
                    "https://images.unsplash.com/photo-1599566150163-29194dcaad36?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8NHx8YXZhdGFyfGVufDB8fDB8fHww&auto=format&fit=crop&w=500&q=60",
                    23,
                    true,
                    "de thuong",
                    "eyJjdHkiOiJzdHJpbmdlZS1hcGk7dj0xIiwidHlwIjoiSldUIiwiYWxnIjoiSFMyNTYifQ.eyJqdGkiOiJTSy4wLnM1OFRaMnBJbkIwMFdWMlZmTlQ1RXRmU2xLQ2g3cy0xNjgyODk0NzUxIiwiaXNzIjoiU0suMC5zNThUWjJwSW5CMDBXVjJWZk5UNUV0ZlNsS0NoN3MiLCJleHAiOjE2ODU0ODY3NTEsInVzZXJJZCI6ImFuaCJ9.w-uJWfmXAmKn_y9nyTUGYrqwe6GoTyxEqvLsF4qvdGs"
            ),
            new FimaeUser(
                    "4",
                    "hien",
                    "https://images.unsplash.com/photo-1527980965255-d3b416303d12?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8NXx8YXZhdGFyfGVufDB8fDB8fHww&auto=format&fit=crop&w=500&q=60",
                    24,
                    false,
                    "ngu ngok",
                    "eyJjdHkiOiJzdHJpbmdlZS1hcGk7dj0xIiwidHlwIjoiSldUIiwiYWxnIjoiSFMyNTYifQ.eyJqdGkiOiJTSy4wLnM1OFRaMnBJbkIwMFdWMlZmTlQ1RXRmU2xLQ2g3cy0xNjgyODk0NzY3IiwiaXNzIjoiU0suMC5zNThUWjJwSW5CMDBXVjJWZk5UNUV0ZlNsS0NoN3MiLCJleHAiOjE2ODU0ODY3NjcsInVzZXJJZCI6ImhpZW4ifQ.OSSprxugshgZDhSux0vG3XOPa5ptsSP_r1qaf-DvtXk"
            ),
            new FimaeUser(
                    "5",
                    "phong",
                    "https://images.unsplash.com/photo-1580489944761-15a19d654956?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Nnx8YXZhdGFyfGVufDB8fDB8fHww&auto=format&fit=crop&w=500&q=60",
                    22,
                    true,
                    "trẻ trung, năng động",
                    "eyJjdHkiOiJzdHJpbmdlZS1hcGk7dj0xIiwidHlwIjoiSldUIiwiYWxnIjoiSFMyNTYifQ.eyJqdGkiOiJTSy4wLnM1OFRaMnBJbkIwMFdWMlZmTlQ1RXRmU2xLQ2g3cy0xNjg0NzkwNzEyIiwiaXNzIjoiU0suMC5zNThUWjJwSW5CMDBXVjJWZk5UNUV0ZlNsS0NoN3MiLCJleHAiOjE2ODczODI3MTIsInVzZXJJZCI6InBob25nIn0.4R7ExpITP7RcG0e0HmeJTP94NYkS24dUxpQ27UOu228"
            ),
            new FimaeUser(
                    "6",
                    "hung",
                    "https://images.unsplash.com/photo-1633332755192-727a05c4013d?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8OHx8YXZhdGFyfGVufDB8fDB8fHww&auto=format&fit=crop&w=500&q=60",
                    23,
                    true,
                    "mạnh mẽ, kiên nhẫn",
                    "eyJjdHkiOiJzdHJpbmdlZS1hcGk7dj0xIiwidHlwIjoiSldUIiwiYWxnIjoiSFMyNTYifQ.eyJqdGkiOiJTSy4wLnM1OFRaMnBJbkIwMFdWMlZmTlQ1RXRmU2xLQ2g3cy0xNjg0NzkxMDI5IiwiaXNzIjoiU0suMC5zNThUWjJwSW5CMDBXVjJWZk5UNUV0ZlNsS0NoN3MiLCJleHAiOjE2ODczODMwMjksInVzZXJJZCI6Imh1bmcifQ.WapzB8ukfEzvKLEKPiDpf-FGBrQx3EoFd8W1EUzOK8Q"
            ),
            new FimaeUser(
                    "7",
                    "thang",
                    "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8MTF8fGF2YXRhcnxlbnwwfHwwfHx8MA%3D%3D&auto=format&fit=crop&w=500&q=60",
                    20,
                    true,
                    "hướng ngoại",
                    "eyJjdHkiOiJzdHJpbmdlZS1hcGk7dj0xIiwidHlwIjoiSldUIiwiYWxnIjoiSFMyNTYifQ.eyJqdGkiOiJTSy4wLnM1OFRaMnBJbkIwMFdWMlZmTlQ1RXRmU2xLQ2g3cy0xNjg0NzkxMDUyIiwiaXNzIjoiU0suMC5zNThUWjJwSW5CMDBXVjJWZk5UNUV0ZlNsS0NoN3MiLCJleHAiOjE2ODczODMwNTIsInVzZXJJZCI6InRoYW5nIn0.DY1DYalMhdzikAdPcRfkWgYFCCnB2k2aRf3UNYyArOM"
            ),
            new FimaeUser(
                    "8",
                    "quan",
                    "https://images.unsplash.com/photo-1628157588553-5eeea00af15c?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8MTd8fGF2YXRhcnxlbnwwfHwwfHx8MA%3D%3D&auto=format&fit=crop&w=500&q=60",
                    19,
                    false,
                    "nội tâm",
                    "eyJjdHkiOiJzdHJpbmdlZS1hcGk7dj0xIiwidHlwIjoiSldUIiwiYWxnIjoiSFMyNTYifQ.eyJqdGkiOiJTSy4wLnM1OFRaMnBJbkIwMFdWMlZmTlQ1RXRmU2xLQ2g3cy0xNjg0NzkxMDc4IiwiaXNzIjoiU0suMC5zNThUWjJwSW5CMDBXVjJWZk5UNUV0ZlNsS0NoN3MiLCJleHAiOjE2ODczODMwNzgsInVzZXJJZCI6InF1YW4ifQ.gUaBgtE705q4y-p4vNC2M0T4RItNdzmhZaso9YEqLyA"
            ),
    };
}
