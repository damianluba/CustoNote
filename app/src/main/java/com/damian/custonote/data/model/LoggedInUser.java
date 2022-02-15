package com.damian.custonote.data.model;

import android.net.Uri;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
public class LoggedInUser {

    private String userId;
    private String username;
    private Uri photoUri;

    public LoggedInUser(String userId, String username, Uri photoUri) {
        this.userId = userId;
        this.username = username;
        this.photoUri = photoUri;
    }

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }
}