package com.example.ingenia.Model;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

public class UsuarioActualizarDTO {
    @SerializedName("username")
    @Nullable
    private String username;
    @SerializedName("contraseña")
    @Nullable
    private String contraseña;
    @SerializedName("activo")
    private Boolean activo;
    public UsuarioActualizarDTO() {
        // Necesario para serialización
    }
    public UsuarioActualizarDTO(String username, String contraseña, boolean activo) {
        this.username = username;
        this.contraseña = contraseña;
        this.activo = activo;
    }
    public UsuarioActualizarDTO(String username, String contraseña) {
        this.username = username;
        this.contraseña = contraseña;
        this.activo = null; // o simplemente no se setea
    }

    // Getters y setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getContraseña() { return contraseña; }
    public void setContraseña(String contraseña) { this.contraseña = contraseña; }
    public Boolean getActivo(){return activo;}
    public void setActivo(Boolean activo) { this.activo = activo; }
}
