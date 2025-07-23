package com.example.ingenia.Model;

import java.io.Serializable;

public class SolicitudCredito implements Serializable {
    public int id_solicitud;
    public int id_usuario;
    public String nombreUsuario;  // Nombre del trabajador

    public int id_cliente;
    public String nombreCliente;  // Nombre del cliente

    public double monto_solicitado;
    public int plazo_meses;
    public String motivo;
    public String fecha_solicitud;
    public int id_estatus;

    public double tasa_interes;  // NUEVO
    public double pago_mensual_estimado;  // NUEVO

    // Puedes agregar getters/setters si usas encapsulación o necesitas serialización personalizada
}
