package com.lordjoe.resource_guide.model;

public class AppUser {
    private final String username;
    private final String encryptedPassword;

    public AppUser(String username, String encryptedPassword) {
        this.username = username;
        this.encryptedPassword = encryptedPassword;
    }

    public String getUsername() {
        return username;
    }

    public String getEncryptedPassword() {
        return encryptedPassword;
    }
}

