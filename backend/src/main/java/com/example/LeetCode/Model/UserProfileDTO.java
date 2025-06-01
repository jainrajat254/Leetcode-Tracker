package com.example.LeetCode.Model;

public class UserProfileDTO {
    private String githubUrl;
    private String twitterUrl;
    private String linkedinUrl;
    private String userAvatar;
    private String school;

    public UserProfileDTO() {

    }

    public UserProfileDTO(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public UserProfileDTO(String githubUrl, String twitterUrl, String linkedinUrl, String school) {
        this.githubUrl = githubUrl;
        this.twitterUrl = twitterUrl;
        this.linkedinUrl = linkedinUrl;
        this.school = school;
    }

    public String getGithubUrl() {
        return githubUrl;
    }

    public void setGithubUrl(String githubUrl) {
        this.githubUrl = githubUrl;
    }

    public String getTwitterUrl() {
        return twitterUrl;
    }

    public void setTwitterUrl(String twitterUrl) {
        this.twitterUrl = twitterUrl;
    }

    public String getLinkedinUrl() {
        return linkedinUrl;
    }

    public void setLinkedinUrl(String linkedinUrl) {
        this.linkedinUrl = linkedinUrl;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }
}

