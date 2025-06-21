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

    public Solicitud(String nombre, String detalles, Estado estado) {
        this.nombre = nombre;
        this.detalles = detalles;
        this.estado = estado;
    }
}
