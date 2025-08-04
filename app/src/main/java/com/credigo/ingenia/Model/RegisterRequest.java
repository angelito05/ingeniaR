package com.credigo.ingenia.Model;

public class RegisterRequest {

    public String username;
    public String correo;
    public String contraseña;
    public int id_rol;

    public RegisterRequest(String username, String correo, String contraseña, int id_rol) {
        this.username = username;
        this.correo = correo;
        this.contraseña = contraseña;
        this.id_rol = id_rol;
    }
}
