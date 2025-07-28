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
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Streaming;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Multipart;
import retrofit2.http.Part;
import okhttp3.MultipartBody;
import retrofit2.http.Query;

public interface UsuarioService {
    @POST("api/usuario/login")
    Call<User> login(@Body LoginRequest request);
    @GET("api/usuario/{id}")
    Call<User> ObtenerUsuario(@Path("id") int id);
    @PUT("api/usuario/{id}")
    Call<User> actualizarUsuario(@Path("id") int id, @Body UsuarioActualizarDTO datosActualizados);
    @GET("api/usuario/todos")
    Call<List<User>> obtenerTodosLosUsuarios();
    @POST("api/usuario/register")
    Call<User> register(@Body RegisterRequest request);
    @DELETE("api/usuario/{id}")
    Call<Void> eliminarUsuario(@Path("id") int id);
    @POST("api/cliente")
    Call<Cliente> crearCliente(@Body ClienteRequest request);
    @POST("api/solicitud/crear")
    Call<SolicitudCredito> crearSolicitudCredito(@Body SolicitudCreditoRequest solicitud);
    @GET("api/solicitud/usuario/{idUsuario}")
    Call<List<SolicitudCredito>> obtenerSolicitudesPorUsuario(@Path("idUsuario") int idUsuario);
    @GET("api/solicitud/todas")
    Call<List<SolicitudCredito>> obtenerTodasSolicitudes();
    @PUT("api/solicitud/cambiar-estatus/{id}")
    Call<ResponseBody> cambiarEstatusSolicitud(@Path("id") int idSolicitud, @Body CambiarEstatusRequest request);
    @GET("api/Cliente/usuario/{idUsuario}")
    Call<List<Cliente>> getClientesPorUsuario(@Path("idUsuario") int idUsuario);
    @GET("api/Cliente/{id}")
    Call<Cliente> getClientePorId(@Path("id") int idCliente);
    @PUT("api/Cliente/{id}")
    Call<Void> actualizarCliente(@Path("id") int idCliente, @Body Cliente cliente);

    @DELETE("api/Cliente/{id}")
    Call<Void> eliminarCliente(@Path("id") int idCliente);

    @DELETE("api/Solicitud/{id}")
    Call<ResponseBody> eliminarSolicitud(@Path("id") int idSolicitud);

    @PUT("api/Solicitud/editar/{id}")
    Call<Object> editarSolicitud(@Path("id") int id, @Body SolicitudCredito solicitud);

    @Multipart
    @POST("api/OCR/ine")
    Call<OcrResponse> enviarIne(@Part MultipartBody.Part archivoINE);

    @Multipart
    @POST("api/Verificamex/validar-y-guardar")
    Call<Cliente> validarYGuardarCliente(
            @Part("Nombre") RequestBody nombre,
            @Part("Apellido_paterno") RequestBody apellidoPaterno,
            @Part("Apellido_materno") RequestBody apellidoMaterno,
            @Part("Curp") RequestBody curp,
            @Part("Clave_elector") RequestBody claveElector,
            @Part("Fecha_nacimiento") RequestBody fechaNacimiento,
            @Part("Genero") RequestBody genero,
            @Part("Domicilio") RequestBody domicilio,
            @Part("Ciudad") RequestBody ciudad,
            @Part("Estado") RequestBody estado,
            @Part("Codigo_postal") RequestBody codigoPostal,
            @Part("Id_usuario") RequestBody idUsuario,
            @Part MultipartBody.Part ArchivoINE
    );

    @GET("api/ValidacionCliente/{id}")
    Call<ValidacionClienteResponse> getValidacionCliente(@Path("id") int id);

    @GET("api/ValidacionCliente/{id}/descargar-ine")
    @Streaming
    Call<ResponseBody> descargarINE(@Path("id") int id);

    @GET("api/ValidacionCliente/{id}/descargar-pdf")
    @Streaming
    Call<ResponseBody> descargarPDF(@Path("id") int id);

    @GET("api/Bitacora/ultimos-con-detalles")
    Call<List<BitacoraDTO>> getUltimosConDetalles(@Query("cantidad") int cantidad);



}
