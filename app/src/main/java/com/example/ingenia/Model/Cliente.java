package com.example.ingenia.Model;

import com.google.gson.annotations.SerializedName;

public class Cliente {
    @SerializedName("id_cliente")
    public int idCliente;

    public String nombre;
    public String apellido_paterno;
    public String apellido_materno;
    public String curp;
    public String clave_elector;
    public String fecha_nacimiento;
    public String genero;
    public String calle;
    public String colonia;
    public String ciudad;
    public String estado;
    public String codigo_postal;
    public boolean cliente_verificado;
    public int id_usuario;

    public Cliente() {}
}
