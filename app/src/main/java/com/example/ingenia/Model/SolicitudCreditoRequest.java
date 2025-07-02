package com.example.ingenia.Model;

public class SolicitudCreditoRequest {
    private int id_usuario;
    private int id_cliente;
    private double monto_solicitado;
    private int plazo_meses;
    private String motivo;

    public SolicitudCreditoRequest(int id_usuario, int id_cliente, double monto_solicitado, int plazo_meses, String motivo) {
        this.id_usuario = id_usuario;
        this.id_cliente = id_cliente;
        this.monto_solicitado = monto_solicitado;
        this.plazo_meses = plazo_meses;
        this.motivo = motivo;
    }

    // Getters y Setters si los necesitas
    public int getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(int id_usuario) {
        this.id_usuario = id_usuario;
    }

    public int getId_cliente() {
        return id_cliente;
    }

    public void setId_cliente(int id_cliente) {
        this.id_cliente = id_cliente;
    }

    public double getMonto_solicitado() {
        return monto_solicitado;
    }

    public void setMonto_solicitado(double monto_solicitado) {
        this.monto_solicitado = monto_solicitado;
    }

    public int getPlazo_meses() {
        return plazo_meses;
    }

    public void setPlazo_meses(int plazo_meses) {
        this.plazo_meses = plazo_meses;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }
}
