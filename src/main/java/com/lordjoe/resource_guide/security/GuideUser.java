package com.lordjoe.resource_guide.security;

public class GuideUser {
    private final String username;
    private final String encryptedPassword;
    private final String role;

    public GuideUser(String username, String encryptedPassword, String role) {
        this.username = username;
        this.encryptedPassword = encryptedPassword;
        this.role = role;
    }

    public String username() {
        return username;
    }

    public String password() {
        return encryptedPassword;
    }

    public String role() {
        return role;
    }

    @Override
    public String toString() {
        return "GuideUser{" +
                "username='" + username + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}
