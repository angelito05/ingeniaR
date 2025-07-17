package com.example.ingenia.api;

import com.example.ingenia.Model.RenapoRequest;
import com.example.ingenia.Model.VerificamexResponse;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface VerificamexService {
    @POST("api/Verificamex/verificar-curp")  // Cambiar por tu endpoint específico
    Call<Void> enviarCURP(@Body JsonObject curp);
}