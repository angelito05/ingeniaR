package com.example.ingenia.View;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.*;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.*;

import com.example.ingenia.Adapter.SolicitudAdapter;
import com.example.ingenia.Model.SolicitudCredito;
import com.example.ingenia.R;
import com.example.ingenia.api.ApiConfig;
import com.example.ingenia.api.UsuarioService;

import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.*;
import retrofit2.converter.gson.GsonConverterFactory;

public class SolicitudesFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView total, aprobadas, rechazadas;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_solicitudes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerSolicitudes);
        total = view.findViewById(R.id.contadorTotal);
        aprobadas = view.findViewById(R.id.contadorAprobadas);
        rechazadas = view.findViewById(R.id.contadorRechazadas);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        cargarSolicitudes();
    }

    private void cargarSolicitudes() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(logging).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        UsuarioService service = retrofit.create(UsuarioService.class);

        service.obtenerTodasSolicitudes().enqueue(new Callback<List<SolicitudCredito>>() {
            @Override
            public void onResponse(Call<List<SolicitudCredito>> call, Response<List<SolicitudCredito>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<SolicitudCredito> solicitudes = response.body();

                    // Convertir estados para contadores
                    int aprobadasCount = 0, rechazadasCount = 0;
                    for (SolicitudCredito s : solicitudes) {
                        if (s.id_estatus == 2) aprobadasCount++;
                        else if (s.id_estatus == 3) rechazadasCount++;
                    }

                    total.setText("Total de solicitudes: " + solicitudes.size());
                    aprobadas.setText("Aprobadas: " + aprobadasCount);
                    rechazadas.setText("Rechazadas: " + rechazadasCount);

                    recyclerView.setAdapter(new SolicitudAdapter(solicitudes, true)); // true: es admin
                } else {
                    Toast.makeText(getContext(), "Error al obtener solicitudes", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<SolicitudCredito>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
