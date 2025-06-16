package com.example.incidentes.network;

import com.example.incidentes.RespuestaPermiso;
import com.example.incidentes.RespuestaUsuario;
import com.example.incidentes.Usuario;
import com.example.incidentes.models.Incidente;  // Importa la clase de Incidente
import com.example.incidentes.models.RespuestaDashboard;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;
import retrofit2.http.Streaming;

public interface ApiService {

    // Ya existentes
    @FormUrlEncoded
    @POST("auth/login.php")
    Call<Usuario> login(
            @Field("email") String email,
            @Field("password") String password
    );

    @POST("usuarios/create.php")
    Call<RespuestaUsuario> createUser(@Body Usuario usuario);

    @GET("usuarios/get_all.php")
    Call<List<Usuario>> getAllUsuarios();

    @GET("usuarios/get_roles.php")
    Call<List<String>> getRoles();

    @POST("usuarios/get_user_by_name.php")
    Call<Usuario> getUsuarioByNombre(@Body String nombreUsuario);

    @FormUrlEncoded
    @POST("permissions.php")
    Call<RespuestaPermiso> verificarPermiso(
            @Field("modulo") String modulo,
            @Field("accion") String accion
    );

    @PUT("usuarios/update.php")
    Call<RespuestaUsuario> updateUsuario(@Body Usuario usuario);

    @FormUrlEncoded
    @PUT("usuarios/update.php")
    Call<RespuestaUsuario> updateUsuarioForm(
            @Field("id") int id,
            @Field("nombre") String nombre,
            @Field("email") String email,
            @Field("password") String password,
            @Field("rol") String rol
    );

    @HTTP(method = "DELETE", path = "usuarios/delete.php", hasBody = true)
    Call<RespuestaUsuario> deleteUsuario(@Body Usuario usuario);

    // Nuevo endpoint para obtener todos los incidentes
    @GET("incidentes/get_all.php")
    Call<List<Incidente>> getAllIncidentes();

    // Endpoint para obtener incidentes por usuario
    @GET("incidentes/get_by_user.php")
    Call<List<Incidente>> getIncidentesByUser(
            @Query("usuario_id") int usuarioId
    );

    // Endpoint para crear un nuevo incidente
    @POST("incidentes/create.php")
    Call<RespuestaUsuario> createIncidente(@Body Incidente incidente);

    @PUT("incidentes/update_status.php")
    Call<RespuestaUsuario> updateIncidenteStatus(@Body Incidente incidente);


    // Descargar reporte de incidentes activos
    @Streaming
    @GET("reportes/reporte_pdf.php")
    Call<ResponseBody> descargarReporteActivos();

    // Descargar reporte de incidentes resueltos
    @Streaming
    @GET("reportes/reportes_resueltos.php")
    Call<ResponseBody> descargarReporteResueltos();

    // Endpoint para obtener datos combinados del dashboard
    @GET("dashboard.php")
    Call<RespuestaDashboard> getDashboardData();

}



