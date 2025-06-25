package com.example.ingenia.View;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ingenia.Model.Solicitud;
import com.example.ingenia.Model.SolicitudRepository;
import com.example.ingenia.Adapter.SolicitudAdapter;
import com.example.ingenia.R;

import java.util.ArrayList;
import java.util.List;

public class RendimientoFragment extends Fragment {

    private RecyclerView recyclerView;

    public RendimientoFragment() {
        // Constructor vacío
    }

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

        // Simulación: nombre del trabajador logueado (puedes usar SharedPreferences luego)
        String trabajadorActual = "Ronald Leyva";

        // Datos de prueba
        List<Solicitud> todas = SolicitudRepository.listaSolicitudes;

        // Filtrar solo las del trabajador actual
        List<Solicitud> propias = new ArrayList<>();
        for (Solicitud s : todas) {
            if (s.trabajador.equals(trabajadorActual)) {
                propias.add(s);
            }
        }

        // Mostrar sin botones
        recyclerView.setAdapter(new SolicitudAdapter(requireContext(), propias, false));
    }
}
