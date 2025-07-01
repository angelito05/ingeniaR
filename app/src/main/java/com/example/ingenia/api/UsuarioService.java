package com.example.ingenia.api;

import com.example.ingenia.Model.RegisterRequest;
import com.example.ingenia.Model.LoginRequest;
import com.example.ingenia.Model.User;
import com.example.ingenia.Model.Cliente;
import com.example.ingenia.Model.ClienteRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UsuarioService {
    @POST("api/usuario/login")
    Call<User> login(@Body LoginRequest request);

    @POST("api/usuario/register")
    Call<User> register(@Body RegisterRequest request);

    @POST("api/cliente")
    Call<Cliente> crearCliente(@Body ClienteRequest request);
}
