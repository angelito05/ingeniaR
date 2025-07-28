package com.example.ingenia.View;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.*;
import android.widget.*;
import android.text.TextWatcher;
import android.text.Editable;

import com.google.android.material.chip.ChipGroup;


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
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.data.*;

import com.github.mikephil.charting.formatter.PercentFormatter;

import java.util.*;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.*;
import retrofit2.converter.gson.GsonConverterFactory;

public class RendimientoFragment extends Fragment {

    private RecyclerView recyclerView;
    private int idUsuario;
    private PieChart pieChart;
    private Spinner spinnerFiltro;
    private ChipGroup chipGroupFiltro;
    private TextView kpiTotalSolicitudes;
    private TextView kpiMontoAprobado;



    private List<SolicitudCredito> listaCompletaSolicitudes;
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

        kpiTotalSolicitudes = view.findViewById(R.id.kpiTotalSolicitudes);
        kpiMontoAprobado = view.findViewById(R.id.kpiMontoAprobado);



        chipGroupFiltro = view.findViewById(R.id.chipGroupFiltro);
        chipGroupFiltro.setOnCheckedChangeListener((group, checkedId) -> {
            int filtro = 0;
            if (checkedId == R.id.chipPendientes) filtro = 1;
            else if (checkedId == R.id.chipAprobadas) filtro = 2;
            else if (checkedId == R.id.chipRechazadas) filtro = 3;
            filtrarSolicitudes(filtro);
        });



        SharedPreferences prefs = requireActivity().getSharedPreferences("CrediGoPrefs", getContext().MODE_PRIVATE);
        idUsuario = prefs.getInt("id_usuario", -1);

        if (idUsuario == -1) {
            Toast.makeText(getContext(), "Usuario no válido", Toast.LENGTH_SHORT).show();
            return;
        }

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(logging).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        service = retrofit.create(UsuarioService.class);

        obtenerSolicitudesUsuario();
    }



    private void configurarPieChart() {
        pieChart.getDescription().setEnabled(false);
        pieChart.setUsePercentValues(true);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleRadius(60f);
        pieChart.setTransparentCircleRadius(65f);
        pieChart.setEntryLabelColor(Color.TRANSPARENT);
        pieChart.setCenterTextSize(24f);
        pieChart.getLegend().setEnabled(true);
        pieChart.getLegend().setTextSize(16f);
        pieChart.getLegend().setWordWrapEnabled(true);
        pieChart.setExtraOffsets(5, 10, 5, 15);
    }

    private void obtenerSolicitudesUsuario() {
        service.obtenerSolicitudesPorUsuario(idUsuario).enqueue(new Callback<List<SolicitudCredito>>() {
            @Override
            public void onResponse(Call<List<SolicitudCredito>> call, Response<List<SolicitudCredito>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listaCompletaSolicitudes = response.body();

                    adapter = new SolicitudAdapter(
                            new ArrayList<>(listaCompletaSolicitudes),
                            false,
                            (id, estatus) -> {},
                            id -> {},
                            RendimientoFragment.this::mostrarDialogEditarEliminar
                    );
                    recyclerView.setAdapter(adapter);

                    filtrarSolicitudes(0); // por defecto: mostrar todos

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
    private int obtenerFiltroSeleccionado() {
        int checkedId = chipGroupFiltro.getCheckedChipId();
        if (checkedId == R.id.chipPendientes) return 1;
        else if (checkedId == R.id.chipAprobadas) return 2;
        else if (checkedId == R.id.chipRechazadas) return 3;
        return 0; // Todos
    }



    private void filtrarSolicitudes(int filtroSeleccionado) {
        if (listaCompletaSolicitudes == null) return;

        List<SolicitudCredito> filtradas = new ArrayList<>();
        int pendientes = 0, aprobadas = 0, rechazadas = 0;
        double montoAprobadoTotal = 0;

        for (SolicitudCredito s : listaCompletaSolicitudes) {
            if (filtroSeleccionado == 0 ||
                    (filtroSeleccionado == 1 && s.id_estatus == 1) ||
                    (filtroSeleccionado == 2 && s.id_estatus == 2) ||
                    (filtroSeleccionado == 3 && s.id_estatus == 3)) {

                filtradas.add(s);

                // Contar para gráfico:
                switch (s.id_estatus) {
                    case 1: pendientes++; break;
                    case 2:
                        aprobadas++;
                        montoAprobadoTotal += s.monto_solicitado;  // sumar monto aprobado
                        break;
                    case 3: rechazadas++; break;
                }
            }
        }

        if (adapter != null) {
            adapter.actualizarLista(filtradas);
        }

        actualizarGrafico(pendientes, aprobadas, rechazadas);

        // Actualizar KPIs
        actualizarKPIs(filtradas.size(), montoAprobadoTotal);
    }

    private void actualizarKPIs(int totalSolicitudes, double montoAprobado) {
        if (kpiTotalSolicitudes != null) {
            kpiTotalSolicitudes.setText("Total: " + totalSolicitudes);
        }
        if (kpiMontoAprobado != null) {
            kpiMontoAprobado.setText(String.format(Locale.getDefault(), "Monto Aprobado: $%.2f", montoAprobado));
        }
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

        s.setSpan(new RelativeSizeSpan(0.9f), 0, titulo.length(), 0);
        s.setSpan(new StyleSpan(Typeface.BOLD), 0, titulo.length(), 0);
        s.setSpan(new ForegroundColorSpan(Color.parseColor("#333333")), 0, titulo.length(), 0);

        s.setSpan(new RelativeSizeSpan(0.9f), titulo.length(), s.length(), 0);
        s.setSpan(new StyleSpan(Typeface.BOLD_ITALIC), titulo.length(), s.length(), 0);
        s.setSpan(new ForegroundColorSpan(Color.parseColor("#00796B")), titulo.length(), s.length(), 0);

        pieChart.setCenterText(s);
    }

    private void mostrarDialogEditarEliminar(SolicitudCredito solicitud) {
        EditarEliminarDialog dialog = EditarEliminarDialog.newInstance(solicitud);
        dialog.setListener(new EditarEliminarDialog.OnEditarEliminarListener() {
            @Override
            public void onEditar(SolicitudCredito solicitudEditada) {
                editarSolicitudApi(solicitudEditada);
            }

            @Override
            public void onEliminar(int idSolicitud) {
                eliminarSolicitudApi(idSolicitud);
            }
        });
        dialog.show(getParentFragmentManager(), "EditarEliminarDialog");
    }

    private void editarSolicitudApi(SolicitudCredito solicitudEditada) {
        service.editarSolicitud(solicitudEditada.id_solicitud, solicitudEditada).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.isSuccessful()) {
                    for (int i = 0; i < listaCompletaSolicitudes.size(); i++) {
                        if (listaCompletaSolicitudes.get(i).id_solicitud == solicitudEditada.id_solicitud) {
                            listaCompletaSolicitudes.set(i, solicitudEditada);
                            break;
                        }
                    }
                    filtrarSolicitudes(obtenerFiltroSeleccionado());

                    Toast.makeText(getContext(), "Solicitud actualizada", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Error al actualizar solicitud", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void eliminarSolicitudApi(int idSolicitud) {
        service.eliminarSolicitud(idSolicitud).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Iterator<SolicitudCredito> iter = listaCompletaSolicitudes.iterator();
                    while (iter.hasNext()) {
                        if (iter.next().id_solicitud == idSolicitud) {
                            iter.remove();
                            break;
                        }
                    }
                    filtrarSolicitudes(obtenerFiltroSeleccionado());

                    Toast.makeText(getContext(), "Solicitud eliminada", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Error al eliminar solicitud", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public static class EditarEliminarDialog extends DialogFragment {
        private SolicitudCredito solicitud;
        private OnEditarEliminarListener listener;

        public interface OnEditarEliminarListener {
            void onEditar(SolicitudCredito solicitudEditada);
            void onEliminar(int idSolicitud);
        }

        public void setListener(OnEditarEliminarListener listener) {
            this.listener = listener;
        }

        public static EditarEliminarDialog newInstance(SolicitudCredito solicitud) {
            EditarEliminarDialog dialog = new EditarEliminarDialog();
            Bundle args = new Bundle();
            args.putSerializable("solicitud", solicitud);
            dialog.setArguments(args);
            return dialog;
        }

        private void actualizarPagoMensual(TextView tvPagoMensual, double tasa, int plazo, double monto) {
            if (plazo <= 0 || monto <= 0) {
                tvPagoMensual.setText("Pago mensual estimado: $0.00");
                return;
            }
            double tasaMensual = tasa / 100.0 / 12.0;
            double pago;
            if (tasaMensual > 0) {
                pago = (monto * tasaMensual) / (1 - Math.pow(1 + tasaMensual, -plazo));
            } else {
                pago = monto / plazo;
            }
            tvPagoMensual.setText(String.format(Locale.getDefault(), "Pago mensual estimado: $%.2f", pago));
        }


        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if (getArguments() != null) {
                solicitud = (SolicitudCredito) getArguments().getSerializable("solicitud");
            }
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            View view = requireActivity().getLayoutInflater().inflate(R.layout.dialog_editar_solicitud, null);

            Spinner spinnerPlazo = view.findViewById(R.id.spinnerPlazo);
            Spinner spinnerTasa = view.findViewById(R.id.spinnerTasaInteres);
            EditText etMonto = view.findViewById(R.id.etMonto);
            EditText etMotivo = view.findViewById(R.id.etMotivo);
            EditText etObservaciones = view.findViewById(R.id.etObservaciones);
            TextView tvPagoMensual = view.findViewById(R.id.tvPagoMensual);

            String[] opcionesPlazo = {"6", "12", "18", "24", "48"};
            String[] opcionesTasa = {"10.0", "15.0", "20.0", "25.0"};

            ArrayAdapter<String> adapterPlazo = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, opcionesPlazo);
            adapterPlazo.setDropDownViewResource(R.layout.spinner_dropdown_item);
            spinnerPlazo.setAdapter(adapterPlazo);

            ArrayAdapter<String> adapterTasa = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, opcionesTasa);
            adapterTasa.setDropDownViewResource(R.layout.spinner_dropdown_item);
            spinnerTasa.setAdapter(adapterTasa);

            // Establecer valores actuales
            spinnerPlazo.setSelection(Arrays.asList(opcionesPlazo).indexOf(String.valueOf(solicitud.plazo_meses)));
            spinnerTasa.setSelection(Arrays.asList(opcionesTasa).indexOf(String.valueOf(solicitud.tasa_interes)));
            etMonto.setText(String.valueOf(solicitud.monto_solicitado));
            etMotivo.setText(solicitud.motivo);
            etObservaciones.setText(solicitud.observaciones);

            AdapterView.OnItemSelectedListener listenerSpinner = new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    actualizarPago();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {}

                private void actualizarPago() {
                    double tasa = Double.parseDouble(spinnerTasa.getSelectedItem().toString());
                    int plazo = Integer.parseInt(spinnerPlazo.getSelectedItem().toString());
                    double monto;
                    try { monto = Double.parseDouble(etMonto.getText().toString()); } catch (NumberFormatException e) { monto = 0; }
                    actualizarPagoMensual(tvPagoMensual, tasa, plazo, monto);
                }
            };

            spinnerPlazo.setOnItemSelectedListener(listenerSpinner);
            spinnerTasa.setOnItemSelectedListener(listenerSpinner);

            etMonto.addTextChangedListener(new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                    listenerSpinner.onItemSelected(null, null, 0, 0);
                }
                @Override public void afterTextChanged(Editable s) {}
            });

            builder.setView(view)
                    .setTitle("Editar Solicitud")
                    .setPositiveButton("Guardar", (dialog, which) -> {
                        try {
                            solicitud.plazo_meses = Integer.parseInt(spinnerPlazo.getSelectedItem().toString());
                            solicitud.tasa_interes = Double.parseDouble(spinnerTasa.getSelectedItem().toString());
                            solicitud.monto_solicitado = Double.parseDouble(etMonto.getText().toString().trim());
                            solicitud.motivo = etMotivo.getText().toString().trim();
                            solicitud.observaciones = etObservaciones.getText().toString().trim();

                            double tasaMensual = solicitud.tasa_interes / 100.0 / 12.0;
                            if (tasaMensual > 0) {
                                solicitud.pago_mensual_estimado = (solicitud.monto_solicitado * tasaMensual) /
                                        (1 - Math.pow(1 + tasaMensual, -solicitud.plazo_meses));
                            } else {
                                solicitud.pago_mensual_estimado = solicitud.monto_solicitado / solicitud.plazo_meses;
                            }

                            if (listener != null) listener.onEditar(solicitud);
                        } catch (Exception e) {
                            Toast.makeText(getContext(), "Datos inválidos", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Cancelar", null)
                    .setNeutralButton("Eliminar", (dialog, which) -> {
                        if (listener != null) listener.onEliminar(solicitud.id_solicitud);
                    });

            return builder.create();
        }
    }
}
