package com.bunny.ai.asyncai.models;

public class ReadWriteUserDetails {

    public String fullName, profileImage, dateJoined, userId, email;

    public ReadWriteUserDetails(){};

    public ReadWriteUserDetails(String textFullName, String textProfileImage, String textDateJoined, String textUserId, String textEmail) {
        this.fullName = textFullName;
        this.profileImage = textProfileImage;
        this.dateJoined = textDateJoined;
        this.userId = textUserId;
        this.email = textEmail;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getDateJoined() {
        return dateJoined;
    }

    public void setDateJoined(String dateJoined) {
        this.dateJoined = dateJoined;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
