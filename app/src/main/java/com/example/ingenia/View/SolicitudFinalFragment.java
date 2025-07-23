package com.example.ingenia.View;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

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
    private Spinner inputPlazo;
    private EditText inputMonto, inputMotivo;
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
        btnEnviar = view.findViewById(R.id.btn_enviar_solicitud);

        btnEnviar.setOnClickListener(v -> enviarSolicitudCredito());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                R.layout.spinner_item,
                new String[]{" 6 meses", "12 meses", "18 meses", "24 meses", "48 meses"}
        );
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        inputPlazo.setAdapter(adapter);

        return view;
    }

    private void enviarSolicitudCredito() {
        String montoStr = inputMonto.getText().toString().trim();
        String plazoStr = inputPlazo.getSelectedItem().toString().replace(" meses", "").trim();
        String motivo = inputMotivo.getText().toString().trim();

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
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Monto o plazo inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        SolicitudCreditoRequest solicitud = new SolicitudCreditoRequest(
                idUsuario, idCliente, monto, plazo, motivo
        );

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

                    // Ir al RendimientoFragment
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
