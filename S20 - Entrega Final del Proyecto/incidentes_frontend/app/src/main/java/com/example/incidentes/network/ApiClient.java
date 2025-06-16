package com.example.incidentes.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static Retrofit retrofit;

    // Método para obtener una instancia de Retrofit (con el nombre getClient)
    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl("http://192.168.1.14/incidentes_backend/") // La URL base de tu servidor
                    .addConverterFactory(GsonConverterFactory.create())  // Convertir las respuestas JSON a objetos Java
                    .build();
        }
        return retrofit;
    }

    // Método para obtener una instancia de Retrofit (con el nombre getRetrofitInstance)
    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl("http://192.168.1.14/incidentes_backend/") // La URL base de tu servidor
                    .addConverterFactory(GsonConverterFactory.create())  // Convertir las respuestas JSON a objetos Java
                    .build();
        }
        return retrofit;
    }
}
