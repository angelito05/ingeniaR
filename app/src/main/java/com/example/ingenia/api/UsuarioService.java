package com.example.ingenia.api;

import com.example.ingenia.Model.LoginRequest;
import com.example.ingenia.Model.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UsuarioService {
    @POST("api/usuario/login")
    Call<User> login(@Body LoginRequest request);
}
