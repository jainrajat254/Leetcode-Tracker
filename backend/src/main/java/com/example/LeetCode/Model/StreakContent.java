package com.example.LeetCode.Model;

public class StreakContent {
    private String name;
    private String username;
    private Boolean submittedToday;
    private String userAvatar;

    public StreakContent() {}

    public StreakContent(String name, String username, Boolean submittedToday, String userAvatar) {
        this.name = name;
        this.username = username;
        this.submittedToday = submittedToday;
        this.userAvatar = userAvatar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Boolean getSubmittedToday() {
        return submittedToday;
    }

    public void setSubmittedToday(Boolean submittedToday) {
        this.submittedToday = submittedToday;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }
}
