package com.minari.model;

import java.util.Date;

public abstract class User {
    protected int userId;
    protected String username;
    protected String email;
    protected String password;
    protected Date createdAt;

    public User(int userId, String username, String email, String password) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.password = password;
        this.createdAt = new Date();
    }

    public abstract boolean login(String email, String password);
    public void logout() {
        System.out.println(username + " logged out successfully.");
    }
    public void viewProfile() {
        System.out.println("=== User Profile ===");
        System.out.println("ID: " + userId);
        System.out.println("Username: " + username);
        System.out.println("Email: " + email);
        System.out.println("Created at: " + createdAt);
    }
    public String getEmail(){
        return this.email;
    }
    public void setPassword(String password){
        this.password = password;
    }
}

