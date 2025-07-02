package com.example.ingenia.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("id_usuario")
    @Expose
    public int id_usuario;

    @SerializedName("username")
    @Expose
    public String username;

    @SerializedName("correo")
    @Expose
    public String correo;

    @SerializedName("contraseña")
    public String contrasena;

    @SerializedName("id_rol")
    @Expose
    public int id_rol;

    @SerializedName("activo")
    @Expose
    public boolean activo;

    @SerializedName("fecha_creacion")
    public String fecha_creacion;

    // Getters
    public int getId_usuario() { return id_usuario; }
    public String getUsername() { return username; }
    public String getCorreo() { return correo; }
    public String getContraseña() { return contrasena; }
    public int getId_rol() { return id_rol; }
    public boolean isActivo() { return activo; }
    public String getFecha_creacion() { return fecha_creacion; }
}
