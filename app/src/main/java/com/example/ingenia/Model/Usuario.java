package com.example.ingenia.Model;

public class Usuario {
    private String nombre;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String username;
    private String correo;
    private String contrasena;

    public Usuario(String nombre, String apellidoPaterno, String apellidoMaterno,
                   String username, String correo, String contrasena) {
        this.nombre = nombre;
        this.apellidoPaterno = apellidoPaterno;
        this.apellidoMaterno = apellidoMaterno;
        this.username = username;
        this.correo = correo;
        this.contrasena = contrasena;
    }

    public String getResumen() {
        return "Nombre: " + nombre +
                "\nApellido Paterno: " + apellidoPaterno +
                "\nApellido Materno: " + apellidoMaterno +
                "\nNombre de Usuario: " + username +
                "\nCorreo: " + correo +
                "\nContraseña: " + contrasena;
    }
}
