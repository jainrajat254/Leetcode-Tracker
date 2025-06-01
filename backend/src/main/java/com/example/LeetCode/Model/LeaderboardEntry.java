package com.example.LeetCode.Model;

public class LeaderboardEntry {
    private String name;
    private String username;
    private int totalSolved;
    private String userAvatar;
    private String year;

    public LeaderboardEntry(String name, String username, int totalSolved, String userAvatar, String year) {
        this.name = name;
        this.username = username;
        this.totalSolved = totalSolved;
        this.userAvatar = userAvatar;
        this.year = year;
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

    public int getTotalSolved() {
        return totalSolved;
    }

    public void setTotalSolved(int totalSolved) {
        this.totalSolved = totalSolved;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }
}

