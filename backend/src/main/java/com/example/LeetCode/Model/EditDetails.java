package com.example.LeetCode.Model;

public class EditDetails {
    private String name;
    private String username;
    private String year;
    private String selectedLanguage;

    public EditDetails(String name, String username, String year, String selectedLanguage) {
        this.name = name;
        this.username = username;
        this.year = year;
        this.selectedLanguage = selectedLanguage;
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

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getSelectedLanguage() {
        return selectedLanguage;
    }

    public void setSelectedLanguage(String selectedLanguage) {
        this.selectedLanguage = selectedLanguage;
    }
}


