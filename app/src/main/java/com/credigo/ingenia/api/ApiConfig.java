package com.credigo.ingenia.api;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiConfig {
    public static final String BASE_URL = "https://credigo-fqfbdhbtddgccaae.mexicocentral-01.azurewebsites.net/"; // Cambia según tu red

    private static Retrofit retrofit = null;

    public static Retrofit getRetrofit() {
        if (retrofit == null) {

            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .connectTimeout(60, TimeUnit.SECONDS)   // 60 segundos para conectar
                    .readTimeout(120, TimeUnit.SECONDS)     // 120 segundos para leer respuesta
                    .writeTimeout(60, TimeUnit.SECONDS)     // 60 segundos para enviar datos
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)    // Aquí pasas el cliente con timeout aumentado
                    .build();
        }
        return retrofit;
    }
}
