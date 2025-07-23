package com.example.ingenia.Model;

public class SolicitudCreditoRequest {
    private int id_usuario;
    private int id_cliente;
    private double monto_solicitado;
    private int plazo_meses;
    private String motivo;

    private double tasa_interes;
    private String fecha_inicio;
    private String fecha_fin;
    private String observaciones;
    private double pago_mensual_estimado;

    // Constructor completo
    public SolicitudCreditoRequest(int id_usuario, int id_cliente, double monto_solicitado,
                                   int plazo_meses, String motivo, double tasa_interes,
                                   String fecha_inicio, String fecha_fin, String observaciones,
                                   double pago_mensual_estimado) {
        this.id_usuario = id_usuario;
        this.id_cliente = id_cliente;
        this.monto_solicitado = monto_solicitado;
        this.plazo_meses = plazo_meses;
        this.motivo = motivo;
        this.tasa_interes = tasa_interes;
        this.fecha_inicio = fecha_inicio;
        this.fecha_fin = fecha_fin;
        this.observaciones = observaciones;
        this.pago_mensual_estimado = pago_mensual_estimado;
    }

    // Getters y setters
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

    public double getTasa_interes() {
        return tasa_interes;
    }

    public void setTasa_interes(double tasa_interes) {
        this.tasa_interes = tasa_interes;
    }

    public String getFecha_inicio() {
        return fecha_inicio;
    }

    public void setFecha_inicio(String fecha_inicio) {
        this.fecha_inicio = fecha_inicio;
    }

    public String getFecha_fin() {
        return fecha_fin;
    }

    public void setFecha_fin(String fecha_fin) {
        this.fecha_fin = fecha_fin;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public double getPago_mensual_estimado() {
        return pago_mensual_estimado;
    }

    public void setPago_mensual_estimado(double pago_mensual_estimado) {
        this.pago_mensual_estimado = pago_mensual_estimado;
    }
}
