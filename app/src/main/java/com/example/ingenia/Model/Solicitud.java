package com.example.ingenia.Model;

public class Solicitud {

    public enum Estado {
        APROBADA,
        RECHAZADA,
        PENDIENTE
    }

    public String nombre;
    public String detalles;
    public Estado estado;
    public String trabajador; // nombre del trabajador que la creó


    public Solicitud(String nombre, String detalles, Estado estado, String trabajador) {
        this.nombre = nombre;
        this.detalles = detalles;
        this.estado = estado;
        this.trabajador = trabajador;
    }

}
