package com.example.LeetCode.Model;

import java.util.UUID;

public class LoginResponse {
    private UUID id;
    private String name;
    private String selectedLanguage;
    private String year;
    private String username;
    private String token;

    public LoginResponse(UUID id, String name, String selectedLanguage, String year, String username, String token) {
        this.id = id;
        this.name = name;
        this.selectedLanguage = selectedLanguage;
        this.year = year;
        this.username = username;
        this.token = token;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSelectedLanguage() {
        return selectedLanguage;
    }

    public void setSelectedLanguage(String selectedLanguage) {
        this.selectedLanguage = selectedLanguage;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}

