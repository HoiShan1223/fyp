package com.example.personalizedinventorycontrolapp.entity;

public class User {

    private int id;
    private String username;
    private String password;
    private String email;
    private String token;
    private String iconImage;


    public User() {
    }

    public User(int id, String username, String password, String email, String token, String iconImage) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.token = token;
        this.iconImage = iconImage;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getIconImage() {
        return iconImage;
    }

    public void setIconImage(String iconImage) {
        this.iconImage = iconImage;
    }
}