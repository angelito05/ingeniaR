package com.example.ingenia.api;
import java.util.List;

import com.example.ingenia.Model.RegisterRequest;
import com.example.ingenia.Model.LoginRequest;
import com.example.ingenia.Model.User;
import com.example.ingenia.Model.Cliente;
import com.example.ingenia.Model.ClienteRequest;
import com.example.ingenia.Model.SolicitudCreditoRequest;
import com.example.ingenia.Model.*;
import com.example.ingenia.Model.UsuarioActualizarDTO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface UsuarioService {
    @POST("api/usuario/login")
    Call<User> login(@Body LoginRequest request);
    @GET("api/usuario/{id}")
    Call<User> ObtenerUsuario(@Path("id") int id);
    @PUT("api/usuario/{id}")
    Call<User> actualizarUsuario(@Path("id") int id, @Body UsuarioActualizarDTO datosActualizados);


    @POST("api/usuario/register")
    Call<User> register(@Body RegisterRequest request);

    @POST("api/cliente")
    Call<Cliente> crearCliente(@Body ClienteRequest request);

    @POST("api/solicitud/crear")
    Call<SolicitudCredito> crearSolicitudCredito(@Body SolicitudCreditoRequest solicitud);

    @GET("api/solicitud/usuario/{idUsuario}")
    Call<List<SolicitudCredito>> obtenerSolicitudesPorUsuario(@Path("idUsuario") int idUsuario);

    @GET("api/solicitud/todas")
    Call<List<SolicitudCredito>> obtenerTodasSolicitudes();



}
