package com.example.ingenia.Model;

public class BitacoraDTO {
    private int id_bitacora;
    private String nombreUsuario;
    private String accion;
    private String descripcion;
    private String entidad_afectada;
    private String nombreClienteAfectado;
    private String fecha;

    // Getters y setters
    public int getId_bitacora() { return id_bitacora; }
    public void setId_bitacora(int id_bitacora) { this.id_bitacora = id_bitacora; }

    public String getNombreUsuario() { return nombreUsuario; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }

    public String getAccion() { return accion; }
    public void setAccion(String accion) { this.accion = accion; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getEntidad_afectada() { return entidad_afectada; }
    public void setEntidad_afectada(String entidad_afectada) { this.entidad_afectada = entidad_afectada; }

    public String getNombreClienteAfectado() { return nombreClienteAfectado; }
    public void setNombreClienteAfectado(String nombreClienteAfectado) { this.nombreClienteAfectado = nombreClienteAfectado; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }
}
