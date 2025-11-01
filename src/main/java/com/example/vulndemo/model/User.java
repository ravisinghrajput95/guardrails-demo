package com.example.vulndemo.model;

public class User {
    private int id;
    private String name;
    private String bio;

    public User() {}

    public User(int id, String name, String bio) {
        this.id = id; this.name = name; this.bio = bio;
    }

    // getters & setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
}
