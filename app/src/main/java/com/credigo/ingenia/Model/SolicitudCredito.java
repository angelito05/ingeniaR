package com.credigo.ingenia.Model;
import java.io.Serializable;

public class SolicitudCredito implements Serializable {
    public int id_solicitud;
    public int id_usuario;
    public String nombreUsuario;  // <-- nuevo campo para nombre del trabajador
    public String nombreCliente;  // <-- nuevo campo para nombre del cliente
    public double monto_solicitado;
    public int plazo_meses;
    public String motivo;
    public String fecha_solicitud;
    public String observaciones;
    public int id_estatus;
    public String ciudadCliente;
    public String estadoCliente;



    public double tasa_interes;  // NUEVO
    public double pago_mensual_estimado;  // NUEVO

    // Puedes agregar getters/setters si usas encapsulación o necesitas serialización personalizada
}
