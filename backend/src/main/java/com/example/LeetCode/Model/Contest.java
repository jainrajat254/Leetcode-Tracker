package com.example.LeetCode.Model;

public class Contest {
    private int attendedContestsCount;
    private double rating;
    private int globalRanking;
    private double topPercentage;

    public Contest(int attendedContestsCount, double rating, int globalRanking, double topPercentage) {
        this.attendedContestsCount = attendedContestsCount;
        this.rating = rating;
        this.globalRanking = globalRanking;
        this.topPercentage = topPercentage;
    }

    public int getAttendedContestsCount() {
        return attendedContestsCount;
    }

    public void setAttendedContestsCount(int attendedContestsCount) {
        this.attendedContestsCount = attendedContestsCount;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getGlobalRanking() {
        return globalRanking;
    }

    public void setGlobalRanking(int globalRanking) {
        this.globalRanking = globalRanking;
    }

    public double getTopPercentage() {
        return topPercentage;
    }

    public void setTopPercentage(double topPercentage) {
        this.topPercentage = topPercentage;
    }
}
