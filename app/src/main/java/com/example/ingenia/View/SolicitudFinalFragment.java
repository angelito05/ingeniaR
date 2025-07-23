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

import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.*;
import retrofit2.converter.gson.GsonConverterFactory;

public class SolicitudFinalFragment extends Fragment {

    private int idCliente;
    private int idUsuario;
    private Spinner inputPlazo;
    private TextView textPagoMensual;

    private EditText inputMonto, inputMotivo;
    private EditText inputFechaInicio, inputFechaFin, inputObservaciones, inputPago;

    private Button btnEnviar;
    private double pago_mensual_estimado = 0.0;

    private Spinner inputTasa;
    private double tasaSeleccionada = 0.0;


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
        textPagoMensual = view.findViewById(R.id.text_pago_mensual);
        btnEnviar = view.findViewById(R.id.btn_enviar_solicitud);

        inputFechaFin.setFocusable(false);
        inputFechaFin.setClickable(false);

        // Evento para mostrar calendario al tocar
        inputFechaInicio.setOnClickListener(v -> mostrarDatePicker(inputFechaInicio));
        inputFechaFin.setOnClickListener(v -> mostrarDatePicker(inputFechaFin));
        btnEnviar.setOnClickListener(v -> enviarSolicitudCredito());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                R.layout.spinner_item,
                new String[]{" 6 meses", "12 meses", "18 meses", "24 meses", "48 meses"}
        );
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        inputPlazo.setAdapter(adapter);

        inputPlazo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                calcularPagoMensual();
                calcularFechaFin();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Configurar el Spinner de tasa (asegúrate que inputTasa sea el ID del Spinner en XML)
        String[] tasas = {"10.00", "15.00", "20.00", "25.00"};
        ArrayAdapter<String> tasaAdapter = new ArrayAdapter<>(requireContext(),
                R.layout.spinner_item, tasas);
        tasaAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        inputTasa.setAdapter(tasaAdapter);

        inputTasa.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String seleccion = tasas[position].replace("%", "").trim();
                try {
                    tasaSeleccionada = Double.parseDouble(seleccion);
                } catch (NumberFormatException e) {
                    tasaSeleccionada = 0.0;
                }
                calcularPagoMensual();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                tasaSeleccionada = 0.0;
                calcularPagoMensual();
            }
        });

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
                    calcularFechaFin(); // Actualizar fecha final
                }, anio, mes, dia);
        datePickerDialog.show();
    }

    private void calcularFechaFin() {
        String fechaInicioStr = inputFechaInicio.getText().toString();
        String plazoStr = inputPlazo.getSelectedItem().toString().replace(" meses", "").trim();

        if (fechaInicioStr.isEmpty() || plazoStr.isEmpty()) return;

        try {
            int meses = Integer.parseInt(plazoStr);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date fechaInicio = sdf.parse(fechaInicioStr);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(fechaInicio);
            calendar.add(Calendar.MONTH, meses);

            String fechaFinStr = sdf.format(calendar.getTime());
            inputFechaFin.setText(fechaFinStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
    private void calcularPagoMensual() {
        String montoStr = inputMonto.getText().toString().trim();
        String plazoStr = inputPlazo.getSelectedItem().toString().replace(" meses", "").trim();

        if (montoStr.isEmpty() || plazoStr.isEmpty() || tasaSeleccionada == 0.0) {
            textPagoMensual.setText("Pago mensual estimado: $0.00");
            return;
        }

        try {
            double monto = Double.parseDouble(montoStr);
            int plazo = Integer.parseInt(plazoStr);
            double tasaMensual = tasaSeleccionada / 100.0 / 12.0;

            double pago;
            if (tasaMensual > 0) {
                pago = (monto * tasaMensual) / (1 - Math.pow(1 + tasaMensual, -plazo));
            } else {
                pago = monto / plazo;
            }

            pago_mensual_estimado = pago;
            textPagoMensual.setText(String.format(Locale.getDefault(), "Pago mensual estimado: $%.2f", pago));
        } catch (NumberFormatException e) {
            pago_mensual_estimado = 0.0;
            textPagoMensual.setText("Pago mensual estimado: $0.00");
        }
    }


    private void enviarSolicitudCredito() {
        // Obtener valores del formulario
        String montoStr = inputMonto.getText().toString().trim();
        String plazoStr = inputPlazo.getSelectedItem().toString().replace(" meses", "").trim();
        String motivo = inputMotivo.getText().toString().trim();
        String tasaStr = inputTasa.getSelectedItem().toString().trim();
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
                } else try {
                    // Lee el cuerpo de error como JSON
                    String errorBody = response.errorBody().string();
                    JSONObject json = new JSONObject(errorBody);

                    String mensaje = json.optString("mensaje", "Error al enviar solicitud");
                    Toast.makeText(getContext(), mensaje, Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Toast.makeText(getContext(), "Error inesperado: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<SolicitudCredito> call, Throwable t) {
                Toast.makeText(getContext(), "Error en la conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


}
