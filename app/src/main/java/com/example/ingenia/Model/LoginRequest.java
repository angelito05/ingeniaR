package com.example.ingenia.Model;

public class LoginRequest {
    public String username;
    public String contraseña;

    public LoginRequest(String username, String contraseña) {
        this.username = username;
        this.contraseña = contraseña;
    }
}