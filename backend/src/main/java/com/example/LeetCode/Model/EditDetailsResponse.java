package com.example.LeetCode.Model;

public class EditDetailsResponse {

    private String name;
    private String selectedLanguage;
    private String year;
    private String username;

    public EditDetailsResponse(String name, String selectedLanguage, String year, String username) {
        this.name = name;
        this.selectedLanguage = selectedLanguage;
        this.year = year;
        this.username = username;
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
}
