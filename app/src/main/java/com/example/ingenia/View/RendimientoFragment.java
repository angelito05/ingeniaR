package com.example.ingenia.View;

import android.app.AlertDialog;
import android.app.Dialog;
import okhttp3.ResponseBody;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.*;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.*;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ingenia.Adapter.SolicitudAdapter;
import com.example.ingenia.Model.SolicitudCredito;
import com.example.ingenia.R;
import com.example.ingenia.api.ApiConfig;
import com.example.ingenia.api.UsuarioService;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.*;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.components.LegendEntry;

import java.util.*;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.*;
import retrofit2.converter.gson.GsonConverterFactory;

public class RendimientoFragment extends Fragment {

    private RecyclerView recyclerView;
    private int idUsuario;
    private PieChart pieChart;
    private Spinner spinnerFiltro;

    private List<SolicitudCredito> listaCompletaSolicitudes;

    private List<SolicitudCredito> solicitudesActuales = new ArrayList<>();
    private SolicitudAdapter adapter;

    private UsuarioService service;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_rendimiento, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerRendimiento);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        pieChart = view.findViewById(R.id.pieChart);
        configurarPieChart();
        spinnerFiltro = view.findViewById(R.id.spinnerFiltro);

        SharedPreferences prefs = requireActivity().getSharedPreferences("CrediGoPrefs", getContext().MODE_PRIVATE);
        idUsuario = prefs.getInt("id_usuario", -1);

        if (idUsuario == -1) {
            Toast.makeText(getContext(), "Usuario no válido", Toast.LENGTH_SHORT).show();
            return;
        }
        configurarSpinner();
        obtenerSolicitudesUsuario();
    }
    private void configurarSpinner() {
        String[] opciones = {"Todos", "Pendientes", "Aprobadas", "Rechazadas"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, opciones);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFiltro.setAdapter(adapter);

        spinnerFiltro.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filtrarSolicitudes(position); // ← Aplica el filtro al cambiar la opción
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void configurarPieChart() {
        pieChart.getDescription().setEnabled(false);
        pieChart.setUsePercentValues(true);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleRadius(60f);
        pieChart.setTransparentCircleRadius(65f);
        pieChart.setEntryLabelColor(android.graphics.Color.TRANSPARENT); // Oculta textos dentro
        pieChart.setCenterTextSize(24f);
        pieChart.getLegend().setEnabled(true);
        pieChart.getLegend().setTextSize(16f);
        pieChart.getLegend().setWordWrapEnabled(true);
        pieChart.setExtraOffsets(5, 10, 5, 15);
    }

    private void obtenerSolicitudesUsuario() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(logging).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        UsuarioService service = retrofit.create(UsuarioService.class);

        service.obtenerSolicitudesPorUsuario(idUsuario).enqueue(new Callback<List<SolicitudCredito>>() {
            @Override
            public void onResponse(Call<List<SolicitudCredito>> call, Response<List<SolicitudCredito>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<SolicitudCredito> solicitudes = response.body();

                    int pendientes = 0, aprobadas = 0, rechazadas = 0;

                    for (SolicitudCredito s : solicitudes) {
                        switch (s.id_estatus) {
                            case 1:
                                pendientes++;
                                break;
                            case 2:
                                aprobadas++;
                                break;
                            case 3:
                                rechazadas++;
                                break;
                        }
                    }

                    actualizarGrafico(pendientes, aprobadas, rechazadas);

                } else {
                    Toast.makeText(getContext(), "No se pudieron obtener las solicitudes", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<SolicitudCredito>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
    private void filtrarSolicitudes(int filtroSeleccionado) {
        if (listaCompletaSolicitudes == null) return;

        List<SolicitudCredito> filtradas = new ArrayList<>();
        int pendientes = 0, aprobadas = 0, rechazadas = 0;

        for (SolicitudCredito s : listaCompletaSolicitudes) {
            switch (s.id_estatus) {
                case 1: pendientes++; break;
                case 2: aprobadas++; break;
                case 3: rechazadas++; break;
            }

            if (filtroSeleccionado == 0 || // Todos
                    (filtroSeleccionado == 1 && s.id_estatus == 1) ||
                    (filtroSeleccionado == 2 && s.id_estatus == 2) ||
                    (filtroSeleccionado == 3 && s.id_estatus == 3)) {
                filtradas.add(s);
            }
        }

        actualizarGrafico(pendientes, aprobadas, rechazadas);
    }



    private void actualizarGrafico(int pendientes, int aprobadas, int rechazadas) {
        List<PieEntry> entries = new ArrayList<>();
        List<LegendEntry> legendEntries = new ArrayList<>();

        if (pendientes > 0) {
            entries.add(new PieEntry(pendientes, ""));
            LegendEntry le = new LegendEntry();
            le.formColor = android.graphics.Color.parseColor("#FFA726"); // naranja
            le.label = "Pendientes";
            legendEntries.add(le);
        }
        if (aprobadas > 0) {
            entries.add(new PieEntry(aprobadas, ""));
            LegendEntry le = new LegendEntry();
            le.formColor = android.graphics.Color.parseColor("#66BB6A"); // verde
            le.label = "Aprobadas";
            legendEntries.add(le);
        }
        if (rechazadas > 0) {
            entries.add(new PieEntry(rechazadas, ""));
            LegendEntry le = new LegendEntry();
            le.formColor = android.graphics.Color.parseColor("#EF5350"); // rojo
            le.label = "Rechazadas";
            legendEntries.add(le);
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setDrawValues(true);
        dataSet.setValueTextSize(18f);
        dataSet.setValueTextColor(android.graphics.Color.BLACK);

        // Colores para el gráfico
        List<Integer> colores = new ArrayList<>();
        for (LegendEntry le : legendEntries) {
            colores.add(le.formColor);
        }
        dataSet.setColors(colores);

        // Configura leyenda con entradas personalizadas
        pieChart.getLegend().setCustom(legendEntries);
        pieChart.getLegend().setForm(Legend.LegendForm.CIRCLE);
        pieChart.getLegend().setTextSize(16f);
        pieChart.getLegend().setXEntrySpace(10f);
        pieChart.getLegend().setYEntrySpace(5f);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter(pieChart));

        int total = pendientes + aprobadas + rechazadas;
        actualizarCentroPieChart(total);

        pieChart.setData(data);
        pieChart.animateY(1400, com.github.mikephil.charting.animation.Easing.EaseInOutQuad);
        pieChart.invalidate();
    }
    private void actualizarCentroPieChart(int total) {
        String titulo = "Solicitudes\n";
        String numero = String.valueOf(total);

        SpannableString s = new SpannableString(titulo + numero);

        // Estilo para "Solicitudes"
        s.setSpan(new RelativeSizeSpan(0.9f), 0, titulo.length(), 0);        // tamaño 1.3x
        s.setSpan(new StyleSpan(Typeface.BOLD), 0, titulo.length(), 0);       // negrita
        s.setSpan(new ForegroundColorSpan(Color.parseColor("#333333")), 0, titulo.length(), 0); // color gris oscuro

        // Estilo para el número
        int startNum = titulo.length();
        int endNum = s.length();
        s.setSpan(new RelativeSizeSpan(0.9f), startNum, endNum, 0);          // tamaño mucho más grande
        s.setSpan(new StyleSpan(Typeface.BOLD_ITALIC), startNum, endNum, 0); // negrita e itálica
        s.setSpan(new ForegroundColorSpan(Color.parseColor("#00796B")), startNum, endNum, 0); // color verde azulado

        pieChart.setCenterText(s);
    }

}
