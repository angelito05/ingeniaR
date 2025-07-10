package com.example.ingenia.Model;
import java.io.Serializable;

public class SolicitudCredito implements Serializable {
    public int id_solicitud;
    public int id_usuario;
    public String nombreUsuario;  // <-- nuevo campo para nombre del trabajador
    public int id_cliente;
    public String nombreCliente;  // <-- nuevo campo para nombre del cliente
    public double monto_solicitado;
    public int plazo_meses;
    public String motivo;
    public String fecha_solicitud;
    public int id_estatus;

    // Constructor, getters y setters pueden ser agregados si usas encapsulación
}
