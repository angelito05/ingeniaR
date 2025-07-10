package com.example.ingenia.View;

import android.graphics.Color;
import android.os.Bundle;
import android.view.*;
import android.widget.*;

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

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.*;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.*;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.*;
import retrofit2.converter.gson.GsonConverterFactory;

public class SolicitudesFragment extends Fragment {
    private RecyclerView recyclerView;
    private TextView total, aprobadas, rechazadas;
    private Spinner spinnerFiltroAdmin;
    private BarChart barChart;

    private UsuarioService service;
    private List<SolicitudCredito> listaCompleta = new ArrayList<>();
    private SolicitudAdapter adapter;

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
        spinnerFiltroAdmin = view.findViewById(R.id.spinnerFiltroAdmin);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        configurarSpinner();
        configurarRetrofit();
        cargarSolicitudes();
    }

    private void configurarSpinner() {
        String[] opciones = {"Todos", "Pendientes", "Aprobadas", "Rechazadas"};

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(requireContext(), R.layout.spinner_item, opciones);
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerFiltroAdmin.setAdapter(spinnerAdapter);

        spinnerFiltroAdmin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filtrarSolicitudes(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
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
                    listaCompleta = response.body();
                    filtrarSolicitudes(spinnerFiltroAdmin.getSelectedItemPosition());
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

    private void filtrarSolicitudes(int filtro) {
        if (listaCompleta == null) return;

        List<SolicitudCredito> filtradas = new ArrayList<>();
        int pendientesCount = 0, aprobadasCount = 0, rechazadasCount = 0;

        for (SolicitudCredito s : listaCompleta) {
            // Aplica filtro visual
            if (filtro == 0 || // Todos
                    (filtro == 1 && s.id_estatus == 1) ||
                    (filtro == 2 && s.id_estatus == 2) ||
                    (filtro == 3 && s.id_estatus == 3)) {
                filtradas.add(s);
                // Solo cuenta para la gráfica los filtrados
                if (s.id_estatus == 1) pendientesCount++;
                else if (s.id_estatus == 2) aprobadasCount++;
                else if (s.id_estatus == 3) rechazadasCount++;
            }
        }

        // Actualiza texto contadores (de acuerdo a filtro)
        total.setText("Total: " + filtradas.size());
        aprobadas.setText("Aprobadas: " + contarPorEstado(filtradas, 2));
        rechazadas.setText("Rechazadas: " + contarPorEstado(filtradas, 3));

        // Actualiza gráfico CON DATOS FILTRADOS
        actualizarGrafico(aprobadasCount, rechazadasCount, pendientesCount);

        // Actualiza RecyclerView
        adapter = new SolicitudAdapter(
                filtradas,
                true,
                this::cambiarEstatus,
                this::eliminarSolicitud,
                solicitud -> {}
        );
        recyclerView.setAdapter(adapter);
    }


    private void actualizarGrafico(int aprobadas, int rechazadas, int pendientes) {
        List<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0f, aprobadas));
        entries.add(new BarEntry(1f, rechazadas));
        entries.add(new BarEntry(2f, pendientes));

        BarDataSet dataSet = new BarDataSet(entries, "Solicitudes por estado");
        dataSet.setColors(
                Color.parseColor("#4CAF50"), // Verde
                Color.parseColor("#F44336"), // Rojo
                Color.parseColor("#FFC107")  // Amarillo
        );
        dataSet.setValueTextSize(14f);

        BarData barData = new BarData(dataSet);
        barChart.setData(barData);
        barChart.getDescription().setEnabled(false);
        barChart.getLegend().setEnabled(false);

        String[] labels = {"Aprobadas", "Rechazadas", "Pendientes"};
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(labels.length);
        xAxis.setDrawGridLines(false);

        barChart.getAxisRight().setEnabled(false);
        barChart.getAxisLeft().setGranularity(1f);
        barChart.getAxisLeft().setAxisMinimum(0f);

        barChart.animateY(1000);
        barChart.invalidate();
    }

    private int contarPorEstado(List<SolicitudCredito> lista, int estado) {
        int count = 0;
        for (SolicitudCredito s : lista) {
            if (s.id_estatus == estado) count++;
        }
        return count;
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
