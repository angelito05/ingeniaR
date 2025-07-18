package com.example.ingenia.Model;

public class ValidacionClienteResponse {
    public int id_validacion;
    public boolean curp_verificada;
    public String fecha_verificacion;
    public String fecha_expiracion;
    public String ocR_texto_plano;
    public String ocR_datos;
    public String respuesta_verificamex;
    public boolean tieneArchivoINE;
    public boolean tienePdf;

    public Cliente cliente;

    public static class Cliente {
        public int id_cliente;
        public String nombre;
        public String apellido_paterno;
        public String apellido_materno;
        public String curp;
        public boolean cliente_verificado;
    }
}