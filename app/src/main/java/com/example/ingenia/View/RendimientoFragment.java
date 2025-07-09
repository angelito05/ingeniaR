package com.example.ingenia.View;

import android.app.AlertDialog;
import android.app.Dialog;
import okhttp3.ResponseBody;
import android.content.DialogInterface;
import android.content.SharedPreferences;
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
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.data.*;
import com.github.mikephil.charting.formatter.PercentFormatter;

import java.util.*;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.*;
import retrofit2.converter.gson.GsonConverterFactory;

public class RendimientoFragment extends Fragment {

    private RecyclerView recyclerView;
    private int idUsuario;
    private PieChart pieChart;

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

        SharedPreferences prefs = requireActivity().getSharedPreferences("CrediGoPrefs", getContext().MODE_PRIVATE);
        idUsuario = prefs.getInt("id_usuario", -1);

        if (idUsuario == -1) {
            Toast.makeText(getContext(), "Usuario no válido", Toast.LENGTH_SHORT).show();
            return;
        }

        // Configurar Retrofit y service solo 1 vez
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
        pieChart.setEntryLabelColor(Color.TRANSPARENT); // Oculta textos dentro
        pieChart.setCenterTextSize(24f);
        Legend legend = pieChart.getLegend();
        legend.setEnabled(true);
        legend.setTextSize(16f);
        legend.setWordWrapEnabled(true);
        pieChart.setExtraOffsets(5, 10, 5, 15);
    }

    private void obtenerSolicitudesUsuario() {
        service.obtenerSolicitudesPorUsuario(idUsuario).enqueue(new Callback<List<SolicitudCredito>>() {
            @Override
            public void onResponse(Call<List<SolicitudCredito>> call, Response<List<SolicitudCredito>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    solicitudesActuales = response.body();

                    actualizarVista();

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

    private void actualizarVista() {
        // Contar estatus para gráfico
        int pendientes = 0, aprobadas = 0, rechazadas = 0;
        for (SolicitudCredito s : solicitudesActuales) {
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

        // Configurar Adapter con listener para editar / eliminar
        adapter = new SolicitudAdapter(
                solicitudesActuales,
                false,
                (id, nuevoEstatus) -> { /* No usado para trabajador */ },
                id -> { /* No usado para trabajador */ },
                this::mostrarDialogEditarEliminar // al hacer clic en la card
        );
        recyclerView.setAdapter(adapter);
    }

    private void mostrarDialogEditarEliminar(SolicitudCredito solicitud) {
        // Crear y mostrar diálogo personalizado para editar o eliminar

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

    // Llamada API para editar solicitud
    private void editarSolicitudApi(SolicitudCredito solicitudEditada) {
        service.editarSolicitud(solicitudEditada.id_solicitud, solicitudEditada).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Solicitud actualizada", Toast.LENGTH_SHORT).show();
                    // Actualizar lista local con la solicitud editada (no recibimos objeto actualizado)
                    for (int i = 0; i < solicitudesActuales.size(); i++) {
                        if (solicitudesActuales.get(i).id_solicitud == solicitudEditada.id_solicitud) {
                            solicitudesActuales.set(i, solicitudEditada);
                            break;
                        }
                    }
                    actualizarVista();
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


    // Llamada API para eliminar solicitud
    private void eliminarSolicitudApi(int idSolicitud) {
        service.eliminarSolicitud(idSolicitud).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Solicitud eliminada", Toast.LENGTH_SHORT).show();
                    // Eliminar de lista local
                    Iterator<SolicitudCredito> iter = solicitudesActuales.iterator();
                    while (iter.hasNext()) {
                        if (iter.next().id_solicitud == idSolicitud) {
                            iter.remove();
                            break;
                        }
                    }
                    actualizarVista();
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


    private void actualizarGrafico(int pendientes, int aprobadas, int rechazadas) {
        List<PieEntry> entries = new ArrayList<>();
        List<LegendEntry> legendEntries = new ArrayList<>();

        if (pendientes > 0) {
            entries.add(new PieEntry(pendientes, ""));
            LegendEntry le = new LegendEntry();
            le.formColor = Color.parseColor("#FFA726"); // naranja
            le.label = "Pendientes";
            legendEntries.add(le);
        }
        if (aprobadas > 0) {
            entries.add(new PieEntry(aprobadas, ""));
            LegendEntry le = new LegendEntry();
            le.formColor = Color.parseColor("#66BB6A"); // verde
            le.label = "Aprobadas";
            legendEntries.add(le);
        }
        if (rechazadas > 0) {
            entries.add(new PieEntry(rechazadas, ""));
            LegendEntry le = new LegendEntry();
            le.formColor = Color.parseColor("#EF5350"); // rojo
            le.label = "Rechazadas";
            legendEntries.add(le);
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setDrawValues(true);
        dataSet.setValueTextSize(18f);
        dataSet.setValueTextColor(Color.BLACK);

        List<Integer> colores = new ArrayList<>();
        for (LegendEntry le : legendEntries) {
            colores.add(le.formColor);
        }
        dataSet.setColors(colores);

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

        int startNum = titulo.length();
        int endNum = s.length();
        s.setSpan(new RelativeSizeSpan(0.9f), startNum, endNum, 0);
        s.setSpan(new StyleSpan(Typeface.BOLD_ITALIC), startNum, endNum, 0);
        s.setSpan(new ForegroundColorSpan(Color.parseColor("#00796B")), startNum, endNum, 0);

        pieChart.setCenterText(s);
    }

    // DialogFragment para editar o eliminar solicitud
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
            LayoutInflater inflater = requireActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.dialog_editar_solicitud, null);

            // Accede a los campos del layout
            final EditText etPlazo = view.findViewById(R.id.etPlazo);
            final EditText etMonto = view.findViewById(R.id.etMonto);
            final EditText etMotivo = view.findViewById(R.id.etMotivo);

            // Poner los valores actuales
            etPlazo.setText(String.valueOf(solicitud.plazo_meses));
            etMonto.setText(String.valueOf(solicitud.monto_solicitado));
            etMotivo.setText(solicitud.motivo);

            builder.setView(view)
                    .setTitle("Editar Solicitud")
                    .setPositiveButton("Guardar", (dialog, which) -> {
                        String plazoTexto = etPlazo.getText().toString().trim();
                        String montoTexto = etMonto.getText().toString().trim();
                        String motivoTexto = etMotivo.getText().toString().trim();

                        if (plazoTexto.isEmpty() || montoTexto.isEmpty() || motivoTexto.isEmpty()) {
                            Toast.makeText(getContext(), "Completa todos los campos", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        try {
                            int plazoNuevo = Integer.parseInt(plazoTexto);
                            double montoNuevo = Double.parseDouble(montoTexto);

                            solicitud.plazo_meses = plazoNuevo;
                            solicitud.monto_solicitado = montoNuevo;
                            solicitud.motivo = motivoTexto;

                            if (listener != null) {
                                listener.onEditar(solicitud);
                            }
                        } catch (NumberFormatException e) {
                            Toast.makeText(getContext(), "Formato inválido", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Cancelar", null)
                    .setNeutralButton("Eliminar", (dialog, which) -> {
                        if (listener != null) {
                            listener.onEliminar(solicitud.id_solicitud);
                        }
                    });

            return builder.create();
        }

    }
}
