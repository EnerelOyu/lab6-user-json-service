package com.example.demo.model;

public class User {

    private int id;
    private String name;
    private String email;
    private String bio;
    private String phone;
    
    //constructers
    public User() {
    }

    public User(int id, String name, String email, String bio, String phone) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.bio = bio;
        this.phone = phone;
    }
    
    //getter
    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getEmail() {
        return email;
    }
    public String getBio() {
        return bio;
    }

    public String getPhone() {
        return phone;
    }
    
    //setter
    public void setId(int id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setBio(String bio) {
        this.bio = bio;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
}