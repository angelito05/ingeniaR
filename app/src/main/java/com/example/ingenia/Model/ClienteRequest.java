package com.example.ingenia.Model;

public class ClienteRequest {
    public String nombre;
    public String apellido_paterno;
    public String apellido_materno;
    public String curp;
    public String clave_elector;
    public String fecha_nacimiento;  // formato "yyyy-MM-dd"
    public String genero;
    public String domicilio;

    public String ciudad;
    public String estado;
    public String codigo_postal;
    public int id_usuario;  // nuevo campo

    public ClienteRequest() {}

    public ClienteRequest(String nombre, String apellido_paterno, String apellido_materno, String curp,
                          String clave_elector, String fecha_nacimiento, String genero,
                          String domicilio, String ciudad, String estado, String codigo_postal,
                          int id_usuario) {
        this.nombre = nombre;
        this.apellido_paterno = apellido_paterno;
        this.apellido_materno = apellido_materno;
        this.curp = curp;
        this.clave_elector = clave_elector;
        this.fecha_nacimiento = fecha_nacimiento;
        this.genero = genero;
        this.domicilio = domicilio;

        this.ciudad = ciudad;
        this.estado = estado;
        this.codigo_postal = codigo_postal;
        this.id_usuario = id_usuario;
    }
}
