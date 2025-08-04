package com.credigo.ingenia.View;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.core.content.ContextCompat;


import com.credigo.ingenia.Adapter.SolicitudAdapter;
import com.credigo.ingenia.Adapter.MunicipioAdapter;
import com.credigo.ingenia.Adapter.BitacoraAdapter;
import com.credigo.ingenia.Model.CambiarEstatusRequest;
import com.credigo.ingenia.Model.BitacoraDTO;
import com.credigo.ingenia.Model.SolicitudCredito;
import com.credigo.ingenia.R;
import com.credigo.ingenia.api.ApiConfig;
import com.credigo.ingenia.api.UsuarioService;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.text.ParseException;
import java.util.Collections;

import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SolicitudesFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView total, aprobadas, rechazadas;
    private BarChart barChart, barChartMunicipios;

    private ChipGroup chipGroupFiltro;
    private SwipeRefreshLayout swipeRefresh;
    private SearchView searchView;
    private TextView kpiTasaAprobacion, kpiMontoTotal, kpiTotalPromotores, kpiMejorPromotor;
    private String busquedaActual = "";

    private UsuarioService service;
    private List<SolicitudCredito> listaCompleta = new ArrayList<>();
    private SolicitudAdapter adapter;
    private RecyclerView recyclerMunicipios;
    private RecyclerView recyclerBitacora;
    private BitacoraAdapter bitacoraAdapter;



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



        chipGroupFiltro = view.findViewById(R.id.chipGroupFiltro);
        searchView = view.findViewById(R.id.searchView);
        swipeRefresh = view.findViewById(R.id.swipeRefresh);
        kpiTasaAprobacion = view.findViewById(R.id.kpiTasaAprobacion);
        kpiMontoTotal = view.findViewById(R.id.kpiMontoTotal);

        kpiMejorPromotor = view.findViewById(R.id.kpiMejorPromotor);
        recyclerBitacora = view.findViewById(R.id.recyclerBitacora);
        recyclerBitacora.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        bitacoraAdapter = new BitacoraAdapter(new ArrayList<>());
        recyclerBitacora.setAdapter(bitacoraAdapter);


        recyclerMunicipios = view.findViewById(R.id.recyclerMunicipios);
        searchView.setQueryHint("Buscar por nombre o promotor");

        // Forzar el color del hint, porque a veces es blanco o transparente
        int hintColor = ContextCompat.getColor(requireContext(), R.color.text_secondary); // o el color que uses
        TextView searchText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        searchText.setHintTextColor(hintColor);

        recyclerMunicipios.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false)
        );


        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        configurarRetrofit();
        chipGroupFiltro.check(R.id.chipTodos);

        chipGroupFiltro.setOnCheckedChangeListener((group, checkedId) -> {
            filtrarSolicitudes(getFiltroActual(), busquedaActual);
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                busquedaActual = query;
                filtrarSolicitudes(getFiltroActual(), busquedaActual);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                busquedaActual = newText;
                filtrarSolicitudes(getFiltroActual(), busquedaActual);
                return true;
            }
        });

        swipeRefresh.setOnRefreshListener(this::cargarSolicitudes);
        cargarSolicitudes();
        cargarBitacora();
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
        if (!isAdded()) return;
        swipeRefresh.setRefreshing(true);

        service.obtenerTodasSolicitudes().enqueue(new Callback<List<SolicitudCredito>>() {
            @Override
            public void onResponse(Call<List<SolicitudCredito>> call, Response<List<SolicitudCredito>> response) {
                if (!isAdded()) return;

                swipeRefresh.setRefreshing(false);

                if (response.isSuccessful() && response.body() != null) {
                    listaCompleta = response.body();
                    filtrarSolicitudes(getFiltroActual(), busquedaActual);
                } else {
                    Toast.makeText(getContext(), "Error al obtener solicitudes", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<SolicitudCredito>> call, Throwable t) {
                if (!isAdded()) return;
                swipeRefresh.setRefreshing(false);
                Toast.makeText(getContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void filtrarSolicitudes(int filtro, String query) {
        if (listaCompleta == null) return;

        List<SolicitudCredito> filtradas = new ArrayList<>();
        int pendientesCount = 0, aprobadasCount = 0, rechazadasCount = 0;
        double montoTotal = 0, montoAprobado = 0;
        Map<String, Integer> porMunicipio = new HashMap<>();
        Map<String, Integer> porPromotor = new HashMap<>();


        String q = query == null ? "" : query.toLowerCase();

        for (SolicitudCredito s : listaCompleta) {
            boolean coincideFiltro = (filtro == 0 ||
                    (filtro == 1 && s.id_estatus == 1) ||
                    (filtro == 2 && s.id_estatus == 2) ||
                    (filtro == 3 && s.id_estatus == 3));

            String nombre = s.nombreCliente == null ? "" : s.nombreCliente.toLowerCase();

            // Promotor: limpio espacios, uso "Desconocido" si es null o vacío
            String promotor = (s.nombreUsuario != null && !s.nombreUsuario.trim().isEmpty())
                    ? s.nombreUsuario.trim()
                    : "Desconocido";

            boolean coincideBusqueda = q.isEmpty()
                    || nombre.contains(q)
                    || promotor.toLowerCase().contains(q);

            if (coincideFiltro && coincideBusqueda) {
                filtradas.add(s);

                if (s.id_estatus == 1) pendientesCount++;
                else if (s.id_estatus == 2) aprobadasCount++;
                else if (s.id_estatus == 3) rechazadasCount++;

                montoTotal += s.monto_solicitado;
                if (s.id_estatus == 2) montoAprobado += s.monto_solicitado;

                porMunicipio.put(s.ciudadCliente, porMunicipio.getOrDefault(s.ciudadCliente, 0) + 1);
                porPromotor.put(promotor, porPromotor.getOrDefault(promotor, 0) + 1);
            }
        }

        total.setText("Total: " + filtradas.size());
        this.aprobadas.setText("Aprobadas: " + contarPorEstado(filtradas, 2));
        this.rechazadas.setText("Rechazadas: " + contarPorEstado(filtradas, 3));
        actualizarGrafico(aprobadasCount, rechazadasCount, pendientesCount);

        double tasa = montoTotal > 0 ? (montoAprobado / montoTotal) * 100 : 0;
        kpiTasaAprobacion.setText(String.format("Tasa: %.1f%%", tasa));
        kpiMontoTotal.setText(String.format("Monto: $%.2f", montoTotal));

        // KPI Total promotores


        // KPI Mejor promotor con cantidad
        String mejorPromotor = "-";
        int maxSolicitudes = 0;
        for (Map.Entry<String, Integer> entry : porPromotor.entrySet()) {
            if (entry.getValue() > maxSolicitudes) {
                maxSolicitudes = entry.getValue();
                mejorPromotor = entry.getKey();
            }
        }

        if (mejorPromotor.equals("-")) {
            kpiMejorPromotor.setText("MEJOR PROMOTOR: -");
        } else {
            kpiMejorPromotor.setText("MEJOR PROMOTOR: " + mejorPromotor + " (" + maxSolicitudes + ")  Solicitudes activas");
        }





        adapter = new SolicitudAdapter(
                filtradas,
                true,
                this::cambiarEstatus,
                this::eliminarSolicitud,
                solicitud -> {}
        );
        recyclerView.setAdapter(adapter);
        recyclerMunicipios.setAdapter(new MunicipioAdapter(porMunicipio));

    }


    private int getFiltroActual() {
        int checkedId = chipGroupFiltro.getCheckedChipId();
        if (checkedId == R.id.chipPendientes) return 1;
        else if (checkedId == R.id.chipAprobadas) return 2;
        else if (checkedId == R.id.chipRechazadas) return 3;
        return 0; // Para el chip "Todos" o ninguno seleccionado
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


    private void actualizarGraficoMunicipios(Map<String, Integer> datos) {
        List<BarEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        int index = 0;

        for (Map.Entry<String, Integer> entry : datos.entrySet()) {
            entries.add(new BarEntry(index, entry.getValue()));
            labels.add(entry.getKey());
            index++;
        }

        BarDataSet dataSet = new BarDataSet(entries, "Solicitudes por municipio");
        dataSet.setColor(Color.parseColor("#3F51B5"));
        dataSet.setValueTextSize(12f);

        BarData data = new BarData(dataSet);
        barChartMunicipios.setData(data);
        barChartMunicipios.getDescription().setEnabled(false);
        barChartMunicipios.getLegend().setEnabled(false);

        XAxis xAxis = barChartMunicipios.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(labels.size());
        xAxis.setLabelRotationAngle(-45);
        xAxis.setDrawGridLines(false);

        barChartMunicipios.getAxisRight().setEnabled(false);
        barChartMunicipios.getAxisLeft().setGranularity(1f);
        barChartMunicipios.getAxisLeft().setAxisMinimum(0f);

        barChartMunicipios.animateY(1000);
        barChartMunicipios.invalidate();
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
    private void ordenarBitacorasPorFecha(List<BitacoraDTO> bitacoras) {
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Collections.sort(bitacoras, (b1, b2) -> {
            try {
                Date fecha1 = sdf.parse(b1.getFecha());
                Date fecha2 = sdf.parse(b2.getFecha());
                // Orden descendente: más recientes primero
                return fecha2.compareTo(fecha1);
            } catch (ParseException e) {
                e.printStackTrace();
                return 0;
            }
        });
    }


    private void cargarBitacora() {
        if (!isAdded()) return;

        // Pide 10 y luego mostramos 3
        service.getUltimosConDetalles(10).enqueue(new Callback<List<BitacoraDTO>>() {
            @Override
            public void onResponse(Call<List<BitacoraDTO>> call, Response<List<BitacoraDTO>> response) {
                if (!isAdded()) return;

                if (response.isSuccessful() && response.body() != null) {
                    List<BitacoraDTO> bitacoras = response.body();

                    // Ordena la lista primero:
                    ordenarBitacorasPorFecha(bitacoras);


                    bitacoraAdapter.actualizarLista(bitacoras);

                } else {
                    Toast.makeText(getContext(), "Error al obtener movimientos", Toast.LENGTH_SHORT).show();
                }
            }


            @Override
            public void onFailure(Call<List<BitacoraDTO>> call, Throwable t) {
                if (!isAdded()) return;
                Toast.makeText(getContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

}
