package com.example.LeetCode.Model;

import java.util.List;

public class StatsEntry {
    private String name;
    private String username;
    private int totalSolved;
    private List<Boolean> submissionCalendar;
    private String year;

    public StatsEntry(String name, String username, int totalSolved, List<Boolean> submissionCalendar, String year) {
        this.name = name;
        this.username = username;
        this.totalSolved = totalSolved;
        this.submissionCalendar = submissionCalendar;
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

    public List<Boolean> getSubmissionCalendar() {
        return submissionCalendar;
    }

    public void setSubmissionCalendar(List<Boolean> submissionCalendar) {
        this.submissionCalendar = submissionCalendar;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }
}
