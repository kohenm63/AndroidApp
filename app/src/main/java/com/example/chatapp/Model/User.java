package com.example.chatapp.Model;

public class User {
    private String id;
    private String username;
    private String language;
    private String imageURL;

    public User(String id, String username, String language, String imageURL) {
        this.id = id;
        this.username = username;
        this.language = language;
        this.imageURL = imageURL;
    }

    public User() {


    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
}
