package com.example.ingenia.Model;

public class UsuarioActualizarDTO {
    private String username;
    private String contraseña;

    public UsuarioActualizarDTO(String username, String contraseña) {
        this.username = username;
        this.contraseña = contraseña;
    }

    // Getters y setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getContraseña() { return contraseña; }
    public void setContraseña(String contraseña) { this.contraseña = contraseña; }
}
