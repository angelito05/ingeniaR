package com.credigo.ingenia.View;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.*;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.credigo.ingenia.Model.Cliente;
import com.credigo.ingenia.Model.ValidacionClienteResponse;
import com.credigo.ingenia.R;
import com.credigo.ingenia.api.ApiConfig;
import com.credigo.ingenia.api.UsuarioService;

import java.io.*;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.app.DatePickerDialog;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.*;
import retrofit2.converter.gson.GsonConverterFactory;

public class DetalleClienteFragment extends Fragment {

    private EditText etNombre, etApellidoPaterno, etApellidoMaterno, etCurp, etClaveElector;
    private EditText etDomicilio, etCiudad, etEstado, etCodigoPostal, etGenero, etFechaNacimiento;
    private Button btnGuardar, btnEliminar, btnValidar;
    private Button btnDescargarINE, btnDescargarPDF;
    private TextView tvCurpVerificada, tvFechaVerificacion, tvFechaExpiracion;
    private ImageView imgINE;

    private int idCliente;
    private UsuarioService service;
    private ValidacionClienteResponse validacionActual;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_detalle_cliente, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Referencias UI
        etNombre = view.findViewById(R.id.etNombre);
        etApellidoPaterno = view.findViewById(R.id.etApellidoPaterno);
        etApellidoMaterno = view.findViewById(R.id.etApellidoMaterno);
        etCurp = view.findViewById(R.id.etCurp);
        etClaveElector = view.findViewById(R.id.etClaveElector);
        etDomicilio = view.findViewById(R.id.etDomicilio);
        etCiudad = view.findViewById(R.id.etMunicipio);
        etEstado = view.findViewById(R.id.etEstado);
        etCodigoPostal = view.findViewById(R.id.etCodigoPostal);
        etGenero = view.findViewById(R.id.etSexo);
        etFechaNacimiento = view.findViewById(R.id.etFechaNacimiento);

        btnGuardar = view.findViewById(R.id.btnGuardar);
        btnEliminar = view.findViewById(R.id.btnEliminar);
        btnValidar = view.findViewById(R.id.btnValidar);
        btnDescargarINE = view.findViewById(R.id.btnDescargarINE);
        btnDescargarPDF = view.findViewById(R.id.btnDescargarPDF);

        tvCurpVerificada = view.findViewById(R.id.tvCurpVerificada);
        tvFechaVerificacion = view.findViewById(R.id.tvFechaVerificacion);
        tvFechaExpiracion = view.findViewById(R.id.tvFechaExpiracion);
        imgINE = view.findViewById(R.id.imgINE);

        // Obtener ID cliente de argumentos
        Bundle args = getArguments();
        if (args == null || !args.containsKey("id_cliente")) {
            Toast.makeText(getContext(), "ID de cliente inválido", Toast.LENGTH_SHORT).show();
            return;
        }
        idCliente = args.getInt("id_cliente");

        // Config Retrofit con logging
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(logging).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        service = retrofit.create(UsuarioService.class);

        cargarDatosCliente();
        cargarValidacionCliente();

        btnGuardar.setOnClickListener(v -> guardarCambios());
        btnEliminar.setOnClickListener(v -> eliminarCliente());
        btnValidar.setOnClickListener(v -> redirigirAValidacion());
        btnDescargarINE.setOnClickListener(v -> descargarArchivoINE());
        btnDescargarPDF.setOnClickListener(v -> descargarArchivoPDF());

        // Mostrar DatePicker al tocar el EditText de fecha de nacimiento
        etFechaNacimiento.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();

            // Intentar cargar fecha actual para mostrar en DatePicker
            String fechaActual = etFechaNacimiento.getText().toString();
            if (!fechaActual.isEmpty()) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date date = sdf.parse(fechaActual);
                    calendar.setTime(date);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), (view1, y, m, d) -> {
                String fechaSeleccionada = String.format("%04d-%02d-%02d", y, m + 1, d);
                etFechaNacimiento.setText(fechaSeleccionada);
            }, year, month, day);

            datePickerDialog.show();
        });
    }


    private void cargarDatosCliente() {
        service.getClientePorId(idCliente).enqueue(new Callback<Cliente>() {
            @Override
            public void onResponse(Call<Cliente> call, Response<Cliente> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Cliente cliente = response.body();
                    etNombre.setText(cliente.nombre);
                    etApellidoPaterno.setText(cliente.apellido_paterno);
                    etApellidoMaterno.setText(cliente.apellido_materno);
                    etCurp.setText(cliente.curp);
                    etClaveElector.setText(cliente.clave_elector);
                    etDomicilio.setText(cliente.domicilio);
                    etCiudad.setText(cliente.ciudad);
                    etEstado.setText(cliente.estado);
                    etFechaNacimiento.setText(cliente.fecha_nacimiento);
                    etCodigoPostal.setText(cliente.codigo_postal);
                    etGenero.setText(cliente.genero);
                } else {
                    Toast.makeText(getContext(), "No se pudo cargar cliente", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Cliente> call, Throwable t) {
                Toast.makeText(getContext(), "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cargarValidacionCliente() {
        service.getValidacionCliente(idCliente).enqueue(new Callback<ValidacionClienteResponse>() {
            @Override
            public void onResponse(Call<ValidacionClienteResponse> call, Response<ValidacionClienteResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    validacionActual = response.body();

                    tvCurpVerificada.setText(validacionActual.curp_verificada ? "CURP Verificada" : "CURP NO Verificada");
                    tvFechaVerificacion.setText("Fecha verificación: " + validacionActual.fecha_verificacion);
                    tvFechaExpiracion.setText("Fecha expiración: " + validacionActual.fecha_expiracion);

                    btnDescargarINE.setEnabled(validacionActual.tieneArchivoINE);
                    btnDescargarPDF.setEnabled(validacionActual.tienePdf);

                    if (validacionActual.tieneArchivoINE) {
                        mostrarImagenINE();  // Aquí mostramos la imagen INE
                    } else {
                        imgINE.setImageResource(android.R.color.darker_gray); // imagen placeholder si no hay archivo
                    }
                } else {
                    Toast.makeText(getContext(), "No hay validación disponible", Toast.LENGTH_SHORT).show();
                    btnDescargarINE.setEnabled(false);
                    btnDescargarPDF.setEnabled(false);
                    imgINE.setImageResource(android.R.color.darker_gray);
                }
            }

            @Override
            public void onFailure(Call<ValidacionClienteResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Error red validación: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void guardarCambios() {
        Cliente actualizado = new Cliente();
        actualizado.idCliente = idCliente;
        actualizado.nombre = etNombre.getText().toString();
        actualizado.apellido_paterno = etApellidoPaterno.getText().toString();
        actualizado.apellido_materno = etApellidoMaterno.getText().toString();
        actualizado.curp = etCurp.getText().toString();
        actualizado.clave_elector = etClaveElector.getText().toString();
        actualizado.domicilio = etDomicilio.getText().toString();
        actualizado.ciudad = etCiudad.getText().toString();
        actualizado.fecha_nacimiento =etFechaNacimiento.getText().toString();
        actualizado.estado = etEstado.getText().toString();
        actualizado.codigo_postal = etCodigoPostal.getText().toString();
        actualizado.genero = etGenero.getText().toString();

        service.actualizarCliente(idCliente, actualizado).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Cliente actualizado correctamente", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Error al actualizar cliente", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getContext(), "Error al actualizar: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void eliminarCliente() {
        new AlertDialog.Builder(getContext())
                .setTitle("Eliminar Cliente")
                .setMessage("¿Estás seguro que deseas eliminar este cliente?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    service.eliminarCliente(idCliente).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(getContext(), "Cliente eliminado", Toast.LENGTH_SHORT).show();
                                requireActivity().getSupportFragmentManager().popBackStack();
                            } else if (response.code() == 401) {
                                Toast.makeText(getContext(), "No tienes permiso para eliminar este cliente", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getContext(), "Error al eliminar cliente", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(getContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void redirigirAValidacion() {
        CrearSolicitudFragment fragment = new CrearSolicitudFragment();

        Bundle args = new Bundle();
        args.putInt("id_cliente", idCliente);
        fragment.setArguments(args);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container_fragment, fragment)
                .addToBackStack(null)
                .commit();
    }


    private void descargarArchivoINE() {
        if (validacionActual == null) {
            Toast.makeText(getContext(), "No hay archivo INE disponible", Toast.LENGTH_SHORT).show();
            return;
        }
        service.descargarINE(idCliente).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    boolean guardado = guardarArchivoEnDisco(response.body(), "INE_Cliente.jpg");
                    if (guardado) {
                        Toast.makeText(getContext(), "Archivo INE descargado", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Error al guardar archivo INE", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Error al descargar archivo INE", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getContext(), "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarImagenINE() {
        service.descargarINE(idCliente).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    InputStream inputStream = response.body().byteStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    imgINE.setImageBitmap(bitmap);
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getContext(), "No se pudo cargar la imagen INE", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getContext(), "Error al descargar imagen INE: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void descargarArchivoPDF() {
        if (validacionActual == null) {
            Toast.makeText(getContext(), "No hay archivo PDF disponible", Toast.LENGTH_SHORT).show();
            return;
        }
        service.descargarPDF(idCliente).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    boolean guardado = guardarArchivoEnDisco(response.body(), "ValidacionCliente.pdf");
                    if (guardado) {
                        Toast.makeText(getContext(), "Archivo PDF descargado", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Error al guardar archivo PDF", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Error al descargar archivo PDF", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getContext(), "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean guardarArchivoEnDisco(ResponseBody body, String nombreArchivo) {
        try {
            File archivo = new File(requireContext().getExternalFilesDir(null), nombreArchivo);

            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] buffer = new byte[4096];
                int read;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(archivo);

                while ((read = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, read);
                }
                outputStream.flush();

                // Abrir automáticamente
                if (nombreArchivo.endsWith(".pdf")) {
                    abrirPdf(archivo);
                } else if (nombreArchivo.endsWith(".jpg") || nombreArchivo.endsWith(".png")) {
                    abrirImagen(archivo);
                }

                return true;

            } finally {
                if (inputStream != null) inputStream.close();
                if (outputStream != null) outputStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void abrirPdf(File file) {
        Uri uri = FileProvider.getUriForFile(requireContext(), requireContext().getPackageName() + ".fileprovider", file);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "application/pdf");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(intent);
    }

    private void abrirImagen(File file) {
        Uri uri = FileProvider.getUriForFile(requireContext(), requireContext().getPackageName() + ".fileprovider", file);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "image/*");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(intent);
    }
}
