package com.example.incidentes.network;

import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.internal.BaseImplementation;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    // La URL base debe ser solo hasta el dominio, no incluir el archivo específico
    private static final String BASE_URL = "http://192.168.1.14/incidentes_backend/";  // URL base de tu API

    private static Retrofit retrofit;

    // Método para obtener la instancia de Retrofit
    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)  // Aquí solo la URL base, sin crear.php ni otros archivos
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    // Método para obtener la instancia del ApiService
    public static ApiService getApiService() {
        return getRetrofitInstance().create(ApiService.class);  // Aquí obtenemos el ApiService
    }

    // Método getInstance: falta implementar según uso concreto
    public static BaseImplementation.ApiMethodImpl<Result, Api.AnyClient> getInstance() {
        throw new UnsupportedOperationException("Implementa getInstance() según tu API");
    }
}

