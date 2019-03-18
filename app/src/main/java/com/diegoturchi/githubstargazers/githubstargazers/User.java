package com.diegoturchi.githubstargazers.githubstargazers;

public class User {

    private String name;
    private String avatarUrl;

    public User(String name, String avatarUrl) {
        this.name = name;
        this.avatarUrl = avatarUrl;
    }

    public String getName() {
        return name;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }
}
