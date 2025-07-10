package com.example.ingenia.View;

import android.graphics.Color;
import android.os.Bundle;
import android.view.*;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.*;

import com.example.ingenia.Adapter.SolicitudAdapter;
import com.example.ingenia.Model.CambiarEstatusRequest;
import com.example.ingenia.Model.SolicitudCredito;
import com.example.ingenia.R;
import com.example.ingenia.api.ApiConfig;
import com.example.ingenia.api.UsuarioService;

import java.util.ArrayList;
import java.util.List;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.*;
import retrofit2.converter.gson.GsonConverterFactory;

public class SolicitudesFragment extends Fragment {
    private RecyclerView recyclerView;
    private TextView total, aprobadas, rechazadas;
    private BarChart barChart;
    private UsuarioService service;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_solicitudes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerSolicitudes);
        total = view.findViewById(R.id.contadorTotal);
        aprobadas = view.findViewById(R.id.contadorAprobadas);
        rechazadas = view.findViewById(R.id.contadorRechazadas);
        barChart = view.findViewById(R.id.barChart);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        configurarRetrofit();
        cargarSolicitudes();
    }
    private void configurarRetrofit() {
        if (service != null) return;

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        service = retrofit.create(UsuarioService.class);
    }

    private void cargarSolicitudes() {
        service.obtenerTodasSolicitudes().enqueue(new Callback<List<SolicitudCredito>>() {
            @Override
            public void onResponse(Call<List<SolicitudCredito>> call, Response<List<SolicitudCredito>> response) {
                if (!isAdded()) return;

                if (response.isSuccessful() && response.body() != null) {
                    List<SolicitudCredito> solicitudes = response.body();

                    int aprobadasCount = 0, rechazadasCount = 0;
                    for (SolicitudCredito s : solicitudes) {
                        if (s.id_estatus == 2) aprobadasCount++;
                        else if (s.id_estatus == 3) rechazadasCount++;
                    }

                    int totalCount = solicitudes.size();
                    int pendientesCount = totalCount - aprobadasCount - rechazadasCount;

                    total.setText("Total de solicitudes: " + totalCount);
                    aprobadas.setText("Aprobadas: " + aprobadasCount);
                    rechazadas.setText("Rechazadas: " + rechazadasCount);

                    // Configurar gráfico de barras
                    List<BarEntry> entries = new ArrayList<>();
                    entries.add(new BarEntry(0f, aprobadasCount));
                    entries.add(new BarEntry(1f, rechazadasCount));
                    entries.add(new BarEntry(2f, pendientesCount));

                    BarDataSet dataSet = new BarDataSet(entries, "Solicitudes por estado");
                    dataSet.setColors(
                            Color.parseColor("#4CAF50"), // Verde - Aprobadas
                            Color.parseColor("#F44336"), // Rojo - Rechazadas
                            Color.parseColor("#FFC107")  // Amarillo - Pendientes
                    );
                    dataSet.setValueTextSize(14f);

                    BarData barData = new BarData(dataSet);
                    barChart.setData(barData);
                    barChart.getDescription().setEnabled(false);
                    barChart.getLegend().setEnabled(false);

                    String[] labels = new String[]{"Aprobadas", "Rechazadas", "Pendientes"};
                    XAxis xAxis = barChart.getXAxis();
                    xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
                    xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                    xAxis.setGranularity(1f);
                    xAxis.setLabelCount(labels.length);
                    xAxis.setDrawGridLines(false);

                    barChart.getAxisRight().setEnabled(false); // Ocultar eje derecho
                    barChart.getAxisLeft().setGranularity(1f);
                    barChart.getAxisLeft().setAxisMinimum(0f);

                    barChart.animateY(1000);
                    barChart.invalidate();

                    // Mostrar solicitudes en RecyclerView
                    SolicitudAdapter adapter = new SolicitudAdapter(
                            solicitudes,
                            true,
                            (idSolicitud, nuevoEstatus) -> cambiarEstatus(idSolicitud, nuevoEstatus),
                            idSolicitud -> eliminarSolicitud(idSolicitud),
                            solicitud -> { /* no hace nada al click */ }
                    );

                    recyclerView.setAdapter(adapter);

                } else {
                    Toast.makeText(getContext(), "Error al obtener solicitudes", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<SolicitudCredito>> call, Throwable t) {
                if (!isAdded()) return;
                Toast.makeText(getContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void cambiarEstatus(int idSolicitud, int nuevoEstatus) {
        CambiarEstatusRequest request = new CambiarEstatusRequest(nuevoEstatus);

        service.cambiarEstatusSolicitud(idSolicitud, request).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (!isAdded()) return;

                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Estatus actualizado correctamente", Toast.LENGTH_SHORT).show();
                    cargarSolicitudes();
                } else {
                    Toast.makeText(getContext(), "Error al actualizar estatus", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (!isAdded()) return;
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void eliminarSolicitud(int idSolicitud) {
        service.eliminarSolicitud(idSolicitud).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (!isAdded()) return;

                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Solicitud eliminada correctamente", Toast.LENGTH_SHORT).show();
                    cargarSolicitudes();
                } else {
                    Toast.makeText(getContext(), "Error al eliminar solicitud", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (!isAdded()) return;
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
