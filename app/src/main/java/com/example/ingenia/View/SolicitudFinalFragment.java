package com.example.ingenia.View;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import android.app.DatePickerDialog;
import java.util.Calendar;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.ingenia.Model.SolicitudCreditoRequest;
import com.example.ingenia.Model.SolicitudCredito;
import com.example.ingenia.R;
import com.example.ingenia.api.ApiConfig;
import com.example.ingenia.api.UsuarioService;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.*;
import retrofit2.converter.gson.GsonConverterFactory;

public class SolicitudFinalFragment extends Fragment {

    private int idCliente;
    private int idUsuario;

    private EditText inputMonto, inputPlazo, inputMotivo;
    private EditText inputTasa, inputFechaInicio, inputFechaFin, inputObservaciones, inputPago;

    private Button btnEnviar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            idCliente = getArguments().getInt("id_cliente", -1);
        }

        SharedPreferences prefs = requireActivity().getSharedPreferences("CrediGoPrefs", getContext().MODE_PRIVATE);
        idUsuario = prefs.getInt("id_usuario", -1);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_solicitud_final, container, false);

        inputMonto = view.findViewById(R.id.input_monto);
        inputPlazo = view.findViewById(R.id.input_plazo);
        inputMotivo = view.findViewById(R.id.input_motivo);
        inputTasa = view.findViewById(R.id.input_tasa);
        inputFechaInicio = view.findViewById(R.id.input_fecha_inicio);
        inputFechaFin = view.findViewById(R.id.input_fecha_fin);
        inputObservaciones = view.findViewById(R.id.input_observaciones);
        btnEnviar = view.findViewById(R.id.btn_enviar_solicitud);
        inputPago = view.findViewById(R.id.input_pago);

        // Evento para mostrar calendario al tocar
        inputFechaInicio.setOnClickListener(v -> mostrarDatePicker(inputFechaInicio));
        inputFechaFin.setOnClickListener(v -> mostrarDatePicker(inputFechaFin));
        btnEnviar.setOnClickListener(v -> enviarSolicitudCredito());

        return view;
    }
    private void mostrarDatePicker(EditText editText) {
        final Calendar c = Calendar.getInstance();
        int anio = c.get(Calendar.YEAR);
        int mes = c.get(Calendar.MONTH);
        int dia = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                (view, year, month, dayOfMonth) -> {
                    // Formatear la fecha en formato YYYY-MM-DD
                    String mesStr = (month + 1) < 10 ? "0" + (month + 1) : String.valueOf(month + 1);
                    String diaStr = dayOfMonth < 10 ? "0" + dayOfMonth : String.valueOf(dayOfMonth);
                    String fechaFormateada = year + "-" + mesStr + "-" + diaStr;
                    editText.setText(fechaFormateada);
                }, anio, mes, dia);
        datePickerDialog.show();
    }


    private void enviarSolicitudCredito() {
        // Obtener valores del formulario
        String montoStr = inputMonto.getText().toString().trim();
        String plazoStr = inputPlazo.getText().toString().trim();
        String motivo = inputMotivo.getText().toString().trim();
        String tasaStr = inputTasa.getText().toString().trim();
        String fechaInicio = inputFechaInicio.getText().toString().trim();
        String fechaFin = inputFechaFin.getText().toString().trim();
        String observaciones = inputObservaciones.getText().toString().trim();

        // Validaciones básicas
        if (tasaStr.isEmpty() || fechaInicio.isEmpty() || fechaFin.isEmpty()) {
            Toast.makeText(getContext(), "Tasa y fechas son obligatorias", Toast.LENGTH_SHORT).show();
            return;
        }

        double tasa;
        try {
            tasa = Double.parseDouble(tasaStr);
            if (tasa <= 0) {
                Toast.makeText(getContext(), "La tasa debe ser mayor que cero", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Tasa de interés inválida", Toast.LENGTH_SHORT).show();
            return;
        }

        if (montoStr.isEmpty() || plazoStr.isEmpty()) {
            Toast.makeText(getContext(), "Monto y plazo son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        if (idCliente == -1 || idUsuario == -1) {
            Toast.makeText(getContext(), "Error: datos de cliente o usuario no disponibles", Toast.LENGTH_LONG).show();
            return;
        }

        double monto;
        int plazo;
        try {
            monto = Double.parseDouble(montoStr);
            plazo = Integer.parseInt(plazoStr);
            if (monto <= 0 || plazo <= 0) {
                Toast.makeText(getContext(), "Monto y plazo deben ser mayores que cero", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Monto o plazo inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validar formato y lógica de fechas
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            java.util.Date fInicio = sdf.parse(fechaInicio);
            java.util.Date fFin = sdf.parse(fechaFin);
            if (fFin.before(fInicio)) {
                Toast.makeText(getContext(), "Fecha fin no puede ser anterior a fecha inicio", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (java.text.ParseException e) {
            Toast.makeText(getContext(), "Formato de fecha inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        // Calcular pago mensual estimado (puedes personalizar esta fórmula)
        double pago_mensual_estimado = (monto * (1 + (tasa / 100))) / plazo;

        // Crear objeto solicitud
        SolicitudCreditoRequest solicitud = new SolicitudCreditoRequest(
                idUsuario,
                idCliente,
                monto,
                plazo,
                motivo,
                tasa,
                fechaInicio + "T00:00:00",
                fechaFin + "T00:00:00",
                observaciones,
                pago_mensual_estimado
        );

        // Enviar solicitud a la API
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(logging).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        UsuarioService service = retrofit.create(UsuarioService.class);

        service.crearSolicitudCredito(solicitud).enqueue(new Callback<SolicitudCredito>() {
            @Override
            public void onResponse(Call<SolicitudCredito> call, Response<SolicitudCredito> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(getContext(), "Solicitud creada con ID: " + response.body().id_solicitud, Toast.LENGTH_LONG).show();

                    // Redirigir a RendimientoFragment
                    RendimientoFragment rendimientoFragment = new RendimientoFragment();
                    Bundle args = new Bundle();
                    args.putInt("id_usuario", idUsuario);
                    rendimientoFragment.setArguments(args);

                    requireActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.container_fragment, rendimientoFragment)
                            .addToBackStack(null)
                            .commit();
                } else {
                    Toast.makeText(getContext(), "Error al enviar solicitud: " + response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<SolicitudCredito> call, Throwable t) {
                Toast.makeText(getContext(), "Error en la conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


}
