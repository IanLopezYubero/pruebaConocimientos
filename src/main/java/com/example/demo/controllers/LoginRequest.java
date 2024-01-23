package com.example.demo.controllers;

public class LoginRequest {
    private String user;
    private String password;

    // Constructores, getters, setters, etc.

    // Ejemplo de constructores y métodos getter y setter
    public LoginRequest() {
    }

    public LoginRequest(String user, String password) {
        this.user = user;
        this.password = password;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
